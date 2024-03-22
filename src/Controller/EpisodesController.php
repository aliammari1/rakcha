<?php

namespace App\Controller;

use App\Entity\Episodes;
use App\Form\EpisodesType;
use App\Repository\EpisodesRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/episodes')]
class EpisodesController extends AbstractController
{
    #[Route('/', name: 'app_episodes_index', methods: ['GET'])]
    public function index(EpisodesRepository $episodesRepository): Response
    {
        return $this->render('episodes/index.html.twig', [
            'episodes' => $episodesRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_episodes_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $episode = new Episodes();
        $form = $this->createForm(EpisodesType::class, $episode);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($episode);
            $entityManager->flush();

            return $this->redirectToRoute('app_episodes_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('episodes/new.html.twig', [
            'episode' => $episode,
            'form' => $form,
        ]);
    }

    #[Route('/{idepisode}', name: 'app_episodes_show', methods: ['GET'])]
    public function show(Episodes $episode): Response
    {
        return $this->render('episodes/show.html.twig', [
            'episode' => $episode,
        ]);
    }

    #[Route('/{idepisode}/edit', name: 'app_episodes_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Episodes $episode, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(EpisodesType::class, $episode);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_episodes_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('episodes/edit.html.twig', [
            'episode' => $episode,
            'form' => $form,
        ]);
    }

    #[Route('/{idepisode}', name: 'app_episodes_delete', methods: ['POST'])]
    public function delete(Request $request, Episodes $episode, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$episode->getIdepisode(), $request->request->get('_token'))) {
            $entityManager->remove($episode);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_episodes_index', [], Response::HTTP_SEE_OTHER);
    }
}
