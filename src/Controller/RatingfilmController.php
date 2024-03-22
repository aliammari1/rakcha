<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class RatingfilmController extends AbstractController
{
    #[Route('/ratingfilm', name: 'app_ratingfilm')]
    public function index(): Response
    {
        return $this->render('front/index.html.twig', [
            'controller_name' => 'RatingfilmController',
        ]);
    }
}
