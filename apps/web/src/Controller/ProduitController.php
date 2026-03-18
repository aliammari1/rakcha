<?php

namespace App\Controller;

use App\Entity\CommentaireProduit;
use App\Entity\Produit;
use App\Form\CommentaireProduitType;
use App\Form\ProduitType;
use App\Repository\CommentaireProduitRepository;
use App\Repository\ProduitRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;


#[Route('/produit')]
class ProduitController extends AbstractController
{


    #[Route('/', name: 'app_produit_index', methods: ['GET'])]
    public function index(ProduitRepository $produitRepository, Request $request, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(ProduitType::class, new Produit());
        $updateForms = array();
        for ($i = 0; $i < count($produitRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(ProduitType::class, $produitRepository->findAll()[$i])->createView();
        }
        return $this->render('back/produittable.html.twig', [
            'produit' => $produitRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
        ]);
    }


    #[Route('/listeproduit', name: 'app_produit_liste', methods: ['GET', 'POST'])]
    public function liste(ProduitRepository $produitRepository, Request $request): Response
    {
        // Récupérer tous les produits
        $produit = $produitRepository->findAll();

        // Initialiser une variable pour stocker le prix maximal
        $maxPrice = 0;

        // Parcourir tous les produits et trouver le prix maximal
        foreach ($produit as $pro) {
            $prix = $pro->getPrix();
            if ($prix > $maxPrice) {
                $maxPrice = $prix;
            }
        }


        // Initialiser un tableau pour stocker le nombre de produits par catégorie
        $nombreProduitsParCategorie = [];

        // Parcourir tous les produits et compter les produits par catégorie
        foreach ($produit as $pro) {
            $categorie = $pro->getIdCategorieproduit()->getNomCategorie();
            if (!isset($nombreProduitsParCategorie[$categorie])) {
                $nombreProduitsParCategorie[$categorie] = 1;
            } else {
                $nombreProduitsParCategorie[$categorie]++;
            }
        }


        return $this->render('front/listproduct.html.twig', [
            'produit' => $produit,
            'nombreProduitsParCategorie' => $nombreProduitsParCategorie,


            'maxPrice' => $maxPrice,
        ]);
    }


    #[Route('/new', name: 'app_produit_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager, ProduitRepository $produitRepository): Response
    {
        $updateForms = array();
        for ($i = 0; $i < count($produitRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(ProduitType::class, $produitRepository->findAll()[$i])->createView();
        }
        $produit = new Produit();
        $form = $this->createForm(ProduitType::class, $produit);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $file = $form['image']->getData();
            if ($file) {
                $extension = $file->guessExtension();
                if (!$extension) {
                    // extension cannot be guessed
                    $extension = 'bin';
                }
                $filename = rand(1, 99999) . '.' . $extension;
                $destination = $this->getParameter('kernel.project_dir') . "/public/img/produit";
                $file->move($destination, $filename);
                $produit->setImage("/img/produit/" . $filename);

                // Copy the file to another location
                $anotherDestination = "C:\\xampp\\htdocs\\Rakcha\\rakcha-desktop\\src\\main\\resources\\img\\produit";
                copy($destination . "/" . $filename, $anotherDestination . "/" . $filename);
            }


            $entityManager->persist($produit);
            $entityManager->flush();


            return $this->redirectToRoute('app_produit_index', [], Response::HTTP_SEE_OTHER);
        }

        $hasErrorsCreate = true;
        return $this->render('back/produittable.html.twig', [
            'produit' => $produitRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
            'hasErrorsCreate' => $hasErrorsCreate,
        ]);
    }


    #[Route('/show/{idProduit}', name: 'app_produit_show', methods: ['GET'])]
    public function show(Produit $produit, CommentaireProduitRepository $commentaireRepository, Request $request, EntityManagerInterface $entityManager): Response
    {
        // Récupérer les commentaires associés au produit
        $commentaires = $commentaireRepository->findBy(['idproduit' => $produit]);

        // Créer un formulaire pour ajouter un nouveau commentaire
        $commentaireProduit = new CommentaireProduit();
        $form = $this->createForm(CommentaireProduitType::class, $commentaireProduit);
        $form->handleRequest($request);

        // Vérifier si le formulaire a été soumis et est valide
        if ($form->isSubmitted() && $form->isValid()) {

            $entityManager->persist($commentaireProduit);
            $entityManager->flush();

            // Rafraîchir la page pour afficher le nouveau commentaire
            return $this->redirectToRoute('app_commentaire_produit_show', ['id' => $produit->getIdproduit()]);
        }

        // Rendez la vue avec les détails du produit et les commentaires
        return $this->render('front/descriptionproduit.html.twig', [
            'produit' => $produit,
            'commentaires' => $commentaires,
            'form' => $form->createView(),
        ]);
    }


    #[Route('/{idProduit}/edit/{formUpdateNumber}/', name: 'app_produit_edit', methods: ['GET', 'POST'])]
    public function edit($formUpdateNumber, Request $request, Produit $produit, EntityManagerInterface $entityManager, ProduitRepository $produitRepository): Response
    {

        $updateForms = array();
        for ($i = 0; $i < count($produitRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(ProduitType::class, $produitRepository->findAll()[$i])->createView();
        }

        $form = $this->createForm(ProduitType::class, $produit);
        $updateform = $this->createForm(ProduitType::class, $produit);
        $updateform->handleRequest($request);

        if ($updateform->isSubmitted() && $updateform->isValid()) {
            $file = $updateform['image']->getData();
            if ($file) {
                $extension = $file->guessExtension();
                if (!$extension) {
                    // extension cannot be guessed
                    $extension = 'bin';
                }
                $filename = rand(1, 99999) . '.' . $extension;
                $destination = $this->getParameter('kernel.project_dir') . "/public/img/produit";
                $file->move($destination, $filename);
                $produit->setImage("/img/produit/" . $filename);

                // Copy the file to another location
                $anotherDestination = "C:\\xampp\\htdocs\\Rakcha\\rakcha-desktop\\src\\main\\resources\\img\\produit";
                copy($destination . "/" . $filename, $anotherDestination . "/" . $filename);
            }

            $entityManager->flush();


            return $this->redirectToRoute('app_produit_index', [], Response::HTTP_SEE_OTHER);
        }
        $entityManager->refresh($produit);

        return $this->render('back/produittable.html.twig', [
            'produit' => $produitRepository->findAll(),
            'form' => $form->createView(),
            'updateform' => $updateform->createView(),
            'updateForms' => $updateForms,
            "formUpdateNumber" => $formUpdateNumber,
        ]);
    }

    #[Route('/{idProduit}', name: 'app_produit_delete', methods: ['POST'])]
    public function delete(Request $request, Produit $produit, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete' . $produit->getIdProduit(), $request->request->get('_token'))) {
            $entityManager->remove($produit);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_produit_index', [], Response::HTTP_SEE_OTHER);
    }

    #[Route('/product/filter', name: 'product_filter', methods: ['GET'])]
    public function filterProducts(Request $request, ProduitRepository $produitRepository): JsonResponse
    {

        $minPrice = $request->query->get('minPrice');
        $maxPrice = $request->query->get('maxPrice');


        $filteredProducts = $produitRepository->findByPriceRange($minPrice, $maxPrice);


        $formattedProducts = [];


        foreach ($filteredProducts as $produit) {

            $imagePath = $this->getParameter('kernel.project_dir') . '/public/img/produit/' . $produit->getImage();


            $formattedProducts[] = [
                'nom' => $produit->getNom(),
                'prix' => $produit->getPrix(),
                'description' => $produit->getDescription(),
                'image' => $imagePath,
            ];
        }


        return $this->json(['produits' => $formattedProducts]);
    }
}
