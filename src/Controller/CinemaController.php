<?php

namespace App\Controller;

use App\Entity\Cinema;
use App\Form\CinemaType;
use App\Repository\CinemaRepository;
use App\Repository\UsersRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\Form\FormErrorIterator;
use Symfony\Component\Form\FormInterface;
use Symfony\Component\Form\FormError;
use Symfony\Component\Validator\Validator\ValidatorInterface;



#[Route('/cinema')]
class CinemaController extends AbstractController
{
    #[Route('/', name: 'app_cinema_index', methods: ['GET', 'POST'])]
    public function index(CinemaRepository $cinemaRepository, UsersRepository $usersRepository, Request $request, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(CinemaType::class, new Cinema());
        $updateForms = array();
        for ($i = 0; $i < count($cinemaRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(CinemaType::class, $cinemaRepository->findAll()[$i])->createView();
        }


        if (!empty($errors)) {
            // Afficher une alerte avec l'erreur
            $errorMessage = $errors[0]->getMessage();
            $this->addFlash('error', $errorMessage);
        }
        return $this->render('cinema/CinemasTable.html.twig', [
            'cinemas' => $cinemaRepository->findAll(),
            'users' => $usersRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,

        ]);
    }


    #[Route('/listeCinemaAdmin', name: 'app_cinemaAdmin_index', methods: ['GET', 'POST'])]
    public function listeCinemaAdmin(CinemaRepository $cinemaRepository, UsersRepository $usersRepository, Request $request, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(CinemaType::class, new Cinema());
        $updateForms = array();
        for ($i = 0; $i < count($cinemaRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(CinemaType::class, $cinemaRepository->findAll()[$i])->createView();
        }



        if (!empty($errors)) {
            // Afficher une alerte avec l'erreur
            $errorMessage = $errors[0]->getMessage();
            $this->addFlash('error', $errorMessage);
        }
        return $this->render('cinema/CinemasTableAdmin.html.twig', [
            'cinemas' => $cinemaRepository->findAll(),
            'users' => $usersRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,

        ]);
    }

    #[Route('/listecinema', name: 'app_cinema_liste', methods: ['GET'])]
    public function liste(CinemaRepository $cinemaRepository): Response
    {
        return $this->render('cinema/listCinema.html.twig', [
            'cinemas' => $cinemaRepository->findAll(),
        ]);
    }



    #[Route('/new', name: 'app_cinema_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager, UsersRepository $usersRepository, CinemaRepository $cinemaRepository): Response
    {

        $form = $this->createForm(CinemaType::class, new Cinema());
        $updateForms = array();
        for ($i = 0; $i < count($cinemaRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(CinemaType::class, $cinemaRepository->findAll()[$i])->createView();
        }
        
        $cinema = new Cinema();
        $form = $this->createForm(CinemaType::class, $cinema);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $imageFile = $form->get('logo')->getData();
            if ($imageFile) {
                $originalFilename = pathinfo($imageFile->getClientOriginalName(), PATHINFO_FILENAME);
                // this is needed to generate a unique file name based on original file name
                $safeFilename = preg_replace('/\s/', '_', $imageFile->getClientOriginalName());
                $safeFilename = strtolower(preg_replace('/[^\w\d.-]/', '', $safeFilename));

                $newFilename = 'img/cinemas/' . $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();

                try {
                    $imageFile->move(
                        $this->getParameter('APP_IMAGE_DIRECTORY'),
                        $newFilename
                    );
                } catch (FileException $e) {
                }
                $cinema->setLogo($newFilename);
            }
            $userId = 151;
            $cinema->setStatut('Pending');
            // Fetch the user by ID
            $user = $usersRepository->find($userId);
            if ($user->getRole() == 'responsable') {
                $cinema->setResponsable($user->getId());
            }
            $entityManager->persist($cinema);
            $entityManager->flush();
            return $this->redirectToRoute('app_cinema_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('cinema/CinemasTable.html.twig', [
            'cinemas' => $cinemaRepository->findAll(),
            'users' => $usersRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
        ]);
    }

    #[Route('/{idCinema}', name: 'app_cinema_show', methods: ['GET'])]
    public function show(Cinema $cinema): Response
    {
        return $this->render('cinema/show.html.twig', [
            'cinema' => $cinema,
        ]);
    }

    #[Route('/{idCinema}/edit', name: 'app_cinema_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Cinema $cinema, EntityManagerInterface $entityManager, CinemaRepository $cinemaRepository, UsersRepository $usersRepository): Response
    {
           $updateForms = array();
        for ($i = 0; $i < count($cinemaRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(CinemaType::class, $cinemaRepository->findAll()[$i])->createView();
        }
        $form = $this->createForm(CinemaType::class, $cinema);
        $form->handleRequest($request);
     

        if ($form->isSubmitted() && $form->isValid()) {
            $imageFile = $form->get('logo')->getData();
            if ($imageFile) {
                $originalFilename = pathinfo($imageFile->getClientOriginalName(), PATHINFO_FILENAME);
                // this is needed to generate a unique file name based on original file name
                $safeFilename = preg_replace('/\s/', '_', $imageFile->getClientOriginalName());
                $safeFilename = strtolower(preg_replace('/[^\w\d.-]/', '', $safeFilename));

                $newFilename = 'img/cinemas/' . $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();

                try {
                    $imageFile->move(
                        $this->getParameter('APP_IMAGE_DIRECTORY'),
                        $newFilename
                    );
                } catch (FileException $e) {
                }
                $cinema->setLogo($newFilename);
            }
            $entityManager->flush();

            return $this->redirectToRoute('app_cinema_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('cinema/CinemasTable.html.twig', [
            'cinemas' => $cinemaRepository->findAll(),
            'users' => $usersRepository->findAll(),
            'form' => $form->createView(), // Utilisez le formulaire original pour afficher les erreurs dans le modal
            'updateForms' => $updateForms,

        ]);
    }

    #[Route('/{idCinema}', name: 'app_cinema_delete', methods: ['POST'])]
    public function delete(Request $request, Cinema $cinema, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete' . $cinema->getIdCinema(), $request->request->get('_token'))) {
            $entityManager->remove($cinema);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_cinema_index', [], Response::HTTP_SEE_OTHER);
    }


    #[Route('Accept/{idCinema}', name: 'app_cinema_accept', methods: ['POST'])]
    public function Accept(Request $request, Cinema $cinema, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('accept' . $cinema->getIdCinema(), $request->request->get('_token'))) {
            // Mettre à jour le statut du cinéma
            $cinema->setStatut('Accepted');
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_cinemaAdmin_index', [], Response::HTTP_SEE_OTHER);
    }

    #[Route('reject/{idCinema}', name: 'app_cinema_reject', methods: ['POST'])]
    public function reject(Request $request, Cinema $cinema, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('reject' . $cinema->getIdCinema(), $request->request->get('_token'))) {
            $entityManager->remove($cinema);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_cinemaAdmin_index', [], Response::HTTP_SEE_OTHER);
    }



}
