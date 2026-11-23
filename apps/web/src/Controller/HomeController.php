<?php

namespace App\Controller;

use App\Repository\CinemaRepository;
use App\Repository\FilmRepository;
use App\Repository\SeanceRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;

class HomeController extends AbstractController
{
    #[Route('/', name: 'app_home_index')]
    public function home(
        FilmRepository $filmRepository,
        SeanceRepository $showtimes,
        CinemaRepository $cinemas,
    ): Response {
        $films = $filmRepository->findBy([], ['id' => 'DESC'], 12);
        $upcoming = $showtimes->findUpcoming(limit: 18);
        $scheduledIds = array_map(static fn ($seance) => $seance->getIdFilm()->getId(), $upcoming);

        return $this->render('front/index.html.twig', [
            'films' => $films,
            'showtimes' => $upcoming,
            'nowShowing' => array_values(array_filter($films, static fn ($film) => in_array($film->getId(), $scheduledIds, true))),
            'comingSoon' => array_values(array_filter($films, static fn ($film) => !in_array($film->getId(), $scheduledIds, true))),
            'cinemas' => $cinemas->findBy(['statut' => 'Accepted'], ['nom' => 'ASC'], 6),
        ]);
    }
}
