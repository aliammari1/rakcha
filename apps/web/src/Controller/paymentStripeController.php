<?php

namespace App\Controller;

use App\Repository\SeatRepository;
use Doctrine\ORM\EntityManagerInterface;
use Doctrine\Persistence\ManagerRegistry;
use Exception;
use Stripe\Charge;
use Stripe\Stripe;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class paymentStripeController extends AbstractController
{
    #[Route('/stripe', name: 'app_payment_stripe')]
    public function index(): Response
    {
        return $this->render('front/paymentStripe.html.twig', [
            'controller_name' => 'StripeController',
            'stripe_key' => $_ENV["STRIPE_KEY"],
        ]);
    }

    #[Route('/stripe/create-charge', name: 'app_stripe_charge', methods: ['POST'])]
    public function createCharge(Request $request, SeatRepository $seatRepository, EntityManagerInterface $entityManager): Response
    {
        $this->denyAccessUnlessGranted('IS_AUTHENTICATED_REMEMBERED');

        $data = json_decode($request->getContent(), true);
        if (!is_array($data) || empty($data['seatIds']) || !is_array($data['seatIds']) || empty($data['stripeToken'])) {
            return $this->json(['success' => false, 'message' => 'Invalid payment payload.'], Response::HTTP_BAD_REQUEST);
        }

        $prix = filter_var($data['prix'] ?? null, FILTER_VALIDATE_FLOAT);
        if ($prix === false || $prix <= 0) {
            return $this->json(['success' => false, 'message' => 'Invalid price amount.'], Response::HTTP_BAD_REQUEST);
        }

        $entityManager->beginTransaction();
        try {
            // Check seat availability before charging payment to prevent double booking
            $seatsToReserve = [];
            foreach ($data['seatIds'] as $seatId) {
                $seat = $seatRepository->findOneBy(['id' => $seatId]);
                if (!$seat) {
                    $entityManager->rollback();
                    return $this->json(['success' => false, 'message' => "Seat {$seatId} does not exist."], Response::HTTP_NOT_FOUND);
                }
                if ($seat->getStatut() === 'reserve') {
                    $entityManager->rollback();
                    return $this->json(['success' => false, 'message' => "Seat {$seatId} is already reserved."], Response::HTTP_CONFLICT);
                }
                $seatsToReserve[] = $seat;
            }

            Stripe::setApiKey($_ENV["STRIPE_SECRET_KEY"]);
            Charge::create([
                "amount" => (int) round($prix * 100),
                "currency" => "usd",
                "source" => $data['stripeToken'],
                "description" => "Rakcha Cinema Ticket Payment"
            ]);

            foreach ($seatsToReserve as $seat) {
                $seat->setStatut("reserve");
                $entityManager->persist($seat);
            }
            $entityManager->flush();
            $entityManager->commit();

        } catch (Exception $e) {
            if ($entityManager->getConnection()->isTransactionActive()) {
                $entityManager->rollback();
            }
            return $this->json(['success' => false, 'message' => $e->getMessage(), 'data' => $data], Response::HTTP_INTERNAL_SERVER_ERROR);
        }

        return $this->json(['success' => true, 'data' => $data]);
    }


}
