<?php
namespace App\Service;

use Symfony\Contracts\HttpClient\HttpClientInterface;

class MyMemoryTranslator
{
    private $client;

    public function __construct(HttpClientInterface $client)
    {
        $this->client = $client;
    }

    public function translate(string $text, string $targetLanguage, string $sourceLanguage = 'en'): string
    {
        if ($sourceLanguage === $targetLanguage || '' === $text) {
            return $text;
        }

        $response = $this->client->request('GET', 'http://api.mymemory.translated.net/get', [
            'query' => [
                'q' => $text,
                'langpair' => "$sourceLanguage|$targetLanguage",
            ],
        ]);

        $data = $response->toArray();

        return $data['responseData']['translatedText'] ?? '';
    }
}