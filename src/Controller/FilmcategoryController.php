<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class FilmcategoryController extends AbstractController
{
    #[Route('/filmcategory', name: 'app_filmcategory')]
    public function index(): Response
    {
        return $this->render('front/index.html.twig', [
            'controller_name' => 'FilmcategoryController',
        ]);
    }
}
