<?php

namespace App\Controller;

use App\Repository\ActorRepository;
use App\Repository\FilmRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class ListfilmsController extends AbstractController
{
    #[Route('/listfilms', name: 'app_listfilms_index')]
    public function index(FilmRepository $filmRepository): Response
    {
        return $this->render('front/listfilms.html.twig', [
            'films' => $filmRepository->findAll(),
        ]);
    }
    #[Route('/filmHome', name: 'app_listhome_index')]
    public function indexHome(FilmRepository $filmRepository): Response
    {
        return $this->render('front/film.html.twig', [
            'films' => $filmRepository->findAll(),
        ]);
    }
    #[Route('/listactors', name: 'app_listactors_index')]
    public function indexactor(ActorRepository $filmRepository): Response
    {
        return $this->render('front/listactors.html.twig', [
            'actors' => $filmRepository->findAll(),
        ]);
    }
}
