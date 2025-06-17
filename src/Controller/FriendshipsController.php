<?php

namespace App\Controller;

use App\Entity\Friendships;
use App\Repository\FriendshipsRepository;
use App\Repository\UsersRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/friendships')]
class FriendshipsController extends AbstractController
{
    #[Route('/', name: 'app_friendships_index', methods: ['GET'])]
    public function index(FriendshipsRepository $friendshipsRepository): Response
    {
        return $this->render('friendships/index.html.twig', [
            'friendships' => $friendshipsRepository->findAll(),
        ]);
    }

    #[Route('/sentfriendRequest', name: 'send_friend_request', methods: ['POST'])]
    public function send(Request $request, EntityManagerInterface $entityManager, UsersRepository $usersRepository): Response
    {
        try {
            $this->denyAccessUnlessGranted('IS_AUTHENTICATED_FULLY');
            $data = json_decode($request->getContent(), true);
            $friendship = new Friendships();
            $friendship->setSender($this->getUser());
            $friendship->setReceiver($usersRepository->findOneById($data['receiver']));
            $friendship->setStatut('pending friend request');
            $entityManager->persist($friendship);
            $entityManager->flush();
        } catch (\Exception $e) {
            return new JsonResponse(['success' => false, 'error' => $e->getMessage()], Response::HTTP_INTERNAL_SERVER_ERROR);
        }
        return new JsonResponse(['success' => true], Response::HTTP_OK);
    }

    #[Route('/{id}', name: 'app_friendships_show', methods: ['GET'])]
    public function show(Friendships $friendship): Response
    {
        return $this->render('friendships/show.html.twig', [
            'friendship' => $friendship,
        ]);
    }

    #[Route('/acceptFriendRequest', name: 'accept_friend_request', methods: ['GET', 'POST'])]
    public function accept(Request $request, FriendshipsRepository $friendshipsRepository, EntityManagerInterface $entityManager, UsersRepository $usersRepository): Response
    {
        $this->denyAccessUnlessGranted('IS_AUTHENTICATED_FULLY');
        $data = json_decode($request->getContent(), true);
        $friendship = $friendshipsRepository->findOneBy(['sender' => $usersRepository->findOneById($data['receiver']), 'receiver' => $this->getUser()]);
        $friendship->setStatut('accepted friend request');
        $entityManager->persist($friendship);
        $entityManager->flush();
        return new JsonResponse(['success' => true], Response::HTTP_OK);
    }

    #[Route('/cancelFriendRequest', name: 'cancel_friend_request', methods: ['POST'])]
    public function cancel(Request $request, FriendshipsRepository $friendshipsRepository, EntityManagerInterface $entityManager, UsersRepository $usersRepository): Response
    {
        $this->denyAccessUnlessGranted('IS_AUTHENTICATED_FULLY');
        $data = json_decode($request->getContent(), true);
        $friendship = $friendshipsRepository->findOneBy(['sender' => $this->getUser(), 'receiver' => $usersRepository->findOneById($data['receiver'])]);
        $friendship->setStatut('cancel friend request');
        $entityManager->remove($friendship);
        $entityManager->flush();
        return new JsonResponse(['success' => true], Response::HTTP_OK);
    }


    #[Route('/rejectFriendRequest', name: 'reject_friend_request', methods: ['POST'])]
    public function reject(Request $request, FriendshipsRepository $friendshipsRepository, EntityManagerInterface $entityManager, UsersRepository $usersRepository): Response
    {
        $this->denyAccessUnlessGranted('IS_AUTHENTICATED_FULLY');
        $data = json_decode($request->getContent(), true);
        $friendship = $friendshipsRepository->findOneBy(['sender' => $this->getUser(), 'receiver' => $usersRepository->findOneById($data['receiver'])]);
        $friendship->setStatut('cancel friend request');
        $entityManager->remove($friendship);
        $entityManager->flush();
        return new JsonResponse(['success' => true], Response::HTTP_OK);
    }
}
