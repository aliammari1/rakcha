<?php

namespace App\Controller;

use App\Entity\Commentairecinema;
use App\Form\CommentairecinemaType;
use App\Repository\CommentairecinemaRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/commentairecinema')]
class CommentairecinemaController extends AbstractController
{
    #[Route('/', name: 'app_commentairecinema_index', methods: ['GET'])]
    public function index(CommentairecinemaRepository $commentairecinemaRepository): Response
    {
        return $this->render('commentairecinema/index.html.twig', [
            'commentairecinemas' => $commentairecinemaRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_commentairecinema_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $commentairecinema = new Commentairecinema();
        $form = $this->createForm(CommentairecinemaType::class, $commentairecinema);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($commentairecinema);
            $entityManager->flush();

            return $this->redirectToRoute('app_commentairecinema_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('commentairecinema/new.html.twig', [
            'commentairecinema' => $commentairecinema,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_commentairecinema_show', methods: ['GET'])]
    public function show(Commentairecinema $commentairecinema): Response
    {
        return $this->render('commentairecinema/show.html.twig', [
            'commentairecinema' => $commentairecinema,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_commentairecinema_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Commentairecinema $commentairecinema, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(CommentairecinemaType::class, $commentairecinema);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_commentairecinema_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('commentairecinema/edit.html.twig', [
            'commentairecinema' => $commentairecinema,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_commentairecinema_delete', methods: ['POST'])]
    public function delete(Request $request, Commentairecinema $commentairecinema, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$commentairecinema->getId(), $request->request->get('_token'))) {
            $entityManager->remove($commentairecinema);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_commentairecinema_index', [], Response::HTTP_SEE_OTHER);
    }
}
