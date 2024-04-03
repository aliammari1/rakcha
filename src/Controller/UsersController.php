<?php

namespace App\Controller;

use App\Entity\Users;
use App\Form\UsersType;
use App\Repository\UsersRepository;
use Doctrine\ORM\EntityManagerInterface;
use Knp\Component\Pager\PaginatorInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/users')]
class UsersController extends AbstractController
{
    #[Route('/', name: 'app_users_index', methods: ['GET', 'POST'])]
    public function index(UsersRepository $usersRepository, EntityManagerInterface $em, PaginatorInterface $paginator, Request $request): Response
    {
        $form = $this->createForm(UsersType::class, new Users());
        $updateForms = array();
        for ($i = 0; $i < count($usersRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(UsersType::class, $usersRepository->findAll()[$i])->createView();
        }
        $users = $usersRepository->findAll();
        $pagination = $paginator->paginate(
            $users, /* query NOT result */
            $request->query->getInt('page', 1), /*page number*/
            5 /*limit per page*/
        );


        return $this->render('back/UserTables.html.twig', [
            'pagination' => $pagination,
            'users' => $users,
            'form' => $form->createView(),
            'updateForms' => $updateForms,
        ]);
    }
    #[Route('/profile', name: 'app_profile_index', methods: ['GET', 'POST'])]
    public function profile(UsersRepository $usersRepository, Request $request, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(UsersType::class, new Users());
        $updateForms = array();
        for ($i = 0; $i < count($usersRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(UsersType::class, $usersRepository->findAll()[$i])->createView();
        }
        return $this->render('back/profile.html.twig', [
            'users' => $usersRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
        ]);
    }

    #[Route('/new', name: 'app_users_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager, UsersRepository $usersRepository): Response
    {
        $user = new Users();
        $updateForms = array();
        for ($i = 0; $i < count($usersRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(UsersType::class, $usersRepository->findAll()[$i])->createView();
        }
        $form = $this->createForm(UsersType::class, $user);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $file = $form['photoDeProfil']->getData();

            $extension = $file->guessExtension();
            if (!$extension) {
                // extension cannot be guessed
                $extension = 'bin';
            }
            $filename = rand(1, 99999) . '.' . $extension;
            $file->move($this->getParameter('kernel.project_dir') . "/public/img/users", $filename);
            $user->setPhotoDeProfil("/img/users/" . $filename);
            $user->setIsVerified(false);
            if ($user->getRole() == 'client')
                $user->setRoles(['ROLE_CLIENT']);
            else if ($user->getRole() == 'admin')
                $user->setRoles(['ROLE_ADMIN']);
            else if ($user->getRole() == 'responsableDeCinema')
                $user->setRoles(['ROLE_RESPONSABLE_DE_CINEMA']);
            $entityManager->persist($user);
            $entityManager->flush();
            return $this->redirectToRoute('app_users_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->render('back/UserTables.html.twig', [
            'users' => $usersRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
        ]);
    }

    #[Route('/{id}', name: 'app_users_show', methods: ['GET'])]
    public function show(Users $user): Response
    {
        return new Response("<h1>show</h1>");
    }

    #[Route('/{id}/edit', name: 'app_users_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Users $user, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(UsersType::class, $user);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();
            return $this->redirectToRoute('app_users_index', [], Response::HTTP_SEE_OTHER);
        }

        return new Response("<h1>Edit</h1>");
    }

    #[Route('/{id}', name: 'app_users_delete', methods: ['POST'])]
    public function delete(Request $request, Users $user, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete' . $user->getId(), $request->request->get('_token'))) {
            $entityManager->remove($user);
            $entityManager->flush();
            $this->addFlash('users', 'User deleted successfully');
        }

        return $this->redirectToRoute('app_users_index', [], Response::HTTP_SEE_OTHER);
    }
}
