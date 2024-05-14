<?php

namespace App\Controller;

use App\Entity\Series;
use App\Form\SeriesType;
use App\Entity\Categories;
use App\Entity\Favoris;
use App\Repository\SeriesRepository;
use App\Repository\FavorisRepository;
use App\Entity\Users;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Symfony\Component\HttpFoundation\RedirectResponse;
use Twilio\Rest\Client;
use Sensio\Bundle\FrameworkExtraBundle\Configuration\ParamConverter;


#[Route('/series')]
class SeriesController extends AbstractController
{
    #[Route('/', name: 'app_series_index', methods: ['GET'])]
    public function index(SeriesRepository $seriesRepository, EntityManagerInterface $entityManager): Response
    {
        $statisticsData = $seriesRepository->getStatisticsByCategory();
        $form = $this->createForm(SeriesType::class, new Series());
        $updateForms = array();
        for ($i = 0; $i < count($seriesRepository->findAll()); $i++) {
            $updateForms[$i] = $this->createForm(SeriesType::class, $seriesRepository->findAll()[$i])->createView();
        }
        return $this->render('back/seriesTables.html.twig', [
            'statisticsData' => $statisticsData,
            'series' => $seriesRepository->findAll(),
            'form' => $form->createView(),
            'updateForms' => $updateForms,
        ]);

    }
    #[Route('/listeSeries', name: 'app_series_liste', methods: ['GET'])]
    public function listeSeries(SeriesRepository $seriesRepository, Request $request, EntityManagerInterface $entityManager): Response
    {
        /*$searchTerm = $request->query->get('nom');
        $sortBy = $request->query->get('sort_by', 'nom'); // Tri par défaut par 'nom'
        $sortOrder = $request->query->get('sort_order', 'asc'); // Ordre de tri par défaut 'asc'
        
        $queryBuilder = $entityManager->getRepository(Series::class)->createQueryBuilder('s');
        
        // Requête de recherche
        if ($searchTerm) {
            $queryBuilder->where('s.nom LIKE :searchTerm')
                         ->orWhere('s.directeur LIKE :searchTerm')
                         ->orWhere('s.pays LIKE :searchTerm')
                         ->setParameter('searchTerm', '%' . $searchTerm . '%');
        }
        
        // Requête de tri
        $queryBuilder->orderBy('s.' . $sortBy, $sortOrder);
        
        $series = $queryBuilder->getQuery()->getResult();
    */

        // Récupérer toutes les séries
        $allSeries = $seriesRepository->findAll();
        $categories = $this->getDoctrine()->getRepository(Categories::class)->findAll();
        // Récupérer les recommandations de séries les plus likées
        $recommendations = $seriesRepository->createQueryBuilder('s')
            ->orderBy('s.nblikes', 'DESC')
            ->setMaxResults(3) // Limiter le nombre de recommandations affichées
            ->getQuery()
            ->getResult();
        /*
            // Récupérer l'identifiant de la catégorie de comédie
            $comedyCategoryId = 4; 
            // Récupérer les recommandations de séries
            $recommendations = $seriesRepository->findRelaxingSeries($comedyCategoryId);
        */
        // Créer un formulaire pour chaque série
        $updateForms = array();
        foreach ($allSeries as $serie) {
            $updateForms[] = $this->createForm(SeriesType::class, $serie)->createView();
        }

        // Renvoyer les données à la vue
        return $this->render('front/listSeries.html.twig', [
            'series' => $allSeries,
            'form' => $this->createForm(SeriesType::class, new Series())->createView(),
            'updateForms' => $updateForms,
            'recommendations' => $recommendations, // Passer les recommandations à la vue
            'categories' => $categories,
        ]);
    }

