<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class CategorieEvenementController extends AbstractController
{
    #[Route('/categorieevenement', name: 'app_categorieevenement')]
    public function index(): Response
    {
        return $this->render('front/index.html.twig', [
            'controller_name' => 'CategorieEvenementController',
        ]);
    }
}
