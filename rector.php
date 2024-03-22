<?php

use Rector\Config\RectorConfig;;


return RectorConfig::configure()
    ->withPaths([
        __DIR__ . '/src/Entity',
    ])
    ->withAttributesSets(symfony: true, doctrine: true)
    ->withImportNames();
