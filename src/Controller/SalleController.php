<?php

namespace App\Controller;

use App\Entity\Cinema;
use App\Entity\Salle;
use App\Entity\Seat;
use App\Form\SalleType;
use App\Repository\SalleRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/salle')]
class SalleController extends AbstractController
{
    #[Route('/{idCinema}', name: 'app_salle_index', methods: ['GET', 'POST'])]
    public function index(int $idCinema, SalleRepository $salleRepository, Request $request, EntityManagerInterface $entityManager): Response
    {
        $cinema = $entityManager->getRepository(Cinema::class)->find($idCinema);

        // Option 1 - Get all salles only related to the current cinema
        $salles = $salleRepository->findBy(['idCinema' => $idCinema]);

        // Option 2 - Only show the form and updateForms when a single cinema is active
        // In this case, you need to pass the `$cinema` to the templates
        $form = $this->createForm(SalleType::class, new Salle());
        $updateForms = array();
        for ($i = 0; $i < count($salles); $i++) {
            $updateForms[$i] = $this->createForm(SalleType::class, $salles[$i])->createView();
        }
        if ($cinema) {
            return $this->render('back/SallesTable.html.twig', [
                'salles' => $salles,
                'form' => $form->createView(),
                'updateForms' => $updateForms,
                'cinema' => $cinema,
            ]);
        }


        return $this->render('back/SallesTable.html.twig', [
            'salles' => [],
            'form' => $form->createView(),
            'updateForms' => [],
            'cinema' => null,
        ]);
    }

    #[Route('/{idCinema}/new', name: 'app_salle_new', methods: ['GET', 'POST'])]

    public function new(int $idCinema, Request $request, EntityManagerInterface $entityManager, SalleRepository  $salleRepository): Response
    {
        $cinema = $entityManager->find(Cinema::class, $idCinema);
        if (!$cinema) {
            throw $this->createNotFoundException('No cinema found for id ' . $idCinema);
        }
        $salles = $salleRepository->findBy(['idCinema' => $idCinema]);

        $form = $this->createForm(SalleType::class, new Salle());
        $updateForms = array();
        for ($i = 0; $i < count($salles); $i++) {
            $updateForms[$i] = $this->createForm(SalleType::class, $salles[$i])->createView();
        }

        $salle = new Salle();
        $salle->setCinema($cinema); // Assigner directement l'objet cinema à la salle

        $form = $this->createForm(SalleType::class, $salle);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($salle);
            $entityManager->flush();
            for ($i = 0; $i < $salle->getNbPlaces(); $i++) {
                $seat = new Seat();
                $seat->setSalle($salle);
                $seat->setStatut("vide");
                $entityManager->persist($seat);
            }
            $entityManager->flush();
            return $this->redirectToRoute('app_salle_index', ['idCinema' => $idCinema], Response::HTTP_SEE_OTHER);
        }

        $hasErrorsCreate = true;
        return $this->render('back/SallesTable.html.twig', [
            'salles' => $salles, // Passer les salles existantes à la vue
            'form' => $form->createView(),
            'updateForms' => $updateForms,
            'cinema' => $cinema,
            'hasErrorsCreate' => $hasErrorsCreate,
        ]);
    }

    #[Route('/{idSalle}', name: 'app_salle_show', methods: ['GET'])]
    public function show(Salle $salle): Response
    {
        return $this->render('salle/show.html.twig', [
            'salle' => $salle,
        ]);
    }

    #[Route('/{idCinema}/{idSalle}/edit/{formUpdateNumber}/', name: 'app_salle_edit', methods: ['GET', 'POST'])]
    public function edit($formUpdateNumber, int $idCinema, Request $request, Salle $salle, EntityManagerInterface $entityManager, SalleRepository $salleRepository): Response
    {
        $cinema = $entityManager->find(Cinema::class, $idCinema);
        if (!$cinema) {
            throw  $this->createNotFoundException('No cinema found for id' . $idCinema);
        }
        $salles = $salleRepository->findBy(['idCinema' => $idCinema]);

        $form = $this->createForm(SalleType::class, new Salle());
        $updateForms = array();
        for ($i = 0; $i < count($salles); $i++) {
            $updateForms[$i] = $this->createForm(SalleType::class, $salles[$i])->createView();
        }
        $form = $this->createForm(SalleType::class, new Salle());

        $updateform = $this->createForm(SalleType::class, $salle);

        $updateform->handleRequest($request);

        if ($updateform->isSubmitted() && $updateform->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_salle_index', ['idCinema' => $idCinema], Response::HTTP_SEE_OTHER);
        }


        $entityManager->refresh($salle);
        return $this->render('back/SallesTable.html.twig', [
            'salles' => $salles, // Passer les salles existantes à la vue
            'form' => $form->createView(),
            'updateForms' => $updateForms,
            'cinema' => $cinema,
            "formUpdateNumber" => $formUpdateNumber,
            'updateform' => $updateform->createView(),
        ]);
    }

    #[Route('/{idCinema}/{idSalle}', name: 'app_salle_delete', methods: ['POST'])]
    public function delete(int $idCinema, Request $request, Salle $salle, EntityManagerInterface $entityManager): Response
    {
        $cinema = $entityManager->find(Cinema::class, $idCinema);
        if (!$cinema) {
            throw  $this->createNotFoundException('No cinema found for id' . $idCinema);
        }
        if ($this->isCsrfTokenValid('delete' . $salle->getIdSalle(), $request->request->get('_token'))) {
            $entityManager->remove($salle);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_salle_index', ['idCinema' => $idCinema], Response::HTTP_SEE_OTHER);
    }
}
