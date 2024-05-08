<?php

namespace App\Controller;

use App\Entity\Cinema;
use App\Form\CinemaType;
use App\Repository\CinemaRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;



#[Route('/cinema')]
class CinemaController extends AbstractController
{
    #[Route('/', name: 'app_cinema_index', methods: ['GET', 'POST'])]
    public function index(CinemaRepository $cinemaRepository, Request $request, EntityManagerInterface $entityManager): Response
{
    // Get the currently logged-in user
    $user = $this->getUser()->getId();
    
    // Fetch cinemas where the responsible ID matches the ID of the current user
    $userCinemas = $cinemaRepository->findBy(['responsable' => $user]);
    
    // Create form for adding a new cinema
    $form = $this->createForm(CinemaType::class, new Cinema());
    
    // Create forms for updating existing cinemas
    $updateForms = [];
    foreach ($userCinemas as $cinema) {
        $updateForms[] = $this->createForm(CinemaType::class, $cinema)->createView();
    }
    
    // Handle errors (if any)
    if (!empty($errors)) {
        // Afficher une alerte avec l'erreur
        $errorMessage = $errors[0]->getMessage();
        $this->addFlash('error', $errorMessage);
    }
    
    return $this->render('back/CinemasTable.html.twig', [
        'cinemas' => $userCinemas,
        'form' => $form->createView(),
        'updateForms' => $updateForms,
    ]);
}


    #[Route('/location/{idCinema}', name: 'app_cinema_location', methods: ['GET', 'POST'])]
    public function localiser(int $idCinema): Response
    {
        $entityManager = $this->getDoctrine()->getManager();
        $cinemaRepository = $entityManager->getRepository(Cinema::class);
        $cinema = $cinemaRepository->find($idCinema);

        if (!$cinema) {
            throw $this->createNotFoundException('Cinema not found');
        }

        $adresseCinema = $cinema->getAdresse();
        $nomCinema = $cinema->getNom();

        return $this->render('front/Map.html.twig', [
            'adresseCinema' => $adresseCinema,
            'nomCinema' => $nomCinema,
        ]);
    }


    #[Route('/listeCinemaAdmin', name: 'app_cinemaAdmin_index', methods: ['GET', 'POST'])]
    public function listeCinemaAdmin(CinemaRepository $cinemaRepository, Request $request, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(CinemaType::class, new Cinema());
        $updateForms = array();
        for ($i = 0; $i < count($cinemaRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(CinemaType::class, $cinemaRepository->findAll()[$i])->createView();
        }



        if (!empty($errors)) {
            $errorMessage = $errors[0]->getMessage();
            $this->addFlash('error', $errorMessage);
        }
        return $this->render('back/CinemasTableAdmin.html.twig', [
            'cinemas' => $cinemaRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,

        ]);
    }

    #[Route('/listecinema', name: 'app_cinema_liste', methods: ['GET'])]
    public function liste(CinemaRepository $cinemaRepository): Response
    {
        return $this->render('front/listCinema.html.twig', [
            'cinemas' => $cinemaRepository->findAll(),
        ]);
    }



    #[Route('/new', name: 'app_cinema_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager,CinemaRepository $cinemaRepository): Response
    {

        $cinema = new Cinema();
        $updateForms = array();
        for ($i = 0; $i < count($cinemaRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(CinemaType::class, $cinemaRepository->findAll()[$i])->createView();
        }

        $form = $this->createForm(CinemaType::class, $cinema);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $imageFile = $form->get('logo')->getData();
            if ($imageFile) {
                $safeFilename = preg_replace('/\s/', '_', $imageFile->getClientOriginalName());
                $safeFilename = strtolower(preg_replace('/[^\w\d.-]/', '', $safeFilename));

                $newFilename = 'img/cinemas/' . $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();

                try {
                    $imageFile->move(
                        $this->getParameter('kernel.project_dir').'/public/img/cinemas',
                        $newFilename
                    );
                } catch (FileException $e) {
                }
                $cinema->setLogo($newFilename);
            }
            $cinema->setStatut('Pending');
            $cinema->setResponsable($this->getUser()->getId());
            $entityManager->persist($cinema);
            $entityManager->flush();
            return $this->redirectToRoute('app_cinema_index', [], Response::HTTP_SEE_OTHER);
        }
        $hasErrorsCreate = true;
        return $this->render('back/CinemasTable.html.twig', [
            'cinemas' => $cinemaRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
            'hasErrorsCreate' => $hasErrorsCreate,
        ]);
    }

    #[Route('/{idCinema}', name: 'app_cinema_show', methods: ['GET'])]
    public function show(Cinema $cinema): Response
    {
        return $this->render('cinema/show.html.twig', [
            'cinema' => $cinema,
        ]);
    }

    #[Route('/{idCinema}/edit/{formUpdateNumber}/', name: 'app_cinema_edit', methods: ['GET', 'POST'])]
    public function edit($formUpdateNumber, Request $request, Cinema $cinema, EntityManagerInterface $entityManager, CinemaRepository $cinemaRepository): Response
    {
        $updateForms = array();
        $cinemas = $cinemaRepository->findAll();
        for ($i = 0; $i < count($cinemas); $i++) {
            $updateForms[$i] = $this->createForm(CinemaType::class, $cinemas[$i])->createView();
        }
        $form = $this->createForm(CinemaType::class, new Cinema());

        $updateform = $this->createForm(CinemaType::class, $cinema);

        $updateform->handleRequest($request);


        if ($updateform->isSubmitted() && $updateform->isValid()) {
            $imageFile = $updateform->get('logo')->getData();
            if ($imageFile) {
                $safeFilename = preg_replace('/\s/', '_', $imageFile->getClientOriginalName());
                $safeFilename = strtolower(preg_replace('/[^\w\d.-]/', '', $safeFilename));

                $newFilename = 'img/cinemas/' . $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();

                try {
                    $imageFile->move(
                        $this->getParameter('kernel.project_dir').'/public/img/cinemas',
                        $newFilename
                    );
                } catch (FileException $e) {
                }
                $cinema->setLogo($newFilename);
            }
            $entityManager->flush();

            return $this->redirectToRoute('app_cinema_index', [], Response::HTTP_SEE_OTHER);
        }
        $entityManager->refresh($cinema);
        return $this->render('back/CinemasTable.html.twig', [
            'cinemas' => $cinemaRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
            "formUpdateNumber" => $formUpdateNumber,
            'updateform' => $updateform->createView(),

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


    #[Route('/Accept/{idCinema}', name: 'app_cinema_accept', methods: ['POST'])]
    public function Accept(Request $request, Cinema $cinema, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('accept' . $cinema->getIdCinema(), $request->request->get('_token'))) {
            // Mettre à jour le statut du cinéma
            $cinema->setStatut('Accepted');
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_cinemaAdmin_index', [], Response::HTTP_SEE_OTHER);
    }

    #[Route('/reject/{idCinema}', name: 'app_cinema_reject', methods: ['POST'])]
    public function reject(Request $request, Cinema $cinema, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('reject' . $cinema->getIdCinema(), $request->request->get('_token'))) {
            $entityManager->remove($cinema);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_cinemaAdmin_index', [], Response::HTTP_SEE_OTHER);
    }
}
