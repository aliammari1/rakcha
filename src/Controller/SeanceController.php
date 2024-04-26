<?php

namespace App\Controller;

use App\Entity\Seance;
use App\Entity\Salle;
use App\Form\SeanceType;
use App\Repository\CinemaRepository;
use App\Repository\FilmRepository;
use App\Repository\SalleRepository;
use App\Repository\SeanceRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/seance')]
class SeanceController extends AbstractController
{
    #[Route('/', name: 'app_seance_index', methods: ['GET' , 'POST'])]
    public function index(SeanceRepository $seanceRepository, CinemaRepository $cinemaRepository, FilmRepository $filmRepository, SalleRepository $salleRepository): Response
    {
        $form = $this->createForm(SeanceType::class, new Seance());
        $updateForms = array();
        for ($i = 0; $i < count($seanceRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(SeanceType::class, $seanceRepository->findAll()[$i])->createView();
        }
        return $this->render('seance/SeancesTable.html.twig', [
            'seances' => $seanceRepository->findAll(),
            'cinemas' =>  $cinemaRepository->findAll(),
            'films' => $filmRepository->findAll(),
            'salles' => $salleRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
        ]);
    }

    #[Route('/new', name: 'app_seance_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager, SeanceRepository  $seanceRepository, CinemaRepository $cinemaRepository, FilmRepository $filmRepository, SalleRepository $salleRepository): Response
    {
        $seance = new Seance();
        $updateForms = array();
        for ($i = 0; $i < count($seanceRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(SeanceType::class, $seanceRepository->findAll()[$i])->createView();
        }
        $form = $this->createForm(SeanceType::class, $seance);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($seance);
            $entityManager->flush();

            return $this->redirectToRoute('app_seance_index', [], Response::HTTP_SEE_OTHER);
        }

        $hasErrorsCreate = true;
        return $this->render('seance/SeancesTable.html.twig', [
            'seances' => $seanceRepository->findAll(),
            'cinemas' =>  $cinemaRepository->findAll(),
            'films' => $filmRepository->findAll(),
            'salles' => $salleRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
            'hasErrorsCreate' => $hasErrorsCreate,
        ]);
    }

    #[Route('/{idSeance}', name: 'app_seance_show', methods: ['GET'])]
    public function show(Seance $seance): Response
    {
        return $this->render('seance/show.html.twig', [
            'seance' => $seance,
        ]);
    }

    #[Route('/{idSeance}/edit/{formUpdateNumber}/', name: 'app_seance_edit', methods: ['GET', 'POST'])]
    public function edit($formUpdateNumber, Request $request, Seance $seance, EntityManagerInterface $entityManager, SeanceRepository $seanceRepository, CinemaRepository $cinemaRepository, FilmRepository $filmRepository, SalleRepository $salleRepository): Response
    {
        $updateForms = array();
        $seances = $seanceRepository->findAll();
     for ($i = 0; $i < count($seances); $i++) {
         $updateForms[$i] = $this->createForm(SeanceType::class, $seances[$i])->createView();
     }
     $form = $this->createForm(SeanceType::class, new Seance());

     $updateform = $this->createForm(SeanceType::class, $seance);

     $updateform->handleRequest($request);

        if ($updateform->isSubmitted() && $updateform->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_seance_index', [], Response::HTTP_SEE_OTHER);
        }
        $entityManager->refresh($seance);
        return $this->render('seance/SeancesTable.html.twig', [
            'seances' => $seanceRepository->findAll(),
            'cinemas' =>  $cinemaRepository->findAll(),
            'films' => $filmRepository->findAll(),
            'salles' => $salleRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
            "formUpdateNumber" => $formUpdateNumber,
            'updateform' => $updateform->createView(),
        ]);
    }

    #[Route('/{idSeance}', name: 'app_seance_delete', methods: ['POST'])]
    public function delete(Request $request, Seance $seance, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$seance->getIdSeance(), $request->request->get('_token'))) {
            $entityManager->remove($seance);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_seance_index', [], Response::HTTP_SEE_OTHER);
    }
}
