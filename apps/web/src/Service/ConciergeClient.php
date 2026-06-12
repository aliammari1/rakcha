<?php

namespace App\Service;

use Symfony\Contracts\HttpClient\HttpClientInterface;

/**
 * Thin client for the RAKCHA AI cinema concierge.
 *
 * The three RAKCHA apps are independent — there is NO shared REST API. The
 * Symfony web app reaches the same `cinemaConcierge` Firebase callable
 * (LangGraph + @langchain/anthropic, model claude-haiku-4-5) directly over
 * HTTPS, exactly like the FlutterFlow mobile app does.
 *
 * Configure the callable URL via the CONCIERGE_CALLABLE_URL env var, e.g.
 *   https://us-central1-rakcha-hn94a9.cloudfunctions.net/cinemaConcierge
 */
class ConciergeClient
{
    public function __construct(
        private readonly HttpClientInterface $httpClient,
        private readonly string $callableUrl,
    ) {
    }

    /**
     * Ask the concierge for film recommendations.
     *
     * @param string   $prompt         free-text request from the viewer
     * @param string[] $favoriteGenres optional genres to steer the model
     *
     * @return array<int, array{title: string, reason: string, genres: string[]}>
     */
    public function recommend(string $prompt, array $favoriteGenres = []): array
    {
        if ('' === trim($prompt)) {
            return [];
        }

        $response = $this->httpClient->request('POST', $this->callableUrl, [
            'json' => [
                // Firebase callable convention: payload nested under "data".
                'data' => [
                    'prompt' => $prompt,
                    'favoriteGenres' => array_values($favoriteGenres),
                ],
            ],
            'timeout' => 20,
        ]);

        $payload = $response->toArray(false);

        return self::parseRecommendations($payload);
    }

    /**
     * Normalizes a callable response payload into a recommendations list.
     *
     * @param array<string, mixed> $payload
     *
     * @return array<int, array{title: string, reason: string, genres: string[]}>
     */
    public static function parseRecommendations(array $payload): array
    {
        // Firebase callables wrap the function return value in {"result": ...}.
        $result = $payload['result'] ?? $payload;
        $recommendations = \is_array($result) ? ($result['recommendations'] ?? []) : [];

        if (!\is_array($recommendations)) {
            return [];
        }

        $out = [];
        foreach ($recommendations as $rec) {
            if (!\is_array($rec) || !isset($rec['title']) || !\is_string($rec['title'])) {
                continue;
            }
            $genres = $rec['genres'] ?? [];
            $out[] = [
                'title' => $rec['title'],
                'reason' => \is_string($rec['reason'] ?? null) ? $rec['reason'] : '',
                'genres' => \is_array($genres) ? array_map('strval', $genres) : [],
            ];
        }

        return $out;
    }
}
