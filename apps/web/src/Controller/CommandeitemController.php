<?php

namespace App\Controller;

use App\Entity\Commandeitem;
use App\Form\CommandeitemType;
use App\Repository\CommandeitemRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/commandeitem')]
class CommandeitemController extends AbstractController
{
    #[Route('/', name: 'app_commandeitem_index', methods: ['GET'])]
    public function index(CommandeitemRepository $commandeitemRepository): Response
    {
        return $this->render('commandeitem/index.html.twig', [
            'commandeitems' => $commandeitemRepository->findAll(),
        ]);
    }

    #[Route('/new', name: 'app_commandeitem_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $commandeitem = new Commandeitem();
        $form = $this->createForm(CommandeitemType::class, $commandeitem);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($commandeitem);
            $entityManager->flush();

            return $this->redirectToRoute('app_commandeitem_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('commandeitem/new.html.twig', [
            'commandeitem' => $commandeitem,
            'form' => $form,
        ]);
    }

    #[Route('/{idcommandeitem}', name: 'app_commandeitem_show', methods: ['GET'])]
    public function show(Commandeitem $commandeitem): Response
    {
        return $this->render('commandeitem/show.html.twig', [
            'commandeitem' => $commandeitem,
        ]);
    }

    #[Route('/{idcommandeitem}/edit', name: 'app_commandeitem_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Commandeitem $commandeitem, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(CommandeitemType::class, $commandeitem);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_commandeitem_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('commandeitem/edit.html.twig', [
            'commandeitem' => $commandeitem,
            'form' => $form,
        ]);
    }

    #[Route('/{idcommandeitem}', name: 'app_commandeitem_delete', methods: ['POST'])]
    public function delete(Request $request, Commandeitem $commandeitem, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete' . $commandeitem->getIdcommandeitem(), $request->request->get('_token'))) {
            $entityManager->remove($commandeitem);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_commandeitem_index', [], Response::HTTP_SEE_OTHER);
    }
}
