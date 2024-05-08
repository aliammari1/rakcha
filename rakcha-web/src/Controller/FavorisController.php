<?php

namespace App\Controller;

use App\Entity\Favoris;
use App\Form\FavorisType;
use App\Entity\Series;
use App\Entity\Users;
use App\Repository\FavorisRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\ParamConverter;


#[Route('/favoris')]
class FavorisController extends AbstractController
{
    #[Route('/', name: 'app_favoris_index', methods: ['GET'])]
    public function index(FavorisRepository $favorisRepository): Response
    {
        // Récupérer les favoris de l'utilisateur actuel
       $favoris = $favorisRepository->findBy(['idUser' => $this->getUser()->getId()]);

    // Créer un tableau pour stocker les objets Serie associés à chaque favori
    $series = [];
    foreach ($favoris as $favori) {
        $idSerie = $favori->getIdSerie();
        $serie = $this->getDoctrine()->getRepository(Series::class)->find($idSerie);
        // Ajouter l'objet Serie au tableau
        $series[] = $serie;
    }

    return $this->render('front/listeFavoris.twig', [
        'favoris' => $favoris,
        'series' => $series, // Passer les objets Serie au template Twig
    ]);
    }

    #[Route('/new', name: 'app_favoris_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $favori = new Favoris();
        $form = $this->createForm(FavorisType::class, $favori);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->persist($favori);
            $entityManager->flush();

            return $this->redirectToRoute('app_favoris_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('favoris/new.html.twig', [
            'favori' => $favori,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_favoris_show', methods: ['GET'])]
    public function show(Favoris $favori): Response
    {
        return $this->render('favoris/show.html.twig', [
            'favori' => $favori,
        ]);
    }

    #[Route('/{id}/edit', name: 'app_favoris_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Favoris $favori, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(FavorisType::class, $favori);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $entityManager->flush();

            return $this->redirectToRoute('app_favoris_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('favoris/edit.html.twig', [
            'favori' => $favori,
            'form' => $form,
        ]);
    }

    #[Route('/{id}', name: 'app_favoris_delete', methods: ['POST'])]
    public function delete(Request $request, Favoris $favori, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete'.$favori->getId(), $request->request->get('_token'))) {
            $entityManager->remove($favori);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_favoris_index', [], Response::HTTP_SEE_OTHER);
    }

    /*
    #[Route('/listeSeriesParFavoris', name: 'app_series_liste_par_favoris', methods: ['GET'])]
    public function listeSerieParFavoris(FavorisRepository $favorisRepository, SeriesRepository $seriesRepository): Response
    {
        // Récupérer tous les favoris
        $favoris = $favorisRepository->findAll();
        
        // Créer un tableau associatif pour stocker les séries par favoris
        $seriesByFavoris = [];
        
        // Remplir le tableau associatif
        foreach ($favoris as $favori) {
            $serieId = $favori->getIdSerie();
            $serie = $seriesRepository->find($serieId); // Récupérer la série associée au favori
            
            // Vérifier si la série existe et si elle n'a pas déjà été ajoutée
            if ($serie && !isset($seriesByFavoris[$favori->getId()])) {
                $seriesByFavoris[$favori->getId()] = $serie; // Ajouter la série au tableau associatif sous la clé du favori
            }
        }
        
        // Passez le tableau associatif à la vue pour affichage
        return $this->render('front/listSeriesParFavoris.html.twig', [
            'seriesByFavoris' => $seriesByFavoris,
        ]);
    }
    */
    #[Route('/{idSerie}/add-to-favorites', name: 'app_add_to_favorites', methods: ['POST'])]
     public function addToFavorites(int $idSerie, FavorisRepository $favorisRepository, Request $request): Response
{

   // Vérifier si la série est déjà dans les favoris de l'utilisateur
  $favoris = $favorisRepository->findOneBy([
    'idUser' => $this->getUser()->getId(),
    'idSerie' => $idSerie,
   ]);

   // Si la série n'est pas déjà dans les favoris, l'ajouter
   if (!$favoris) {
    $favoris = new Favoris();
    $favoris->setIdUser($this->getUser()->getId());
    $favoris->setIdSerie($idSerie);

    // Enregistrer le favori
    $entityManager = $this->getDoctrine()->getManager();
    $entityManager->persist($favoris);
    $entityManager->flush();
   }
   return $this->redirectToRoute('app_favoris_index');

   }

   #[Route('/favoris/remove/{idSerie}', name: 'app_remove_from_favorites', methods: ['POST'])]
   public function removeFromFavorites(int $idSerie, FavorisRepository $favorisRepository, Request $request): Response
   {
   
       // Récupérer l'EntityManager
       $entityManager = $this->getDoctrine()->getManager();
   
       // Récupérer le favori correspondant à la série et à l'utilisateur
       $favori = $favorisRepository->findOneBy(['idUser' => $this->getUser()->getId(), 'idSerie' => $idSerie]);
   
       // Vérifier si le favori existe
       if (!$favori) {
           throw $this->createNotFoundException('Le favori que vous essayez de retirer n\'existe pas.');
       }
   
       // Supprimer le favori de la base de données
       $entityManager->remove($favori);
       $entityManager->flush();
   
       // Rediriger l'utilisateur vers la page des favoris
       return $this->redirectToRoute('app_favoris_index');
   }

}
