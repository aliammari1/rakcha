<?php

namespace App\Controller;

use App\Entity\Commande;
use App\Entity\Commandeitem;
use App\Entity\Panier;;

use App\Form\CommandeType;
use App\Repository\CommandeRepository;
use DateTime;
use Doctrine\ORM\EntityManagerInterface;

use Omnipay\Omnipay;
use Payum\Core\Request\GetHumanStatus;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Component\Routing\Annotation\Route;

#[Route('/commande')]
class CommandeController extends AbstractController
{


    #[Route('/', name: 'app_commande')]
    public function index(): Response
    {
        return $this->render('front/index.html.twig', [
            'controller_name' => 'CommandeController',
        ]);
    }
    #[Route('/success', name: 'app_success')]
    public function success(Request $request, CommandeRepository $commandeRepository, EntityManagerInterface $em): Response
    {
        $commandeId = $request->query->get('commandeId');
        if ($request->query->get('paymentId') && $request->query->get('PayerID')) {
            $operation = $this->passerelle->completePurchase([
                'payer_id' => $request->query->get('PayerID'),
                'transactionReference' => $request->query->get('paymentId'),
            ]);

            $response = $operation->send();

            if ($response->isSuccessful()) {
                $data = $response->getData();
                $commande = $commandeRepository->find($commandeId);
                $commande->setStatu('payé');
                $em->persist($commande);
                $em->flush();
                // Rediriger l'utilisateur vers la page de succès après un paiement réussi
                return $this->redirectToRoute('app_payment_success', ['transactionId' => $data['id']]);
            } else {
                // Gérer les erreurs de paiement ici
                return $this->render('front/error.html.twig', [
                    'message' => 'Une erreur est survenue lors du traitement du paiement.'
                ]);
            }
        } else {
            // Gérer les paramètres manquants ici
            return $this->render('front/error.html.twig', [
                'message' => 'Les paramètres de paiement sont manquants.'
            ]);
        }
    }

    //Page d'error de la transaction
    #[Route('/error', name: 'app_error')]
    public function error(): Response
    {
        return $this->render(
            'front/error.html.twig',
            [
                'message' => 'le paiement a échoué'
            ]
        );
    }

    #[Route('/payment/success', name: 'app_payment_success')]
    public function paymentSuccess(Request $request): Response
    {
        // Afficher la page de succès avec les détails de la transaction
        return $this->render('commande/payment_success.html.twig');
    }

    #[Route('/new', name: 'app_commande_new', methods: ['POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {

        $selectedItemIds = $request->query->get('selectedItemIds');
        $commande = new Commande();
        $form = $this->createForm(CommandeType::class, $commande);
        $form->handleRequest($request);

        $commande = $form->getData();
        $commande->setIdclient($this->getUser());
        $commande->setStatu('en cours');
        $commande->setDatecommande(new DateTime());


        $entityManager->persist($commande);
        $entityManager->flush();






        $selectedItemIdsArray = $selectedItemIds ? explode(',', $selectedItemIds) : [];



        foreach ($selectedItemIdsArray as $itemId) {
            $panierItem = $entityManager->getRepository(Panier::class)->find($itemId);

            if ($panierItem) {

                $commandeItem = new CommandeItem();
                $commandeItem->setIdCommande($commande);
                $commandeItem->setIdProduit($panierItem->getIdproduit());
                $commandeItem->setQuantity($panierItem->getQuantite());




                $entityManager->persist($commandeItem);
            }

            $quantite = $panierItem->getQuantite();
            $newquantite = $panierItem->getIdproduit()->getQuantiteP() - $quantite;
            $panierItem->getIdproduit()->setQuantiteP($newquantite);
        }
        $entityManager->flush();





        return $this->redirectToRoute('app_commande_form', ['idcommande' => $commande->getIdcommande()]);
    }


    #[Route('/order/form/', name: 'app_commande_form', methods: ['GET'])]
    public function form(Request $request): Response
    {
        $jsonData = $request->getContent();
        $data = json_decode($jsonData, true);
        $produitSelectionnes = $request->query->get('produits_selectionnes');
        $commandeId = $request->query->get('idcommande');


        //  $selectedItemIdsArray = $produitSelectionnes ? explode(',', $produitSelectionnes) : [];



        $commande = new Commande();
        $form = $this->createForm(CommandeType::class, $commande);

        return $this->render('front/addCommandeProduit.html.twig', [
            'form' => $form->createView(),
            'produitSelectionnes' => $produitSelectionnes,
            'commandeId' => $commandeId
        ]);
    }


    #[Route('/{idcommande}', name: 'app_commande_show', methods: ['GET'])]
    public function show(Commande $commande): Response
    {
        return $this->render('front/show.html.twig', [
            'commande' => $commande,
        ]);
    }

    #[Route('/{idcommande}/edit', name: 'app_commande_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Commande $commande, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(CommandeType::class, $commande);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_commande_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('front/edit.html.twig', [
            'commande' => $commande,
            'form' => $form,
        ]);
    }

    #[Route('/{idcommande}', name: 'app_commande_delete', methods: ['POST'])]
    public function delete(Request $request, Commande $commande, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete' . $commande->getIdcommande(), $request->request->get('_token'))) {
            $entityManager->remove($commande);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_commande_index', [], Response::HTTP_SEE_OTHER);
    }

    private $passerelle;
    private $manager;

    public function __construct(EntityManagerInterface $manager)
    {
        $this->passerelle = Omnipay::create('PayPal_Rest');
        $this->passerelle->initialize([
            'clientId' => $_ENV['PAYPAL_CLIENT_ID'],
            'secret' => $_ENV['PAYPAL_SECRET_KEY'],
            'testMode' => true,
        ]);
        $this->manager = $manager;
    }

    //Page d'accueil

    #[Route('/payment', name: 'app_payment', methods: ['POST'])]
    public function payment(Request $request): Response
    {
        $token = $request->request->get('token');
        $commandeId = $request->query->get('commandeId');
        if (!$this->isCsrfTokenValid('form', $token)) {
            return new Response(
                'Operation non autorisée',
                Response::HTTP_BAD_REQUEST,
                ['content-type' => 'text/plain']
            );
        }

        $response = $this->passerelle->purchase([
            'amount' => $request->request->get('amount'),
            'currency' => $_ENV['PAYPAL_CURRENCY'],
            'returnUrl' => 'http://127.0.0.1:8001/commande/success?commandeId=' . $commandeId,
            'cancelUrl' => 'http://127.0.0.1:8001/commande/error'
        ])->send();

        var_dump($response->getData());

        if ($response->isRedirect()) {

            $responseData = $response->getData();


            if (isset($responseData['links'])) {
                foreach ($responseData['links'] as $link) {

                    if ($link['rel'] === 'approval_url') {

                        $redirectUrl = $link['href'];


                        return $this->redirect($redirectUrl);
                    }
                }
            }

            return new Response('L\'URL de redirection est introuvable dans les données de réponse.', Response::HTTP_INTERNAL_SERVER_ERROR);
        } else {

            return $this->render('front/error.html.twig', [
                'message' => 'Une erreur est survenue lors du traitement du paiement.'
            ]);
        }
    }
}
