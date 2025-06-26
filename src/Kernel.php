<?php

namespace App;

use Symfony\Bundle\FrameworkBundle\Kernel\MicroKernelTrait;
use Symfony\Component\HttpKernel\Kernel as BaseKernel;

class Kernel extends BaseKernel
{
    use MicroKernelTrait;

    public function getCacheDir(): string
    {
        return sys_get_temp_dir() . '/cache';
    }

    public function getLogDir(): string
    {
        return sys_get_temp_dir() . '/log';
    }

}
