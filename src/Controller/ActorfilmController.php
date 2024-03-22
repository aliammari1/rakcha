<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class ActorfilmController extends AbstractController
{
    #[Route('/actorfilm', name: 'app_actorfilm')]
    public function index(): Response
    {
        return $this->render('front/index.html.twig', [
            'controller_name' => 'ActorfilmController',
        ]);
    }
}
