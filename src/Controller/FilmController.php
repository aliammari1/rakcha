<?php

namespace App\Controller;

use App\Entity\Film;
use App\Form\FilmType;
use App\Repository\FilmRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/film')]
class FilmController extends AbstractController
{
    #[Route('/', name: 'app_film_index', methods: ['GET'])]
    public function index(FilmRepository $filmRepository): Response
    {
        $form = $this->createForm(FilmType::class, new Film());
        $updateForms = array();
        for ($i = 0; $i < count($filmRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(FilmType::class, $filmRepository->findAll()[$i])->createView();
        }
        return $this->render('back/filmTables.html.twig', [
            'films' => $filmRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
        ]);
    }
    #[Route('/new', name: 'app_film_new', methods: ['POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager, FilmRepository $filmRepository): Response
    {
        $updateForms = array();
        for ($i = 0; $i < count($filmRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(FilmType::class, $filmRepository->findAll()[$i])->createView();
        } 
        $film = new Film();
        $form = $this->createForm(FilmType::class, $film);
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
            $file->move($this->getParameter('kernel.project_dir') . "/public/img/films", $filename);
            $film->setImage("/img/films/" . $filename);
        }
            $entityManager->persist($film);//creation the query of create 
            $entityManager->flush();//execute the query
            
            
            $this->addFlash('films','film added successfully');

            return $this->redirectToRoute('app_film_index', [], Response::HTTP_SEE_OTHER);
        }
        $hasErrorsCreate = true;
        return $this->render('back/filmTables.html.twig', [
            'films' => $filmRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
            'hasErrorsCreate' => $hasErrorsCreate
        ]);

    }

    #[Route('/{id}', name: 'app_film_show', methods: ['GET'])]
    public function show(Film $film): Response
    {
        return $this->render('film/show.html.twig', [
            'film' => $film,
        ]);
    }

    #[Route('/{id}/edit/{formUpdateNumber}', name: 'app_film_edit', methods: ['POST'])]
    public function edit(Request $request, Film $film, $formUpdateNumber, EntityManagerInterface $entityManager, FilmRepository $filmRepository): Response
    {
        $updateForms = array();
        $films = $filmRepository->findAll();
        for ($i = 0; $i < count($films); $i++) {
            $updateForms[$i] = $this->createForm(FilmType::class, $films[$i])->createView();
        }
        $form = $this->createForm(FilmType::class, new Film());
        $updateform = $this->createForm(FilmType::class, $film);
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
            $file->move($this->getParameter('kernel.project_dir') . "/public/img/films", $filename);
            $film->setImage("/img/films/" . $filename);  
        }   
            $entityManager->flush();
            $this->addFlash('films','film edited successfully');
            return $this->redirectToRoute('app_film_index', [], Response::HTTP_SEE_OTHER);
        }
        $entityManager->refresh($film);
        return $this->render('back/filmTables.html.twig', [
            "formUpdateNumber" => $formUpdateNumber,
            'films' => $filmRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
            'updateform' => $updateform->createView(),
        ]);

    }

    #[Route('/{id}', name: 'app_film_delete', methods: ['POST'])]
    public function delete(Request $request, Film $film, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete' . $film->getId(), $request->request->get('_token'))) {
            $entityManager->remove($film);
            $entityManager->flush();
            $this->addFlash('films','film deleted successfully');
        }
        return $this->redirectToRoute('app_film_index', [], Response::HTTP_SEE_OTHER);
    }
}
