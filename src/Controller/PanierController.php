<?php

namespace App\Controller;

use App\Entity\Panier;
use App\Entity\Produit;
use App\Entity\Users;

use App\Repository\PanierRepository;
use App\Repository\ProduitRepository;
use App\Repository\UsersRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\HttpFoundation\JsonResponse;


use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Session\SessionInterface;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;



class PanierController extends AbstractController
{
    #[Route('/panier', name: 'app_panier')]
    public function index(): Response
    {
        return $this->render('panier/index.html.twig', [
            'controller_name' => 'PanierController',
        ]);
    }

    #[Route('/panier/add/{idProduit}', name: 'panier_add')]
public function addToPanier(Request $request, Produit $produit, PanierRepository $panierRepository, EntityManagerInterface $entityManager, UsersRepository $usersRepository): Response
{
    // Récupérer la quantité à partir des données du formulaire et la convertir en entier
    $quantite = (int) $request->request->get('quantite');

    // Récupérer l'utilisateur client à associer au panier (remplacez par la logique appropriée)
    $userId = 1; // ID de l'utilisateur que vous souhaitez attribuer
    $user = $usersRepository->findOneBy(['id' => $userId]);


    
  // Récupérer la quantité disponible en stock pour le produit
     $quantiteEnStock = $produit->getQuantitep();

    // Récupérer les quantités de produits dans le panier pour l'utilisateur actuel
      $quantitesDansPanier = $panierRepository->getQuantitesDansPanierParProduit($user);


    // Vérifier si la clé existe dans $quantitesDansPanier
if (!isset($quantitesDansPanier[$produit->getIdproduit()])) {
    // Si la clé n'existe pas, initialiser la quantité dans le panier à zéro
    $quantitesDansPanier[$produit->getIdproduit()] = 0;
}

// Comparer avec la quantité disponible en stock
if ($quantiteEnStock < $quantite + $quantitesDansPanier[$produit->getIdproduit()] || $quantite <= 0) {
    // Stock insuffisant, afficher une alerte et ne pas ajouter au panier
    $this->addFlash('error', 'Stock insuffisant.');
    return $this->redirectToRoute('app_panier_liste');
}




    // Récupérer le panier de l'utilisateur pour le produit donné
    $panier = $panierRepository->findOneBy([
        'idproduit' => $produit,
        'idclient' => $user,
    ]);

    if ($panier) {
        // Si le produit existe déjà dans le panier, mettre à jour la quantité
        $panier->setQuantite($panier->getQuantite() + $quantite);
    } else {
        // Si le produit n'existe pas dans le panier, créer une nouvelle entrée dans le panier
        $panier = new Panier();
        $panier->setIdproduit($produit);
        $panier->setClient($user);
        $panier->setQuantite($quantite);
    }

    // Enregistrer le panier en base de données
    $entityManager->persist($panier);
    $entityManager->flush();

    // Redirection vers la page du panier
    return $this->redirectToRoute('app_panier_liste');
}


public function storeSelectedProducts(Request $request, SessionInterface $session): Response
{
    $produitsSelectionnes = $request->request->get('produits_selectionnes', []);

    // Stockez les produits sélectionnés dans la session
    $session->set('produits_selectionnes', $produitsSelectionnes);

    // Redirigez vers l'action 'new' où vous créerez la commande
    return $this->redirectToRoute('app_commande_new');
}
    




    #[Route('/listepanier', name: 'app_panier_liste', methods: ['GET'])]
    public function afficherPanier(PanierRepository $panierRepository): Response
{
    // Récupérer les données du panier à partir du repository
    $panierItems = $panierRepository->findAll(); // Par exemple, à adapter selon vos besoins

    // Rendre la vue avec les données du panier
    return $this->render('front/listPanier.html.twig', [
        'panierItems' => $panierItems,
    ]);
}


#[Route('/panier/delete/{idPanier}', name: 'panier_delete')]
public function deletePanier(Request $request, $idPanier,EntityManagerInterface $entityManager): Response
{
   

    // Récupérer le panier à supprimer
    $panier = $entityManager->getRepository(Panier::class)->find($idPanier);

    // Vérifier si le panier existe
    if (!$panier) {
        throw $this->createNotFoundException('Panier non trouvé');
    }

    // Supprimer le panier
    $entityManager->remove($panier);
    $entityManager->flush();

    // Rediriger vers une autre page ou renvoyer une réponse JSON
 
    return $this->redirectToRoute('app_panier_liste');
}

    #[Route('/panier/update-quantity', name: 'panier_update_quantity', methods: ['POST'])]
    public function updateQuantity(Request $request, EntityManagerInterface $entityManager): Response
{
    // Récupérer les données JSON envoyées par la requête
    $content = json_decode($request->getContent(), true);
    $itemId = $content['itemId'];
    $newQuantity = $content['newQuantity'];

    // Récupérer l'entité correspondant à l'item à mettre à jour
    $item = $entityManager->getRepository(Panier::class)->find($itemId);

    // Vérifier si l'item existe
    if (!$item) {
        return new Response(json_encode(['success' => false, 'message' => 'Item not found']), Response::HTTP_NOT_FOUND);
    }

    // Récupérer le produit associé à l'item
    $produit = $item->getidproduit();

    // Vérifier si la nouvelle quantité est inférieure ou égale au stock disponible du produit
    if ($newQuantity <= $produit->getQuantiteP()) {
        // Mettre à jour la quantité de l'item dans le panier
        $item->setQuantite($newQuantity);
        $entityManager->flush();

        // Répondre avec une réponse JSON indiquant que la mise à jour a réussi
        return new Response(json_encode(['success' => true]));
    } else {
        // Retourner une réponse indiquant un stock insuffisant
        return new Response(json_encode(['success' => false, 'message' => 'Stock insuffisant']), Response::HTTP_BAD_REQUEST);
    }
}



    


    #[Route('/panier/calculate-total', name: 'panier_calculate_total', methods: ['POST'])]
    public function calculateTotal(Request $request,PanierRepository $panierRepository): Response
    {
        // Récupérer les IDs des produits depuis les données JSON envoyées par la requête
        $content = json_decode($request->getContent(), true);
        $productIds = $content['itemIds'];
        
        
        // Initialiser le total à 0
        $total = 0;

        // Récupérer les entités Panier correspondant aux IDs des produits
       
        $paniers = [];
        foreach ($productIds as $productId) {
            $panier =  $panierRepository->findOneBy(['idpanier' => $productId]);
            if ($panier) {
                $paniers[] = $panier;
            }
        }
        
        // Calculer le total pour tous les produits
        foreach ($paniers as $panier) {
            $total += $panier->getIdproduit()->getPrix() * $panier->getQuantite();
        }

        // Retourner le total au format JSON
        return new JsonResponse(['total' => $total]);
    }
    
    

 
     

 





}
