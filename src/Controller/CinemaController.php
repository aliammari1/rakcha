<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class CinemaController extends AbstractController
{
    #[Route('/cinema', name: 'app_cinema')]
    public function index(): Response
    {
        return $this->render('front/index.html.twig', [
            'controller_name' => 'CinemaController',
        ]);
    }
}
