// Unit tests for the hand-written AI concierge client.
// These do NOT touch FlutterFlow-generated code or Firebase; they exercise the
// pure JSON parsing logic so they run fast and reliably in CI.
import 'dart:convert';

import 'package:flutter_test/flutter_test.dart';
import 'package:rakcha_mobile/concierge/concierge_client.dart';

void main() {
  group('ConciergeClient.parseRecommendations', () {
    test('parses Firebase callable {"result": {...}} envelope', () {
      final body = jsonEncode({
        'result': {
          'recommendations': [
            {
              'title': 'Dune: Part Two',
              'reason': 'Epic sci-fi matching your love of space opera.',
              'genres': ['Sci-Fi', 'Adventure'],
            },
            {'title': 'Whiplash', 'reason': 'Intense character drama.'},
          ],
        },
      });

      final recs = ConciergeClient.parseRecommendations(body);

      expect(recs, hasLength(2));
      expect(recs.first.title, 'Dune: Part Two');
      expect(recs.first.genres, ['Sci-Fi', 'Adventure']);
      expect(recs[1].genres, isEmpty);
    });

    test('handles a bare (non-enveloped) result object', () {
      final body = jsonEncode({
        'recommendations': [
          {'title': 'Parasite', 'reason': 'Genre-bending thriller.'},
        ],
      });

      final recs = ConciergeClient.parseRecommendations(body);
      expect(recs, hasLength(1));
      expect(recs.single.title, 'Parasite');
    });

    test('returns empty list when no recommendations present', () {
      final recs = ConciergeClient.parseRecommendations('{"result": {}}');
      expect(recs, isEmpty);
    });

    test('throws on a non-object body', () {
      expect(
        () => ConciergeClient.parseRecommendations('[]'),
        throwsA(isA<ConciergeException>()),
      );
    });
  });
}
