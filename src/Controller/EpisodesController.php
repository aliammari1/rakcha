<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class EpisodesController extends AbstractController
{
    #[Route('/episodes', name: 'app_episodes')]
    public function index(): Response
    {
        return $this->render('front/index.html.twig', [
            'controller_name' => 'EpisodesController',
        ]);
    }
}
