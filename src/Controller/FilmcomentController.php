<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class FilmcomentController extends AbstractController
{
    #[Route('/filmcoment', name: 'app_filmcoment')]
    public function index(): Response
    {
        return $this->render('front/index.html.twig', [
            'controller_name' => 'FilmcomentController',
        ]);
    }
}
