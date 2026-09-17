<?php

namespace App\Controller;

use App\Entity\Filmcoment;
use App\Form\FilmcomentType;
use App\Repository\FilmcomentRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/filmcoment')]
class FilmcomentController extends AbstractController
{
    #[Route('/', name: 'app_filmcoment_index', methods: ['GET'])]
    public function index(FilmcomentRepository $filmcomentRepository): Response
    {
        return $this->render('filmcoment/index.html.twig', [
            'filmcoments' => $filmcomentRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_filmcoment_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $filmcoment = new Filmcoment();
        $form = $this->createForm(FilmcomentType::class, $filmcoment);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($filmcoment);
            $entityManager->flush();

            return $this->redirectToRoute('app_filmcoment_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('filmcoment/new.html.twig', [
            'filmcoment' => $filmcoment,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_filmcoment_show', methods: ['GET'])]
    public function show(Filmcoment $filmcoment): Response
    {
        return $this->render('filmcoment/show.html.twig', [
            'filmcoment' => $filmcoment,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_filmcoment_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Filmcoment $filmcoment, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(FilmcomentType::class, $filmcoment);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_filmcoment_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('filmcoment/edit.html.twig', [
            'filmcoment' => $filmcoment,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_filmcoment_delete', methods: ['POST'])]
    public function delete(Request $request, Filmcoment $filmcoment, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$filmcoment->getId(), $request->request->get('_token'))) {
            $entityManager->remove($filmcoment);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_filmcoment_index', [], Response::HTTP_SEE_OTHER);
    }
}
