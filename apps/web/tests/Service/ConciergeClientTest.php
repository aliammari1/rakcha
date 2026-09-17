<?php

namespace App\Tests\Service;

use App\Service\ConciergeClient;
use PHPUnit\Framework\TestCase;

/**
 * Unit test for the concierge response parser — no HTTP / network needed.
 */
final class ConciergeClientTest extends TestCase
{
    public function testParsesFirebaseCallableEnvelope(): void
    {
        $payload = [
            'result' => [
                'recommendations' => [
                    ['title' => 'Dune: Part Two', 'reason' => 'Epic sci-fi.', 'genres' => ['Sci-Fi', 'Adventure']],
                    ['title' => 'Whiplash', 'reason' => 'Intense drama.'],
                ],
            ],
        ];

        $recs = ConciergeClient::parseRecommendations($payload);

        self::assertCount(2, $recs);
        self::assertSame('Dune: Part Two', $recs[0]['title']);
        self::assertSame(['Sci-Fi', 'Adventure'], $recs[0]['genres']);
        self::assertSame([], $recs[1]['genres']); // defaulted
    }

    public function testParsesBareResultObject(): void
    {
        $payload = ['recommendations' => [['title' => 'Parasite', 'reason' => 'Thriller.']]];

        $recs = ConciergeClient::parseRecommendations($payload);

        self::assertCount(1, $recs);
        self::assertSame('Parasite', $recs[0]['title']);
    }

    public function testDropsEntriesWithoutTitle(): void
    {
        $payload = ['result' => ['recommendations' => [['reason' => 'no title'], ['title' => 'Heat']]]];

        $recs = ConciergeClient::parseRecommendations($payload);

        self::assertCount(1, $recs);
        self::assertSame('Heat', $recs[0]['title']);
    }

    public function testReturnsEmptyWhenNoRecommendations(): void
    {
        self::assertSame([], ConciergeClient::parseRecommendations(['result' => []]));
        self::assertSame([], ConciergeClient::parseRecommendations([]));
    }
}
