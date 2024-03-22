<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class ParticipationEvenementController extends AbstractController
{
    #[Route('/participationevenement', name: 'app_participationevenement')]
    public function index(): Response
    {
        return $this->render('front/index.html.twig', [
            'controller_name' => 'ParticipationEvenementController',
        ]);
    }
}
