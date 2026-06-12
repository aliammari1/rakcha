/// Hand-written client for the RAKCHA AI cinema concierge.
///
/// This is NOT FlutterFlow-generated code — it is hand-maintained and covered
/// by unit tests (see test/concierge_client_test.dart). It calls the
/// `cinemaConcierge` Firebase callable (LangGraph + @langchain/anthropic,
/// model claude-haiku-4-5) over HTTPS and parses the recommendations.
///
/// The three RAKCHA apps are independent (no shared REST API); the mobile and
/// web clients each reach the same callable directly via fetch/HTTP.
import 'dart:convert';

import 'package:http/http.dart' as http;

/// A single film recommendation returned by the concierge.
class FilmRecommendation {
  const FilmRecommendation({
    required this.title,
    required this.reason,
    this.genres = const <String>[],
  });

  final String title;
  final String reason;
  final List<String> genres;

  factory FilmRecommendation.fromJson(Map<String, dynamic> json) {
    return FilmRecommendation(
      title: (json['title'] ?? '').toString(),
      reason: (json['reason'] ?? '').toString(),
      genres: (json['genres'] as List<dynamic>? ?? <dynamic>[])
          .map((dynamic g) => g.toString())
          .toList(),
    );
  }
}

class ConciergeException implements Exception {
  ConciergeException(this.message);
  final String message;
  @override
  String toString() => 'ConciergeException: $message';
}

class ConciergeClient {
  ConciergeClient({required this.callableUrl, http.Client? httpClient})
    : _http = httpClient ?? http.Client();

  /// HTTPS URL of the deployed `cinemaConcierge` callable, e.g.
  /// https://us-central1-rakcha-hn94a9.cloudfunctions.net/cinemaConcierge
  final String callableUrl;
  final http.Client _http;

  /// Ask the concierge for film recommendations given a free-text [prompt]
  /// and optional [favoriteGenres] to steer the model.
  Future<List<FilmRecommendation>> recommend(
    String prompt, {
    List<String> favoriteGenres = const <String>[],
  }) async {
    final response = await _http.post(
      Uri.parse(callableUrl),
      headers: const <String, String>{'Content-Type': 'application/json'},
      body: jsonEncode(<String, dynamic>{
        // Firebase callable convention: payload nested under "data".
        'data': <String, dynamic>{
          'prompt': prompt,
          'favoriteGenres': favoriteGenres,
        },
      }),
    );

    if (response.statusCode != 200) {
      throw ConciergeException(
        'Concierge call failed (HTTP ${response.statusCode})',
      );
    }

    return parseRecommendations(response.body);
  }

  /// Parses a callable response body into recommendations. Exposed for tests.
  static List<FilmRecommendation> parseRecommendations(String body) {
    final dynamic decoded = jsonDecode(body);
    if (decoded is! Map<String, dynamic>) {
      throw ConciergeException('Unexpected response shape');
    }
    // Firebase callables wrap the function's return value in {"result": ...}.
    final dynamic result = decoded['result'] ?? decoded;
    final dynamic recs = result is Map<String, dynamic>
        ? result['recommendations']
        : null;
    if (recs is! List<dynamic>) {
      return const <FilmRecommendation>[];
    }
    return recs
        .whereType<Map<String, dynamic>>()
        .map(FilmRecommendation.fromJson)
        .toList();
  }
}
