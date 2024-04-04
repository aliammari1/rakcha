<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class MicrosoftController extends AbstractController
{
    #[Route('/microsoft', name: 'app_microsoft')]
    public function index(): Response
    {
        return $this->render('microsoft/index.html.twig', [
            'controller_name' => 'MicrosoftController',
        ]);
    }
}
