<?php

namespace App\Controller;

use App\Entity\Seance;
use App\Entity\Seat;
use App\Repository\SeanceRepository;
use App\Repository\SeatRepository;
use Doctrine\DBAL\LockMode;
use Doctrine\ORM\EntityManagerInterface;
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
            'stripe_key' => $_ENV['STRIPE_KEY'] ?? '',
        ]);
    }

    #[Route('/stripe/create-charge', name: 'app_stripe_charge', methods: ['POST'])]
    public function createCharge(
        Request $request,
        SeatRepository $seatRepository,
        SeanceRepository $seanceRepository,
        EntityManagerInterface $entityManager,
    ): Response {
        $this->denyAccessUnlessGranted('IS_AUTHENTICATED_REMEMBERED');

        $data = json_decode($request->getContent(), true);
        if (!is_array($data) || empty($data['seatIds']) || !is_array($data['seatIds']) || empty($data['stripeToken']) || empty($data['seanceId'])) {
            return $this->json(['success' => false, 'message' => 'Invalid payment payload: seanceId, seatIds, and stripeToken are required.'], Response::HTTP_BAD_REQUEST);
        }

        // Authoritative Seance lookup: determine seat price strictly from database
        $seance = $seanceRepository->find($data['seanceId']);
        if (!$seance) {
            return $this->json(['success' => false, 'message' => 'Seance not found.'], Response::HTTP_NOT_FOUND);
        }

        $seatPrice = $seance->getPrix();
        if (null === $seatPrice || $seatPrice <= 0) {
            return $this->json(['success' => false, 'message' => 'Invalid seance ticket price.'], Response::HTTP_BAD_REQUEST);
        }

        // Sort and deduplicate seat IDs to ensure deterministic lock acquisition order and prevent deadlocks
        $seatIds = array_values(array_unique(array_map('intval', $data['seatIds'])));
        sort($seatIds, \SORT_NUMERIC);

        // Concurrency-safe seat reservation using pessimistic write locking
        $entityManager->beginTransaction();
        try {
            $seatsToReserve = [];
            $seanceSalle = $seance->getIdSalle();

            foreach ($seatIds as $seatId) {
                // Acquire pessimistic write lock to prevent race conditions & double-booking
                $seat = $entityManager->find(Seat::class, $seatId, LockMode::PESSIMISTIC_WRITE);
                if (!$seat) {
                    $entityManager->rollback();

                    return $this->json(['success' => false, 'message' => "Seat {$seatId} does not exist."], Response::HTTP_NOT_FOUND);
                }

                // Ensure seat belongs to the seance's room
                if (null !== $seanceSalle && null !== $seat->getSalle() && $seat->getSalle()->getIdSalle() !== $seanceSalle->getIdSalle()) {
                    $entityManager->rollback();

                    return $this->json(['success' => false, 'message' => "Seat {$seatId} does not belong to this session's hall."], Response::HTTP_BAD_REQUEST);
                }

                if ('reserve' === $seat->getStatut()) {
                    $entityManager->rollback();

                    return $this->json(['success' => false, 'message' => "Seat {$seatId} is already reserved."], Response::HTTP_CONFLICT);
                }

                $seatsToReserve[] = $seat;
            }

            // Calculate authoritative total strictly from database data (client 'prix' is ignored)
            $authoritativeTotal = count($seatsToReserve) * $seatPrice;

            // Execute Stripe charge with server-derived amount
            $this->executeStripeCharge(
                (int) round($authoritativeTotal * 100),
                $data['stripeToken'],
                sprintf('Rakcha Cinema Ticket Payment - Seance %d (%d seat(s))', $seance->getIdSeance(), count($seatsToReserve))
            );

            // Mark seats as reserved only after payment succeeds
            foreach ($seatsToReserve as $seat) {
                $seat->setStatut('reserve');
                $entityManager->persist($seat);
            }
            $entityManager->flush();
            $entityManager->commit();
        } catch (\Exception $e) {
            if ($entityManager->getConnection()->isTransactionActive()) {
                $entityManager->rollback();
            }

            return $this->json(['success' => false, 'message' => $e->getMessage()], Response::HTTP_INTERNAL_SERVER_ERROR);
        }

        return $this->json([
            'success' => true,
            'amount' => $authoritativeTotal,
            'seatsCount' => count($seatsToReserve),
        ]);
    }

    /**
     * Dispatch payment charge to Stripe API.
     */
    protected function executeStripeCharge(int $amountInCents, string $token, string $description): void
    {
        Stripe::setApiKey($_ENV['STRIPE_SECRET_KEY'] ?? '');
        Charge::create([
            'amount' => $amountInCents,
            'currency' => 'usd',
            'source' => $token,
            'description' => $description,
        ]);
    }
}
