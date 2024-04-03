<?php

use Rector\Config\RectorConfig;
// use Rector\Doctrine\CodeQuality\Rector\Class_\InitializeDefaultEntityCollectionRector;
// use Rector\Doctrine\CodeQuality\Rector\Property\ImproveDoctrineCollectionDocTypeInEntityRector;
// use Rector\Doctrine\CodeQuality\Rector\Property\TypedPropertyFromColumnTypeRector;
// use Rector\Doctrine\CodeQuality\Rector\Property\TypedPropertyFromDoctrineCollectionRector;
// use Rector\Doctrine\CodeQuality\Rector\Property\TypedPropertyFromToManyRelationTypeRector;
// use Rector\Doctrine\CodeQuality\Rector\Property\TypedPropertyFromToOneRelationTypeRector;


return RectorConfig::configure()
    ->withPaths([
        __DIR__ . '/src/Entity',
    ])
    ->withAttributesSets(symfony: true, doctrine: true)
    //->withPreparedSets(deadCode: true, codeQuality: true, codingStyle: true, typeDeclarations: true)
    ->withImportNames();
