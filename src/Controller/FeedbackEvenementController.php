<?php

namespace App\Controller;

use App\Entity\FeedbackEvenement;
use App\Form\FeedbackEvenementType;
use App\Repository\FeedbackEvenementRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/feedback/evenement')]
class FeedbackEvenementController extends AbstractController
{
    #[Route('/', name: 'app_feedback_evenement_index', methods: ['GET'])]
    public function index(FeedbackEvenementRepository $feedbackEvenementRepository): Response
    {
        return $this->render('feedback_evenement/index.html.twig', [
            'feedback_evenements' => $feedbackEvenementRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_feedback_evenement_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $feedbackEvenement = new FeedbackEvenement();
        $form = $this->createForm(FeedbackEvenementType::class, $feedbackEvenement);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($feedbackEvenement);
            $entityManager->flush();

            return $this->redirectToRoute('app_feedback_evenement_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('feedback_evenement/new.html.twig', [
            'feedback_evenement' => $feedbackEvenement,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_feedback_evenement_show', methods: ['GET'])]
    public function show(FeedbackEvenement $feedbackEvenement): Response
    {
        return $this->render('feedback_evenement/show.html.twig', [
            'feedback_evenement' => $feedbackEvenement,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_feedback_evenement_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, FeedbackEvenement $feedbackEvenement, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(FeedbackEvenementType::class, $feedbackEvenement);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_feedback_evenement_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('feedback_evenement/edit.html.twig', [
            'feedback_evenement' => $feedbackEvenement,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_feedback_evenement_delete', methods: ['POST'])]
    public function delete(Request $request, FeedbackEvenement $feedbackEvenement, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$feedbackEvenement->getId(), $request->request->get('_token'))) {
            $entityManager->remove($feedbackEvenement);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_feedback_evenement_index', [], Response::HTTP_SEE_OTHER);
    }
}
