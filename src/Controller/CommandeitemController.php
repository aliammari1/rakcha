<?php

namespace App\Controller;

use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class CommandeitemController extends AbstractController
{
    #[Route('/commandeitem', name: 'app_commandeitem')]
    public function index(): Response
    {
        return $this->render('front/index.html.twig', [
            'controller_name' => 'CommandeitemController',
        ]);
    }
}
