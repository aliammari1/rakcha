<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class CommentairecinemaController extends AbstractController
{
    #[Route('/commentairecinema', name: 'app_commentairecinema')]
    public function index(): Response
    {
        return $this->render('front/index.html.twig', [
            'controller_name' => 'CommentairecinemaController',
        ]);
    }
}
