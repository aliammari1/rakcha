<?php

namespace App\Controller;

use App\Entity\Filmcinema;
use App\Form\FilmcinemaType;
use App\Repository\FilmcinemaRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/filmcinema')]
class FilmcinemaController extends AbstractController
{
    #[Route('/', name: 'app_filmcinema_index', methods: ['GET'])]
    public function index(FilmcinemaRepository $filmcinemaRepository): Response
    {
        return $this->render('filmcinema/index.html.twig', [
            'filmcinemas' => $filmcinemaRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_filmcinema_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $filmcinema = new Filmcinema();
        $form = $this->createForm(FilmcinemaType::class, $filmcinema);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($filmcinema);
            $entityManager->flush();

            return $this->redirectToRoute('app_filmcinema_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('filmcinema/new.html.twig', [
            'filmcinema' => $filmcinema,
            'form' => $form,
        ]);
    }

    #[Route('/{idFilm}', name: 'app_filmcinema_show', methods: ['GET'])]
    public function show(Filmcinema $filmcinema): Response
    {
        return $this->render('filmcinema/show.html.twig', [
            'filmcinema' => $filmcinema,
        ]);
    }

    #[Route('/{idFilm}/edit', name: 'app_filmcinema_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Filmcinema $filmcinema, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(FilmcinemaType::class, $filmcinema);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_filmcinema_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('filmcinema/edit.html.twig', [
            'filmcinema' => $filmcinema,
            'form' => $form,
        ]);
    }

    #[Route('/{idFilm}', name: 'app_filmcinema_delete', methods: ['POST'])]
    public function delete(Request $request, Filmcinema $filmcinema, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$filmcinema->getIdFilm(), $request->request->get('_token'))) {
            $entityManager->remove($filmcinema);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_filmcinema_index', [], Response::HTTP_SEE_OTHER);
    }
}
