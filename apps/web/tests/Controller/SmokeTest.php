<?php

namespace App\Tests\Controller;

use Symfony\Bundle\FrameworkBundle\Test\WebTestCase;

/**
 * Functional smoke test: boots the real Symfony kernel and drives the HTTP
 * stack (routing, security firewall, error handling) without depending on a
 * populated database. Confirms the server-rendered MVC app wires up.
 */
final class SmokeTest extends WebTestCase
{
    public function testKernelBoots(): void
    {
        $client = self::createClient();
        self::assertNotNull($client->getKernel());
        self::assertSame('test', $client->getKernel()->getEnvironment());
    }

    public function testUnknownRouteReturnsNotFound(): void
    {
        $client = self::createClient();
        $client->request('GET', '/this-route-does-not-exist-' . uniqid());

        self::assertSame(404, $client->getResponse()->getStatusCode());
    }
}
