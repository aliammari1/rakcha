<?php

namespace App\Controller;

use App\Repository\CinemaRepository;
use App\Repository\FilmRepository;
use App\Repository\SalleRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use App\Repository\SeanceRepository;
use DateTime;

class PlanningController extends AbstractController
{
    private $seanceRepository;

    public function __construct(SeanceRepository $seanceRepository)
    {
        $this->seanceRepository = $seanceRepository;
    }

    #[Route('/planning/{idCinema}', name: 'app_planning')]
public function index($idCinema, CinemaRepository $cinemaRepository, FilmRepository $filmRepository, SalleRepository $salleRepository, SeanceRepository $seanceRepository): Response
{
    // Obtenir la date du début et de la fin de la semaine courante
    $currentDate = new DateTime();
    $startWeek = $currentDate->format('Y-m-d');
    $endWeek = $currentDate->modify('+6 day')->format('Y-m-d');

    // Récupérer toutes les séances pour les 7 prochains jours et le cinéma spécifié
    $seances = $seanceRepository->createQueryBuilder('s')
        ->innerJoin('s.idSalle', 'sa')
        ->andWhere('s.date BETWEEN :startWeek AND :endWeek')
        ->andWhere('sa.idCinema = :idCinema') // Ajouter cette condition pour filtrer par idCinema
        ->setParameter('startWeek', $startWeek)
        ->setParameter('endWeek', $endWeek)
        ->setParameter('idCinema', $idCinema) // Liaison du paramètre idCinema
        ->getQuery()
        ->getResult();

    $seanceData = [];
    foreach ($seances as $seance) {
        $seanceData[] = [
            'title' => $seance->getIdFilm()->getNom(),
            'start' => $seance->getDate()->format('Y-m-d') . 'T' .$seance->getHd()->format('H:i:s'),
            'end' => $seance->getDate()->format('Y-m-d') . 'T' . $seance->getHf()->format('H:i:s'),
            'image' => $seance->getIdFilm()->getImage(), // Ajout de l'image du film
            'prix' => $seance->getPrix(),
            'salle' => $seance->getIdSalle()->getNomSalle(),
        ];
    }

    $seanceData = json_encode($seanceData);

    return $this->render('planning/planning.html.twig', compact('seanceData'));
}

    
}
?>
