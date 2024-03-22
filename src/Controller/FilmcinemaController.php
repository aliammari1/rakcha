<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class FilmcinemaController extends AbstractController
{
    #[Route('/filmcinema', name: 'app_filmcinema')]
    public function index(): Response
    {
        return $this->render('front/index.html.twig', [
            'controller_name' => 'FilmcinemaController',
        ]);
    }
}
