<?php

namespace App\Controller;

use App\Entity\Friendships;
use App\Entity\Users;
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
            $data = json_decode($request->getContent(), true, 512, \JSON_THROW_ON_ERROR);
            $receiverId = $data['receiver'] ?? null;
            if (!is_int($receiverId) && !ctype_digit((string) $receiverId)) {
                throw $this->createNotFoundException('Receiver not found.');
            }
            $receiver = $usersRepository->find((int) $receiverId);
            if (!$receiver instanceof Users) {
                throw $this->createNotFoundException('Receiver not found.');
            }
            $friendship = new Friendships();
            $friendship->setSender($this->currentUser());
            $friendship->setReceiver($receiver);
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
        $data = json_decode($request->getContent(), true, 512, \JSON_THROW_ON_ERROR);
        $friendship = $friendshipsRepository->findOneBy(['sender' => $this->receiverFromRequest($data, $usersRepository), 'receiver' => $this->currentUser()]);
        if (!$friendship instanceof Friendships) {
            throw $this->createNotFoundException('Friend request not found.');
        }
        $friendship->setStatut('accepted friend request');
        $entityManager->persist($friendship);
        $entityManager->flush();

        return new JsonResponse(['success' => true], Response::HTTP_OK);
    }

    #[Route('/cancelFriendRequest', name: 'cancel_friend_request', methods: ['POST'])]
    public function cancel(Request $request, FriendshipsRepository $friendshipsRepository, EntityManagerInterface $entityManager, UsersRepository $usersRepository): Response
    {
        $this->denyAccessUnlessGranted('IS_AUTHENTICATED_FULLY');
        $data = json_decode($request->getContent(), true, 512, \JSON_THROW_ON_ERROR);
        $friendship = $friendshipsRepository->findOneBy(['sender' => $this->currentUser(), 'receiver' => $this->receiverFromRequest($data, $usersRepository)]);
        if (!$friendship instanceof Friendships) {
            throw $this->createNotFoundException('Friend request not found.');
        }
        $friendship->setStatut('cancel friend request');
        $entityManager->remove($friendship);
        $entityManager->flush();

        return new JsonResponse(['success' => true], Response::HTTP_OK);
    }

    #[Route('/rejectFriendRequest', name: 'reject_friend_request', methods: ['POST'])]
    public function reject(Request $request, FriendshipsRepository $friendshipsRepository, EntityManagerInterface $entityManager, UsersRepository $usersRepository): Response
    {
        $this->denyAccessUnlessGranted('IS_AUTHENTICATED_FULLY');
        $data = json_decode($request->getContent(), true, 512, \JSON_THROW_ON_ERROR);
        $friendship = $friendshipsRepository->findOneBy(['sender' => $this->currentUser(), 'receiver' => $this->receiverFromRequest($data, $usersRepository)]);
        if (!$friendship instanceof Friendships) {
            throw $this->createNotFoundException('Friend request not found.');
        }
        $friendship->setStatut('cancel friend request');
        $entityManager->remove($friendship);
        $entityManager->flush();

        return new JsonResponse(['success' => true], Response::HTTP_OK);
    }

    private function currentUser(): Users
    {
        $user = $this->getUser();
        if (!$user instanceof Users) {
            throw $this->createAccessDeniedException();
        }

        return $user;
    }

    /** @param array<string, mixed> $data */
    private function receiverFromRequest(array $data, UsersRepository $usersRepository): Users
    {
        $receiverId = $data['receiver'] ?? null;
        if (!is_int($receiverId) && !ctype_digit((string) $receiverId)) {
            throw $this->createNotFoundException('Receiver not found.');
        }

        $receiver = $usersRepository->find((int) $receiverId);
        if (!$receiver instanceof Users) {
            throw $this->createNotFoundException('Receiver not found.');
        }

        return $receiver;
    }
}
