<?php

namespace App\Controller;

use App\Entity\Commentairecinema;
use App\Form\CommentairecinemaType;
use App\Repository\CommentairecinemaRepository;
use App\Repository\UsersRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use App\Entity\Cinema;
use Sentiment\Analyzer;



#[Route('/commentairecinema')]
class CommentairecinemaController extends AbstractController
{
    #[Route('/{idCinema}', name: 'app_commentairecinema_index', methods: ['GET'])]
    public function index(int $idCinema, CommentairecinemaRepository $commentairecinemaRepository, EntityManagerInterface $entityManager): Response
    {
        $commentairecinema = new Commentairecinema();
        $form = $this->createForm(CommentairecinemaType::class, $commentairecinema);

        // Récupérer le cinema
        $cinema = $entityManager->getRepository(Cinema::class)->findOneBy(['idCinema' => $idCinema]);

        return $this->render('cinema/commentCinema.html.twig', [
            'commentairecinemas' => $commentairecinemaRepository->findAll(), 
            'form' => $form->createView(),
            'cinema' => $cinema, // Assurez-vous de passer le cinema au modèle
        ]);
    }

    #[Route('/new/{idCinema}', name: 'app_commentairecinema_new', methods: ['GET', 'POST'])]
    public function new(int $idCinema, Request $request, EntityManagerInterface $entityManager, UsersRepository $usersRepository, CommentairecinemaRepository $commentairecinemaRepository): Response
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
            $userId = 147;
    
            // Analyse de sentiment avec php-sentiment-analyzer
            $commentText = $commentairecinema->getCommentaire(); // Supposons que vous avez une méthode getComment() dans votre entité Commentairecinema
            $sentiment = $this->analyseSentiment($commentText);
    
            // Le sentiment est retourné sous forme de chaîne ('positive', 'negative', 'neutral')
            // Vous pouvez utiliser directement cette valeur comme sentiment du commentaire
            $commentairecinema->setSentiment($sentiment);
    
            $user = $usersRepository->find($userId);
            if ($user->getRole() == 'client') {
                $commentairecinema->setIdclient($user->getId());
            }
            $entityManager->persist($commentairecinema);
            $entityManager->flush();
    
            return $this->redirectToRoute('app_commentairecinema_index', ['idCinema' => $idCinema], Response::HTTP_SEE_OTHER);
        }
    
        return $this->render('cinema/commentCinema.html.twig', [
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

    



