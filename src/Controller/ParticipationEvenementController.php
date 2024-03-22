<?php

namespace App\Controller;

use App\Entity\ParticipationEvenement;
use App\Form\ParticipationEvenementType;
use App\Repository\ParticipationEvenementRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/participation/evenement')]
class ParticipationEvenementController extends AbstractController
{
    #[Route('/', name: 'app_participation_evenement_index', methods: ['GET'])]
    public function index(ParticipationEvenementRepository $participationEvenementRepository): Response
    {
        return $this->render('participation_evenement/index.html.twig', [
            'participation_evenements' => $participationEvenementRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_participation_evenement_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $participationEvenement = new ParticipationEvenement();
        $form = $this->createForm(ParticipationEvenementType::class, $participationEvenement);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($participationEvenement);
            $entityManager->flush();

            return $this->redirectToRoute('app_participation_evenement_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('participation_evenement/new.html.twig', [
            'participation_evenement' => $participationEvenement,
            'form' => $form,
        ]);
    }

    #[Route('/{idParticipation}', name: 'app_participation_evenement_show', methods: ['GET'])]
    public function show(ParticipationEvenement $participationEvenement): Response
    {
        return $this->render('participation_evenement/show.html.twig', [
            'participation_evenement' => $participationEvenement,
        ]);
    }

    #[Route('/{idParticipation}/edit', name: 'app_participation_evenement_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, ParticipationEvenement $participationEvenement, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(ParticipationEvenementType::class, $participationEvenement);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_participation_evenement_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('participation_evenement/edit.html.twig', [
            'participation_evenement' => $participationEvenement,
            'form' => $form,
        ]);
    }

    #[Route('/{idParticipation}', name: 'app_participation_evenement_delete', methods: ['POST'])]
    public function delete(Request $request, ParticipationEvenement $participationEvenement, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$participationEvenement->getIdParticipation(), $request->request->get('_token'))) {
            $entityManager->remove($participationEvenement);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_participation_evenement_index', [], Response::HTTP_SEE_OTHER);
    }
}
