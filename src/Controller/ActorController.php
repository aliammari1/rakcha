<?php

namespace App\Controller;

use App\Entity\Actor;
use App\Form\ActorType;
use App\Repository\ActorRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/actor')]
class ActorController extends AbstractController
{
    #[Route('/', name: 'app_actor_index', methods: ['GET'])]
    public function index(ActorRepository $actorRepository): Response
    {
        flash()->addSuccess("successfully added actor");
        $form = $this->createForm(ActorType::class, new Actor());
        $updateForms = array();
        for ($i = 0; $i < count($actorRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(ActorType::class, $actorRepository->findAll()[$i])->createView();
        }
        return $this->render('back/actorTables.html.twig', [
            'actors' => $actorRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
        ]);
    }

    #[Route('/new', name: 'app_actor_new', methods: [ 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $actor = new Actor();
        $form = $this->createForm(ActorType::class, $actor);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $file = $form['image']->getData();

            $extension = $file->guessExtension();
            if (!$extension) {
                // extension cannot be guessed
                $extension = 'bin';
            }
            $filename = rand(1, 99999) . '.' . $extension;
            $file->move($this->getParameter('kernel.project_dir')."/public/img/actors", $filename);
            $actor->setImage("/img/actors/" . $filename);
            $entityManager->persist($actor);
            $entityManager->flush();

            return $this->redirectToRoute('app_actor_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('actor/new.html.twig', [
            'actor' => $actor,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_actor_show', methods: ['GET'])]
    public function show(Actor $actor,Request $request): Response
    {
        $referer = $request->headers->get('referer');

        return $this->render('actor/show.html.twig', [
            'actor' => $actor,
            'referer' => $referer,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_actor_edit', methods: [ 'POST'])]
    public function edit(Request $request, Actor $actor, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(ActorType::class, $actor);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $file = $form['image']->getData();

            $extension = $file->guessExtension();
            if (!$extension) {
                // extension cannot be guessed
                $extension = 'bin';
            }
            $filename = rand(1, 99999) . '.' . $extension;
            $file->move($this->getParameter('kernel.project_dir')."/public/img/actors", $filename);
            $actor->setImage("/img/actors/" . $filename);
            $entityManager->flush();

            return $this->redirectToRoute('app_actor_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('actor/edit.html.twig', [
            'actor' => $actor,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_actor_delete', methods: ['POST'])]
    public function delete(Request $request, Actor $actor, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$actor->getId(), $request->request->get('_token'))) {
            $entityManager->remove($actor);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_actor_index', [], Response::HTTP_SEE_OTHER);
    }
}
