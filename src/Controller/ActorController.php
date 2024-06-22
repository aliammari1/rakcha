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

    #[Route('/new', name: 'app_actor_new', methods: ['POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager, ActorRepository $actorRepository): Response
    {
        $updateForms = array();
        for ($i = 0; $i < count($actorRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(ActorType::class, $actorRepository->findAll()[$i])->createView();
        }
        $actor = new Actor();
        $form = $this->createForm(ActorType::class, $actor);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $file = $form['image']->getData();
            if ($file) {
                $extension = $file->guessExtension();
                if (!$extension) {
                    // extension cannot be guessed
                    $extension = 'bin';
                }
                $filename = rand(1, 99999) . '.' . $extension;
                $destination = $this->getParameter('kernel.project_dir') . "/public/img/films";
                $file->move($destination, $filename);
                $actor->setimage("/img/actors/" . $filename);
    
                // Copy the file to another location
                $anotherDestination = "C:\\xampp\\htdocs\\Rakcha\\rakcha-desktop\\src\\main\\resources\\img\\films";
                copy($destination . "/" . $filename, $anotherDestination . "/" . $filename);
            }
            $filename = rand(1, 99999) . '.' . $extension;
            $file->move($this->getParameter('kernel.project_dir') . "/public/img/actors", $filename);
            $actor->setImage("/img/actors/" . $filename);
            $entityManager->persist($actor);
            $entityManager->flush();
            $this->addFlash('actors', 'actor added successfully');

            return $this->redirectToRoute('app_actor_index', [], Response::HTTP_SEE_OTHER);
        }
        $hasErrorsCreate = true;
        return $this->render('back/actorTables.html.twig', [
            'actors' => $actorRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
            'hasErrorsCreate' => $hasErrorsCreate
        ]);
    }

    #[Route('/{id}', name: 'app_actor_show', methods: ['GET'])]
    public function show(Actor $actor, Request $request): Response
    {
        $referer = $request->headers->get('referer');

        return $this->render('actor/show.html.twig', [
            'actor' => $actor,
            'referer' => $referer,
        ]);
    }

    #[Route('/{id}/edit/{formUpdateNumber}', name: 'app_actor_edit', methods: ['POST'])]
    public function edit(Request $request, Actor $actor, EntityManagerInterface $entityManager, $formUpdateNumber, ActorRepository $actorRepository): Response
    {
        $updateForms = array();
        $actors = $actorRepository->findAll();
        for ($i = 0; $i < count($actors); $i++) {
            $updateForms[$i] = $this->createForm(ActorType::class, $actors[$i])->createView();
        }
        $form = $this->createForm(ActorType::class, new Actor());
        $updateform = $this->createForm(ActorType::class, $actor);
        $updateform->handleRequest($request);

        if ($updateform->isSubmitted() && $updateform->isValid()) {
            $file = $form['image']->getData();
            if ($file) {
                $extension = $file->guessExtension();
                if (!$extension) {
                    // extension cannot be guessed
                    $extension = 'bin';
                }
                $filename = rand(1, 99999) . '.' . $extension;
                $destination = $this->getParameter('kernel.project_dir') . "/public/img/films";
                $file->move($destination, $filename);
                $actor->setimage("/img/actors/" . $filename);
    
                // Copy the file to another location
                $anotherDestination = "C:\\xampp\\htdocs\\Rakcha\\rakcha-desktop\\src\\main\\resources\\img\\films";
                copy($destination . "/" . $filename, $anotherDestination . "/" . $filename);
            }
            $entityManager->flush();
            $this->addFlash('actors', 'actor edited successfully');

            return $this->redirectToRoute('app_actor_index', [], Response::HTTP_SEE_OTHER);
        }
        $entityManager->refresh($actor);

        return $this->render('back/actorTables.html.twig', [
            "formUpdateNumber" => $formUpdateNumber,
            'actors' => $actorRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
            'updateform' => $updateform->createView(),
        ]);
    }

    #[Route('/{id}', name: 'app_actor_delete', methods: ['POST'])]
    public function delete(Request $request, Actor $actor, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete' . $actor->getId(), $request->request->get('_token'))) {
            $entityManager->remove($actor);
            $entityManager->flush();
            $this->addFlash('actors','actor deleted successfully');
        }

        return $this->redirectToRoute('app_actor_index', [], Response::HTTP_SEE_OTHER);
    }
}
