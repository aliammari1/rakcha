<?php

namespace App\Twig;

use BaconQrCode\Renderer\ImageRenderer;
use BaconQrCode\Renderer\Image\SvgImageBackEnd;
use BaconQrCode\Renderer\RendererStyle\RendererStyle;
use BaconQrCode\Writer;
use Twig\Extension\AbstractExtension;
use Twig\TwigFunction;

class QrCodeExtension extends AbstractExtension
{
    public function getFunctions(): array
    {
        return [
            new TwigFunction('qr_code_result', [$this, 'generateQrCode']),
        ];
    }

    public function generateQrCode(string $data, int $size = 300): array
    {
        // Handle empty or null data
        if (empty($data) || trim($data) === '') {
            $data = 'No URL available';
        }

        $renderer = new ImageRenderer(
            new RendererStyle($size),
            new SvgImageBackEnd()
        );
        $writer = new Writer($renderer);
        $svgContent = $writer->writeString($data);

        // Convert to base64 data URI for inline use
        $dataUri = 'data:image/svg+xml;base64,' . base64_encode($svgContent);

        return [
            'dataUri' => $dataUri,
            'matrix' => [
                'outerSize' => $size
            ]
        ];
    }
}
