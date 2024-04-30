<?php

namespace App\Controller;

use App\Entity\Category;
use App\Form\CategoryType;
use App\Repository\CategoryRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/category')]
class CategoryController extends AbstractController
{
    #[Route('/', name: 'app_category_index', methods: ['GET'])]
    public function index(CategoryRepository $categoryRepository): Response
    {
        $form = $this->createForm(CategoryType::class, new Category());
        $updateForms = array();
        for ($i = 0; $i < count($categoryRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(CategoryType::class, $categoryRepository->findAll()[$i])->createView();
        }
        return $this->render('back/categoryTables.html.twig', [
            'categorys' => $categoryRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
        ]);
    }

    #[Route('/new', name: 'app_category_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager, CategoryRepository $categoryRepository): Response
    {
        $updateForms = array();
        for ($i = 0; $i < count($categoryRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(CategoryType::class, $categoryRepository->findAll()[$i])->createView();
        }
        $category = new Category();
        $form = $this->createForm(CategoryType::class, $category);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($category);
            $entityManager->flush();
            $this->addFlash('categorys', 'category added successfully');

            return $this->redirectToRoute('app_category_index', [], Response::HTTP_SEE_OTHER);
        }
        $hasErrorsCreate = true;
        return $this->render('back/categoryTables.html.twig', [
            'categorys' => $categoryRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
            'hasErrorsCreate' => $hasErrorsCreate
        ]);
    }

    #[Route('/{id}', name: 'app_category_show', methods: ['GET'])]
    public function show(Category $category): Response
    {
        return $this->render('category/show.html.twig', [
            'category' => $category,
        ]);
    }

    #[Route('/{id}/edit/{formUpdateNumber}', name: 'app_category_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Category $category, EntityManagerInterface $entityManager, $formUpdateNumber, CategoryRepository $categoryRepository): Response
    {
        $updateForms = array();
        $users = $categoryRepository->findAll();
        for ($i = 0; $i < count($users); $i++) {
            $updateForms[$i] = $this->createForm(CategoryType::class, $users[$i])->createView();
        }
        $form = $this->createForm(CategoryType::class, new Category());
        $updateform = $this->createForm(CategoryType::class, $category);
        $updateform->handleRequest($request);

        if ($updateform->isSubmitted() && $updateform->isValid()) {
            $entityManager->flush();
            $this->addFlash('categorys', 'category edited successfully');
            return $this->redirectToRoute('app_category_index', [], Response::HTTP_SEE_OTHER);
        }
        $entityManager->refresh($category);
        return $this->render('back/categoryTables.html.twig', [
            "formUpdateNumber" => $formUpdateNumber,
            'categorys' => $categoryRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
            'updateform' => $updateform->createView(),
        ]);
    }

    #[Route('/{id}', name: 'app_category_delete', methods: ['POST'])]
    public function delete(Request $request, Category $category, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete' . $category->getId(), $request->request->get('_token'))) {
            $entityManager->remove($category);
            $entityManager->flush();
            $this->addFlash('categorys','category deleted successfully');
        }

        return $this->redirectToRoute('app_category_index', [], Response::HTTP_SEE_OTHER);
    }
}
