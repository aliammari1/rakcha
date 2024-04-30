<?php

namespace App\Controller;

use App\Entity\Users;
use App\Repository\SeatRepository;
use Doctrine\ORM\EntityManagerInterface;
use Stripe\Charge;
use Stripe\Stripe;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\Response;
use Doctrine\Persistence\ManagerRegistry;
use Exception;

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
    public function createCharge(Request $request,SeatRepository $seatRepository, EntityManagerInterface $entityManager): Response
    {
        $data = json_decode($request->getContent(), true);
        try {
            Stripe::setApiKey($_ENV["STRIPE_SECRET_KEY"]);
            Charge::create([
                "amount" => $data["prix"] * 10,
                "currency" => "usd",
                "source" => $data['stripeToken'],
                "description" => "Binaryboxtuts Payment Test"
            ]);
            for($i=0;$i<count($data["seatIds"]);$i++) {
                $seat = $seatRepository->findOneBy(['id'=> $data["seatIds"][$i]]);
                $seat->setStatut("reserve");
                $entityManager->persist($seat);
            }
            $entityManager->flush();

        } catch (Exception $e) {
            return $this->json(['success' => false, 'message' => $e->getMessage(),'data' => $data]);
        }
        // $user = $doct->getRepository(Users::class)->findOneBy(['email' => $this->getUser()->getUserIdentifier()]);

        // $commande = $commandsRepository->findOneBy(["user" => $user->getId(), "status" => 0]);
        // $commandeAchats = $achatsRepository->findOneBy(["commande" => $commande]);

        // $commandeAchats->setValidate(1);
        // $commande->setStatus(1);
        // $em = $doct->getManager();
        // $em->flush();

        return $this->json(['success' => true]);
    }


}
