<?php

namespace App\Controller;

use App\Entity\Commentairecinema;
use App\Form\CommentairecinemaType;
use App\Repository\CommentairecinemaRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use App\Entity\Cinema;
use Sentiment\Analyzer;
use Symfony\UX\Chartjs\Builder\ChartBuilderInterface;
use Symfony\UX\Chartjs\Model\Chart;



#[Route('/commentairecinema')]
class CommentairecinemaController extends AbstractController
{
    #[Route('/{idCinema}', name: 'app_commentairecinema_index', methods: ['GET', 'POST'])]
    public function index(int $idCinema, CommentairecinemaRepository $commentairecinemaRepository, EntityManagerInterface $entityManager, ChartBuilderInterface $chartBuilder): Response
    {
        // Récupérer le cinema
        $cinema = $entityManager->getRepository(Cinema::class)->findOneBy(['idCinema' => $idCinema]);
        $commentairecinema = new Commentairecinema();
        $form = $this->createForm(CommentairecinemaType::class, $commentairecinema);

        // Récupérer les commentaires pour ce cinéma spécifique
        $commentairecinemas = $commentairecinemaRepository->findBy(['idcinema' => $idCinema]);

        // Compter le nombre de commentaires pour chaque type de sentiment
        $positiveCount = 0;
        $negativeCount = 0;
        $neutralCount = 0;
        foreach ($commentairecinemas as $commentairecinema) {
            $sentiment = $commentairecinema->getSentiment();
            if ($sentiment === 'pos') {
                $positiveCount++;
            } elseif ($sentiment === 'neg') {
                $negativeCount++;
            } else {
                $neutralCount++;
            }
        }

        // Calculer les pourcentages
        $total = count($commentairecinemas);
        $positivePercentage = ($total > 0) ? ($positiveCount / $total) * 100 : 0;
        $negativePercentage = ($total > 0) ? ($negativeCount / $total) * 100 : 0;
        $neutralPercentage = ($total > 0) ? ($neutralCount / $total) * 100 : 0;

        // Créer le graphique
        $chart = $chartBuilder->createChart(Chart::TYPE_DOUGHNUT);
        $chart->setData([
            'labels' => ['Positive', 'Negative', 'Neutral'],
            'datasets' => [
                [
                    'label' => 'Sentiments',
                    'backgroundColor' => ['#36A2EB', '#FF6384', '#FFCE56'],
                    'data' => [$positivePercentage, $negativePercentage, $neutralPercentage],
                ],
            ],
        ]);



        return $this->render('front/commentCinema.html.twig', [
            'commentairecinemas' => $commentairecinemas,
            'chart' => $chart,
            'cinema' => $cinema,
            'form' => $form->createView(),
        ]);
    }


    #[Route('/new/{idCinema}', name: 'app_commentairecinema_new', methods: ['GET', 'POST'])]
    public function new(int $idCinema, Request $request, EntityManagerInterface $entityManager, CommentairecinemaRepository $commentairecinemaRepository): Response
    {
        $cinema = $entityManager->find(Cinema::class, $idCinema);
        if (!$cinema) {
            throw $this->createNotFoundException('No cinema found for id ' . $idCinema);
        }

        $commentairecinema = new Commentairecinema();
        $form = $this->createForm(CommentairecinemaType::class, $commentairecinema);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            // Traiter le formulaire soumis
            $commentairecinema->setIdCinema($cinema->getIdCinema()); // Ajouter l'objet Cinema au commentaire

            // Analyse de sentiment avec php-sentiment-analyzer
            $commentText = $commentairecinema->getCommentaire(); // Supposons que vous avez une méthode getComment() dans votre entité Commentairecinema
            $sentiment = $this->analyseSentiment($commentText);

            // Le sentiment est retourné sous forme de chaîne ('positive', 'negative', 'neutral')
            // Vous pouvez utiliser directement cette valeur comme sentiment du commentaire
            $commentairecinema->setSentiment($sentiment);

            $commentairecinema->setIdclient($this->getUser()->getId());

            $entityManager->persist($commentairecinema);
            $entityManager->flush();

            return $this->redirectToRoute('app_commentairecinema_index', ['idCinema' => $idCinema], Response::HTTP_SEE_OTHER);
        }

        return $this->render('front/commentCinema.html.twig', [
            'cinema' => $cinema,
            'form' => $form->createView(),
        ]);
    }


    private function analyseSentiment(string $comment): string
    {
        $analyzer = new Analyzer();
        $sentiment = $analyzer->getSentiment($comment);

        // Récupérer la catégorie de sentiment avec le score le plus élevé
        $maxSentiment = array_keys($sentiment, max($sentiment))[0];

        // Retourner la catégorie de sentiment
        return $maxSentiment;
    }
}
