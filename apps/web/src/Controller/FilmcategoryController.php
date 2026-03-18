<?php

namespace App\Controller;

use App\Entity\Filmcategory;
use App\Form\FilmcategoryType;
use App\Repository\FilmcategoryRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/filmcategory')]
class FilmcategoryController extends AbstractController
{
    #[Route('/', name: 'app_filmcategory_index', methods: ['GET'])]
    public function index(FilmcategoryRepository $filmcategoryRepository): Response
    {
        return $this->render('filmcategory/index.html.twig', [
            'filmcategories' => $filmcategoryRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_filmcategory_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $filmcategory = new Filmcategory();
        $form = $this->createForm(FilmcategoryType::class, $filmcategory);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($filmcategory);
            $entityManager->flush();

            return $this->redirectToRoute('app_filmcategory_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('filmcategory/new.html.twig', [
            'filmcategory' => $filmcategory,
            'form' => $form,
        ]);
    }

    #[Route('/{filmId}', name: 'app_filmcategory_show', methods: ['GET'])]
    public function show(Filmcategory $filmcategory): Response
    {
        return $this->render('filmcategory/show.html.twig', [
            'filmcategory' => $filmcategory,
        ]);
    }

    #[Route('/{filmId}/edit', name: 'app_filmcategory_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Filmcategory $filmcategory, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(FilmcategoryType::class, $filmcategory);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_filmcategory_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('filmcategory/edit.html.twig', [
            'filmcategory' => $filmcategory,
            'form' => $form,
        ]);
    }

    #[Route('/{filmId}', name: 'app_filmcategory_delete', methods: ['POST'])]
    public function delete(Request $request, Filmcategory $filmcategory, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete' . $filmcategory->getFilmId(), $request->request->get('_token'))) {
            $entityManager->remove($filmcategory);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_filmcategory_index', [], Response::HTTP_SEE_OTHER);
    }
}
