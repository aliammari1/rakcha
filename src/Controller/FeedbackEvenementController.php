<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class FeedbackEvenementController extends AbstractController
{
    #[Route('/feedbackevenement', name: 'app_feedbackevenement')]
    public function index(): Response
    {
        return $this->render('front/index.html.twig', [
            'controller_name' => 'FeedbackEvenementController',
        ]);
    }
}
