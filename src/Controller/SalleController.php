<?php

namespace App\Controller;

use App\Entity\Salle;
use App\Form\SalleType;
use App\Repository\CinemaRepository;
use App\Repository\SalleRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use App\Entity\Cinema;
use Symfony\Component\Form\FormErrorIterator;
use Symfony\Component\Form\FormInterface;
use Symfony\Component\Form\FormError;

#[Route('/salle')]
class SalleController extends AbstractController
{
    #[Route('/{idCinema}', name: 'app_salle_index', methods: ['GET', 'POST'])]
    public function index(int $idCinema, SalleRepository $salleRepository, Request $request, EntityManagerInterface $entityManager): Response
{
    $cinema = $entityManager->getRepository(Cinema::class)->find($idCinema);

    // Option 1 - Get all salles only related to the current cinema
    $salles = $salleRepository->findBy(['idCinema' => $idCinema]);

    // Option 2 - Only show the form and updateForms when a single cinema is active
    // In this case, you need to pass the `$cinema` to the templates
    $form = $this->createForm(SalleType::class, new Salle());
    $updateForms = array();
    for ($i = 0; $i < count($salles); $i++) {
        $updateForms[$i] = $this->createForm(SalleType::class, $salles[$i])->createView();
    }

    // Passer les erreurs de validation au modèle
    $errors = $this->getErrorsFromForm($form);

    if (!empty($errors)) {
        // Afficher une alerte avec l'erreur
        $errorMessage = $errors[0]->getMessage();
        $this->addFlash('error', $errorMessage);
        return $this->redirectToRoute('app_salle_index', [], Response::HTTP_SEE_OTHER);

    }


    if ($cinema) {
        return $this->render('salle/SallesTable.html.twig', [
            'salles' => $salles,
            'form' => $form->createView(),
            'updateForms' => $updateForms,
            'cinema' => $cinema,
            'errors' => $errors, // Passer les erreurs au modèle Twig
        ]);
    }

     
    return $this->render('salle/SallesTable.html.twig', [
        'salles' => [],
        'form' => $form->createView(),
        'updateForms' => [],
        'cinema' => null,
        'errors' => $errors, // Passer les erreurs au modèle Twig
    ]);
}

#[Route('/{idCinema}/new', name: 'app_salle_new', methods: ['GET', 'POST'])]

public function new(int $idCinema, Request $request, EntityManagerInterface $entityManager): Response
{
    $cinema = $entityManager->find(Cinema::class, $idCinema);
    if (!$cinema) {
        throw $this->createNotFoundException('No cinema found for id ' . $idCinema);
    }

    $salle = new Salle();
    $salle->setCinema($cinema); // Assigner directement l'objet cinema à la salle

    $form = $this->createForm(SalleType::class, $salle);
    $form->handleRequest($request);

    if ($form->isSubmitted() && $form->isValid()) {
        $entityManager->persist($salle);
        $entityManager->flush();

        return $this->redirectToRoute('app_salle_index', ['idCinema' => $idCinema], Response::HTTP_SEE_OTHER);
    }

    // Passer les erreurs de validation au modèle
    $errors = $this->getErrorsFromForm($form);

    if (!empty($errors)) {
        // Afficher une alerte avec l'erreur
        $errorMessage = $errors[0]->getMessage();
        $this->addFlash('error', $errorMessage);
        return $this->redirectToRoute('app_salle_index', ['idCinema' => $idCinema], Response::HTTP_SEE_OTHER);
    }

    return $this->renderForm('salle/new.html.twig', [
        'salle' => $salle,
        'form' => $form,
    ]);
}




    /**
 * Récupère les erreurs de validation du formulaire.
 *
 * @param FormInterface $form Le formulaire
 * @return FormError[] Les erreurs de validation
 */
private function getErrorsFromForm(FormInterface $form): array
{
    $errors = [];

    // Récupérer les erreurs de chaque champ
    foreach ($form as $child) {
        /** @var FormErrorIterator $childErrors */
        $childErrors = $child->getErrors(true, false);

        // Ajouter chaque erreur à la liste des erreurs
        foreach ($childErrors as $error) {
            $errors[] = $error;
        }
    }

    return $errors;
}

    #[Route('/{idSalle}', name: 'app_salle_show', methods: ['GET'])]
    public function show(Salle $salle): Response
    {
        return $this->render('salle/show.html.twig', [
            'salle' => $salle,
        ]);
    }

    #[Route('/{idCinema}/{idSalle}/edit', name: 'app_salle_edit', methods: ['GET', 'POST'])]
    public function edit(int $idCinema, Request $request, Salle $salle, EntityManagerInterface $entityManager): Response
    {
        $cinema = $entityManager->find(Cinema::class, $idCinema);
        if (!$cinema) {
            throw  $this->createNotFoundException('No cinema found for id'.$idCinema);
        }
        $form = $this->createForm(SalleType::class, $salle);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_salle_index', ['idCinema' => $idCinema], Response::HTTP_SEE_OTHER);
        }

        // Passer les erreurs de validation au modèle
    $errors = $this->getErrorsFromForm($form);

    if (!empty($errors)) {
        // Afficher une alerte avec l'erreur
        $errorMessage = $errors[0]->getMessage();
        $this->addFlash('error', $errorMessage);
        return $this->redirectToRoute('app_salle_index', ['idCinema' => $idCinema], Response::HTTP_SEE_OTHER);
    }

        return $this->renderForm('salle/edit.html.twig', [
            'salle' => $salle,
            'form' => $form,
        ]);
    }

    #[Route('/{idCinema}/{idSalle}', name: 'app_salle_delete', methods: ['POST'])]
    public function delete(int $idCinema, Request $request, Salle $salle, EntityManagerInterface $entityManager): Response
    {
        $cinema = $entityManager->find(Cinema::class, $idCinema);
        if (!$cinema) {
            throw  $this->createNotFoundException('No cinema found for id'.$idCinema);
        }
        if ($this->isCsrfTokenValid('delete'.$salle->getIdSalle(), $request->request->get('_token'))) {
            $entityManager->remove($salle);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_salle_index', ['idCinema' => $idCinema], Response::HTTP_SEE_OTHER);
    }
}
