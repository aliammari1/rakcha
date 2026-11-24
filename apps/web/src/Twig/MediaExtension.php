<?php

namespace App\Twig;

use Twig\Extension\AbstractExtension;
use Twig\TwigFilter;

final class MediaExtension extends AbstractExtension
{
    public function getFilters(): array
    {
        return [new TwigFilter('rakcha_media', $this->localize(...))];
    }

    public function localize(?string $url): ?string
    {
        $url = trim($url ?? '');
        if ('' === $url) {
            return null;
        }

        return preg_replace(
            '#^https://cdn\.jsdelivr\.net/gh/aliammari1/rakcha-jsdelivr-cdn(?:@[^/]+)?/public/#',
            '/',
            $url,
        );
    }
}