    #[Route('/new', name: 'app_series_new', methods: ['GET', 'POST'])]
    public function new(Request $request, EntityManagerInterface $entityManager): Response
    {
        $series = new Series();
        $form = $this->createForm(SeriesType::class, $series);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Traitement de l'image
            $file = $form['image']->getData();
            if ($file) {
                $extension = $file->guessExtension();
                if (!$extension) {
                    // extension cannot be guessed
                    $extension = 'bin';
                }
                $filename = rand(1, 99999) . '.' . $extension;
                $destination = $this->getParameter('kernel.project_dir') . "/public/img/series";
                $file->move($destination, $filename);
                $series->setImage("/img/series/" . $filename);

                // Copy the file to another location
                $anotherDestination = "C:\\xampp\\htdocs\\Rakcha\\rakcha-desktop\\src\\main\\resources\\img\\series";
                copy($destination . "/" . $filename, $anotherDestination . "/" . $filename);
            }
            $entityManager->persist($series);
            $entityManager->flush();
            /*
           // Envoi du SMS après l'ajout de la série
        $twilioSid = "ACd3d2094ef7f546619e892605940f1631";
        $twilioToken = "8d56f8a04d84ff2393de4ea888f677a1";
        $twilioPhoneNumber = "+17573640849";
        $phoneNumber = '+21653775010'; // Remplacez par le numéro de téléphone réel de votre base de données

      try {
          $client = new Client($twilioSid, $twilioToken);
          $client->messages->create(
              $phoneNumber,
              [
                  'from' => $twilioPhoneNumber,
                  'body' => 'New Serie is Out There'
              ]
          );
      } catch (\Exception $e) {
          // Gérer l'exception ici
          $errorMessage = $e->getMessage();
          $this->addFlash('error', 'Erreur lors de l\'envoi du SMS : ' . $errorMessage);
      }
        */

            return $this->redirectToRoute('app_series_index', [], Response::HTTP_SEE_OTHER);
        }
        dump($form->getErrors(true));
        return $this->renderForm('series/new.html.twig', [
            'series' => $series,
            'form' => $form,
        ]);
    }

    #[Route('/{idserie}', name: 'app_series_show', methods: ['GET'])]
    public function show(Series $series): Response
    {
        return $this->render('series/show.html.twig', [
            'series' => $series,
        ]);
    }

    #[Route('/{idserie}/edit', name: 'app_series_edit', methods: ['GET', 'POST'])]
    public function edit(Request $request, Series $series, EntityManagerInterface $entityManager): Response
    {
        $form = $this->createForm(SeriesType::class, $series);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Traitement de l'image
            $file = $form['image']->getData();
            if ($file) {
                $extension = $file->guessExtension();
                if (!$extension) {
                    // extension cannot be guessed
                    $extension = 'bin';
                }
                $filename = rand(1, 99999) . '.' . $extension;
                $destination = $this->getParameter('kernel.project_dir') . "/public/img/series";
                $file->move($destination, $filename);
                $series->setImage("/img/series/" . $filename);

                // Copy the file to another location
                $anotherDestination = "C:\\xampp\\htdocs\\Rakcha\\rakcha-desktop\\src\\main\\resources\\img\\series";
                copy($destination . "/" . $filename, $anotherDestination . "/" . $filename);
            }
            $entityManager->flush();

            return $this->redirectToRoute('app_series_index', [], Response::HTTP_SEE_OTHER);
        }

        return $this->renderForm('series/edit.html.twig', [
            'series' => $series,
            'form' => $form,
        ]);
    }

    #[Route('/{idserie}', name: 'app_series_delete', methods: ['POST'])]
    public function delete(Request $request, Series $series, EntityManagerInterface $entityManager): Response
    {
        if ($this->isCsrfTokenValid('delete' . $series->getIdserie(), $request->request->get('_token'))) {
            $entityManager->remove($series);
            $entityManager->flush();
        }

        return $this->redirectToRoute('app_series_index', [], Response::HTTP_SEE_OTHER);
    }

    #[Route('/series/{idserie}/like', name: 'app_like_series', methods: ['GET'])]
    public function likeSeries(int $idserie): RedirectResponse
    {
        // Récupérer la série depuis la base de données en fonction de l'ID
        $series = $this->getDoctrine()->getRepository(Series::class)->find($idserie);

        // Incrémenter le nombre de likes
        $series->setNblikes($series->getNblikes() + 1);

        // Enregistrer les modifications
        $entityManager = $this->getDoctrine()->getManager();
        $entityManager->persist($series);
        $entityManager->flush();

        // Rediriger l'utilisateur
        return new RedirectResponse($this->generateUrl('app_series_liste'));
    }


    #[Route('/series/{idserie}/dislike', name: 'app_dislike_series', methods: ['GET'])]
    public function dislikeSeries(int $idserie): RedirectResponse
    {
        // Récupérer la série depuis la base de données en fonction de l'ID
        $series = $this->getDoctrine()->getRepository(Series::class)->find($idserie);

        // Incrémenter le nombre de dislikes
        $series->setNbDislikes($series->getNbDislikes() + 1);

        // Enregistrer les modifications
        $entityManager = $this->getDoctrine()->getManager();
        $entityManager->persist($series);
        $entityManager->flush();

        // Rediriger l'utilisateur
        return $this->redirectToRoute('app_series_liste');
    }








    /*
    #[Route('/{idSerie}/toggle-favorite', name: 'app_toggle_favorite', methods: ['POST'])]
    public function toggleFavorite(int $idSerie, FavorisRepository $favorisRepository): Response
    {
        $idUser = 1; // Remplacez 1 par l'ID de l'utilisateur connecté
        $user = $this->getDoctrine()->getRepository(Users::class)->find($idUser);
        
        // Récupérer le favori correspondant à l'utilisateur et à la série
        $favoris = $favorisRepository->findOneBy([
            'idUser' => $user->getId(),
            'idSerie' => $idSerie,
        ]);

        // Si le favori existe, le supprimer. Sinon, l'ajouter.
        $entityManager = $this->getDoctrine()->getManager();
        if ($favoris) {
            $entityManager->remove($favoris);
        } else {
            $favoris = new Favoris();
            $favoris->setIdUser($user->getId());
            $favoris->setIdSerie($idSerie);
            $entityManager->persist($favoris);
        }
        
        $entityManager->flush();
        
        // Rediriger vers la liste des favoris
        return $this->redirectToRoute('app_favorites_list');
    }
    */

    /*
    public function generatePDFContent(): string
    {
        // Générer le contenu du PDF (par exemple, à partir de la base de données)
        $pdfContent = "<html><body><h1>Contenu du PDF</h1><p>Ceci est un exemple de contenu pour le PDF.</p></body></html>";

        return $pdfContent;
    }


    #[Route('/update-pdf', name: 'update_pdf')]
    public function updatePDFAction(): Response
    {
        // Générer le contenu du PDF
        $pdfContent = $this->generatePDFContent();

        // Chemin vers le fichier PDF
        $pdfFile = $this->getParameter('kernel.project_dir') . '/public/pdf/feedback.pdf';

        // Écrire le contenu dans le fichier PDF
        file_put_contents($pdfFile, $pdfContent);

        // Retourner une réponse réussie ou rediriger l'utilisateur
        // return $this->redirectToRoute('route_to_success_page');
    }

    #[Route('/download-pdf', name: 'download_pdf')]
    public function downloadPDFAction(): Response
        {
            // Chemin vers le fichier PDF
            $pdfFile = $this->getParameter('kernel.project_dir') . '/public/pdf/feedback.pdf';

            // Créer une réponse avec le fichier PDF en tant que contenu
            $response = new Response(file_get_contents($pdfFile));

            // Définir les en-têtes pour le téléchargement du fichier
            $response->headers->set('Content-Type', 'application/pdf');
            $response->headers->set('Content-Disposition', 'attachment; filename="feedback.pdf"');

            return $response;
        }
       
    */
}
