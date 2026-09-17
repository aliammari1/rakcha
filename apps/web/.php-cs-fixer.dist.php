<?php

$finder = (new PhpCsFixer\Finder())
    ->in(__DIR__ . '/src')
    ->in(__DIR__ . '/tests')
    ->exclude('var');

return (new PhpCsFixer\Config())
    ->setRiskyAllowed(true)
    ->setRules([
        '@Symfony' => true,
        '@Symfony:risky' => true,
        'array_syntax' => ['syntax' => 'short'],
        'ordered_imports' => ['sort_algorithm' => 'alpha'],
        'no_unused_imports' => true,
        'declare_strict_types' => false,
        'native_function_invocation' => false,
        'php_unit_method_casing' => ['case' => 'camel_case'],
    ])
    ->setFinder($finder)
    ->setCacheFile(__DIR__ . '/var/.php-cs-fixer.cache');
