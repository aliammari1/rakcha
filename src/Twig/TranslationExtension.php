<?php

namespace App\Twig;

use App\Service\MyMemoryTranslator;
use Symfony\Component\HttpFoundation\RequestStack;
use Twig\Extension\AbstractExtension;
use Twig\TwigFilter;

class TranslationExtension extends AbstractExtension
{
    private $translator;
    private $requestStack;

    public function __construct(MyMemoryTranslator $translator, RequestStack $requestStack)
    {
        $this->translator = $translator;
        $this->requestStack = $requestStack;
    }

    public function getFilters(): array
    {
        return [
            new TwigFilter('auto_translate', [$this, 'translate']),
        ];
    }

    public function translate(string $text): string
    {
        $locale = $this->requestStack->getCurrentRequest()->getLocale();
        return $this->translator->translate($text, $locale);
    }
}