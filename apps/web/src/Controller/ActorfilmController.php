<?php

namespace App\Controller;

use App\Entity\Actorfilm;
use App\Form\ActorfilmType;
use App\Repository\ActorfilmRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/actorfilm')]
class ActorfilmController extends AbstractController
{
    #[Route('/', name: 'app_actorfilm_index', methods: ['GET'])]
    public function index(ActorfilmRepository $actorfilmRepository): Response
    {
        return $this->render('actorfilm/index.html.twig', [
            'actorfilms' => $actorfilmRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_actorfilm_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $actorfilm = new Actorfilm();
        $form = $this->createForm(ActorfilmType::class, $actorfilm);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($actorfilm);
            $entityManager->flush();

            return $this->redirectToRoute('app_actorfilm_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('actorfilm/new.html.twig', [
            'actorfilm' => $actorfilm,
            'form' => $form,
        ]);
    }

    #[Route('/{idactor}', name: 'app_actorfilm_show', methods: ['GET'])]
    public function show(Actorfilm $actorfilm): Response
    {
        return $this->render('actorfilm/show.html.twig', [
            'actorfilm' => $actorfilm,
        ]);
    }

    #[Route('/{idactor}/edit', name: 'app_actorfilm_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Actorfilm $actorfilm, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(ActorfilmType::class, $actorfilm);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_actorfilm_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('actorfilm/edit.html.twig', [
            'actorfilm' => $actorfilm,
            'form' => $form,
        ]);
    }

    #[Route('/{idactor}', name: 'app_actorfilm_delete', methods: ['POST'])]
    public function delete(Request $request, Actorfilm $actorfilm, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$actorfilm->getIdactor(), $request->request->get('_token'))) {
            $entityManager->remove($actorfilm);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_actorfilm_index', [], Response::HTTP_SEE_OTHER);
    }
}
