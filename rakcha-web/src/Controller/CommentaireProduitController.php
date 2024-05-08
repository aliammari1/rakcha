<?php

namespace App\Controller;

use App\Entity\Produit;
use App\Entity\CommentaireProduit;
use App\Repository\UsersRepository;
use App\Form\CommentaireProduitType;
use App\Repository\ProduitRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use App\Repository\CommentaireProduitRepository;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;

#[Route('/commentaire')]
class CommentaireProduitController extends AbstractController
{
    #[Route('/', name: 'app_commentaire_produit_index', methods: ['GET'])]
    public function index(CommentaireProduitRepository $commentaireProduitRepository): Response
    {
        return $this->render('front/descriptionproduit.html.twig', [
            'commentaire_produits' => $commentaireProduitRepository->findAll(),
        ]);
    }

    #[Route('/produit/{id}/commentaire/new', name: 'app_commentaire_produit_new', methods: ['GET', 'POST'])]
    public function new(Request $request, UsersRepository $usersRepository, EntityManagerInterface $entityManager, ProduitRepository $produitRepository, $id): Response
    {
        $produit = $produitRepository->find($id);
        if (!$produit) {
            throw $this->createNotFoundException('Produit non trouvé');
        }

        $commentaireProduit = new CommentaireProduit();
        $commentaireProduit->setIdproduit($produit);

        $commentaireProduit->setIdClient($this->getUser());

        $form = $this->createForm(CommentaireProduitType::class, $commentaireProduit);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {

            $commentText = $commentaireProduit->getCommentaire();


            $correctedText = $this->autoCorrect($commentText);


            $commentaireProduit->setCommentaire($correctedText);

            $entityManager->persist($commentaireProduit);
            $entityManager->flush();




            return $this->redirectToRoute('app_commentaire_produit_show', ['id' => $id]);
        }


        return $this->render('front/descriptionproduit.html.twig', [
            'produit' => $produit,
            'form' => $form->createView(),
        ]);
    }

    public function autoCorrect($text): string
    {
        // Initialize cURL
        $curl = curl_init();

        // Set cURL options
        curl_setopt_array($curl, [
            CURLOPT_URL => "https://typewise-ai.p.rapidapi.com/correction/whole_sentence",
            CURLOPT_RETURNTRANSFER => true,
            CURLOPT_ENCODING => "",
            CURLOPT_MAXREDIRS => 10,
            CURLOPT_TIMEOUT => 30,
            CURLOPT_HTTP_VERSION => CURL_HTTP_VERSION_1_1,
            CURLOPT_CUSTOMREQUEST => "POST",
            CURLOPT_POSTFIELDS => json_encode([
                'text' => $text,
                'keyboard' => 'QWERTY',
                'languages' => [
                    'en'
                ]
            ]),
            CURLOPT_HTTPHEADER => [
                "X-RapidAPI-Host: typewise-ai.p.rapidapi.com",
                "X-RapidAPI-Key: aef1032e49msh9d46f007189dde9p15f3f9jsn879fab779700",
                "content-type: application/json"
            ],
        ]);


        $response = curl_exec($curl);
        $err = curl_error($curl);


        curl_close($curl);


        if ($err) {

            return "cURL Error #:" . $err;
        } else {

            $responseData = json_decode($response, true);


            if ($responseData === null) {

                return "Failed to decode JSON response";
            } else {

                if (isset($responseData['corrected_text'])) {

                    return $responseData['corrected_text'];
                } else {

                    return "No corrected text found in the response";
                }
            }
        }
    }



    #[Route('/produit/{id}/commentaire', name: 'app_commentaire_produit_show', methods: ['GET'])]
    public function show(EntityManagerInterface $entityManager, Produit $produit, CommentaireProduitRepository $commentaireRepository, Request $request): Response
    {


        $commentaires = $commentaireRepository->findBy(['idproduit' => $produit]);


        $commentaireProduit = new CommentaireProduit();
        $form = $this->createForm(CommentaireProduitType::class, $commentaireProduit);
        $form->handleRequest($request);


        if ($form->isSubmitted() && $form->isValid()) {

            $entityManager->persist($commentaireProduit);
            $entityManager->flush();


            return $this->redirectToRoute('app_commentaire_produit_show', ['id' => $produit->getIdproduit()]);
        }


        return $this->render('front/descriptionproduit.html.twig', [
            'produit' => $produit,
            'commentaires' => $commentaires,
            'form' => $form->createView(),
        ]);
    }



    #[Route('/commentaireproduit/{id}/edit', name: 'app_edit_commentaire', methods: ['POST'])]
    public function edit(Request $request, $id, EntityManagerInterface $entityManager, CommentaireProduitRepository $commentaireRepository): Response
    {

        $commentaireProduit = $commentaireRepository->find($id);


        if (!$commentaireProduit) {
            throw $this->createNotFoundException('Commentaire non trouvé');
        }


        $data = json_decode($request->getContent(), true);

        if (!isset($data['contenu'])) {
            return $this->json(['error' => 'Le champ "contenu" est manquant dans les données JSON.'], 200);
        }
        else{

        $correctedText = $this->autoCorrect($data['contenu']);
        $commentaireProduit->setCommentaire($correctedText);


        $entityManager->flush();}


        return $this->json(['message' => 'Commentaire mis à jour avec succès', 'commentaire' => $commentaireProduit]);
    }

    #[Route('/commentaireproduit/{id}', name: 'app_delete_commentaire', methods: ['POST'])]
    public function deleteCommentaire(Request $request, CommentaireProduit $commentaire, EntityManagerInterface $entityManager): Response
    {
        // Vérifier si la requête est une requête AJAX
        if ($request->isXmlHttpRequest()) {
            // Récupérer l'entité CommentaireProduit à partir de l'ID et le supprimer

            $entityManager->remove($commentaire);
            $entityManager->flush();

            // Retourner une réponse JSON pour indiquer que la suppression a réussi
            return $this->json(['success' => true]);
        }

        // Si la requête n'est pas AJAX, retourner simplement une réponse vide
        return new Response();
    }
}
