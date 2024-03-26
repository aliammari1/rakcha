<?php

namespace App\Controller;

use App\Repository\FilmRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class ListfilmsController extends AbstractController
{
    #[Route('/listfilms', name: 'app_listfilms')]
    public function index(FilmRepository $filmRepository): Response
    {
        return $this->render('front/listfilms.html.twig', [
            'films' => $filmRepository->findAll(),
        ]);
    }

}
