<?php

namespace App\Controller;

use App\Entity\Ratingfilm;
use App\Form\RatingfilmType;
use App\Repository\RatingfilmRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/ratingfilm')]
class RatingfilmController extends AbstractController
{
    #[Route('/', name: 'app_ratingfilm_index', methods: ['GET'])]
    public function index(RatingfilmRepository $ratingfilmRepository): Response
    {
        return $this->render('ratingfilm/index.html.twig', [
            'ratingfilms' => $ratingfilmRepository->findAll(),
        ]);
    }

    #[Route('/ratefilm', name: 'app_rate_film_index', methods: ['POST', 'GET'])]
    public function rateFilm(Request $request, EntityManagerInterface $entityManager, RatingfilmRepository $ratingfilmRepository): Response
    {
        $data = json_decode($request->getContent(), true);
        $ratingfilm = $ratingfilmRepository->findOneBy(['idUser' => $this->getUser()->getId(), 'idFilm' => $data['filmId']]);
        if ($ratingfilm == null)
            $ratingfilm = new Ratingfilm();
        $ratingfilm->setRate($data['rate']);
        $ratingfilm->setIdFilm($data['filmId']);
        $ratingfilm->setIdUser($this->getUser()->getId());
        $entityManager->persist($ratingfilm);
        $entityManager->flush();
        return $this->json(["success" => true, 'ratingfilm' => $ratingfilm]);
    }

    #[Route('/new', name: 'app_ratingfilm_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $ratingfilm = new Ratingfilm();
        $form = $this->createForm(RatingfilmType::class, $ratingfilm);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($ratingfilm);
            $entityManager->flush();

            return $this->redirectToRoute('app_ratingfilm_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('ratingfilm/new.html.twig', [
            'ratingfilm' => $ratingfilm,
            'form' => $form,
        ]);
    }

    #[Route('/{idFilm}', name: 'app_ratingfilm_show', methods: ['GET'])]
    public function show(Ratingfilm $ratingfilm): Response
    {
        return $this->render('ratingfilm/show.html.twig', [
            'ratingfilm' => $ratingfilm,
        ]);
    }

    #[Route('/{idFilm}/edit', name: 'app_ratingfilm_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Ratingfilm $ratingfilm, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(RatingfilmType::class, $ratingfilm);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_ratingfilm_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('ratingfilm/edit.html.twig', [
            'ratingfilm' => $ratingfilm,
            'form' => $form,
        ]);
    }

    #[Route('/{idFilm}', name: 'app_ratingfilm_delete', methods: ['POST'])]
    public function delete(Request $request, Ratingfilm $ratingfilm, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete' . $ratingfilm->getIdFilm(), $request->request->get('_token'))) {
            $entityManager->remove($ratingfilm);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_ratingfilm_index', [], Response::HTTP_SEE_OTHER);
    }
}
