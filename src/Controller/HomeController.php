<?php

namespace App\Controller;

use App\Repository\FilmRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class HomeController extends AbstractController
{
    #[Route('/', name: 'app_home_index')]
    public function home(FilmRepository $filmRepository): Response
    {
        $films = $filmRepository->findAll();
        return $this->render('front/index.html.twig', [
            'films' => $films,
        ]);
    }
}
