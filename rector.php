<?php

use Rector\Config\RectorConfig;
use Rector\Doctrine\CodeQuality\Rector\Class_\InitializeDefaultEntityCollectionRector;
use Rector\Doctrine\CodeQuality\Rector\Property\ImproveDoctrineCollectionDocTypeInEntityRector;

return RectorConfig::configure()
    ->withPaths([
        __DIR__ . '/src/Entity',
    ])
    ->withRules([
        Rector\Doctrine\CodeQuality\Rector\Class_\YamlToAttributeDoctrineMappingRector::class,
        ImproveDoctrineCollectionDocTypeInEntityRector::class
    ]);
