<?php

namespace App\Controller;

use App\Entity\Film;
use App\Repository\ActorRepository;
use App\Repository\FilmRepository;
use App\Repository\RatingfilmRepository;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Madcoda\Youtube\Youtube;
use Symfony\Component\HttpFoundation\Request;

class ListfilmsController extends AbstractController
{
    #[Route('/listfilms', name: 'app_listfilms_index')]
    public function index(FilmRepository $filmRepository, RatingfilmRepository $ratingfilmRepository): Response
    {
        $youtube = new Youtube(array('key' => 'AIzaSyDnbB_QFZd-4YVa5d8dHHbN_SXDj2mq2EE'));
        $films = $filmRepository->findAll();
        $videoUrls = array();
        $averageRatings = array();
        $ratings = array();
        foreach ($films as $film) {
            $averageRatings[] = $ratingfilmRepository->getAverageRating($film->getId());
            $ratings[] = $ratingfilmRepository->findOneBy(['idFilm' => $film->getId(), 'idUser' => 1])?->getRate();
            $videoList = json_decode(json_encode($youtube->searchVideos($film->getNom() . ' trailer', 1)), true);
            if (!empty($videoList)) {
                $firstVideo = $videoList[0]['id']['videoId'];
            } else {
                $firstVideo = '';
            }
            $videoUrls[] = "https://www.youtube.com/embed/{$firstVideo}";
        }
        // dd($averageRatings);
        return $this->render('front/listfilms.html.twig', [
            'films' => $filmRepository->findAll(),
            'videoUrl' => $videoUrls,
            'averageRatings' => $averageRatings,
            'ratings' => $ratings
        ]);
    }

    #[Route('/listfilms/bookmarks', name: 'app_film_bookmarks_index')]
    public function bookmarks(FilmRepository $filmRepository, RatingfilmRepository $ratingfilmRepository): Response
    {
        $youtube = new Youtube(array('key' => 'AIzaSyDnbB_QFZd-4YVa5d8dHHbN_SXDj2mq2EE'));
        $films = $filmRepository->findBy(['isBookmarked' => true]);
        $videoUrls = array();
        $averageRatings = array();
        foreach ($films as $film) {
            $averageRatings[] = $ratingfilmRepository->getAverageRating($film->getId());
            $videoList = json_decode(json_encode($youtube->searchVideos($film->getNom() . ' trailer', 1)), true);
            if (!empty($videoList)) {
                $firstVideo = $videoList[0]['id']['videoId'];
            } else {
                $firstVideo = '';
            }
            $videoUrls[] = "https://www.youtube.com/embed/{$firstVideo}";
        }
        // dd($averageRatings);
        return $this->render('front/listfilms.html.twig', [
            'films' => $filmRepository->findBy(['isBookmarked' => true]),
            'videoUrl' => $videoUrls,
            'averageRatings' => $averageRatings
        ]);
    }

    #[Route('/bookmark', name: 'app_bookmark_film')]
    public function bookmark(Request $request, FilmRepository $filmRepository, EntityManagerInterface $entityManager): Response
    {
        $data = json_decode($request->getContent(), true);
        $film = $filmRepository->findOneBy(['id' => $data["id"]]);
        $film->setIsBookmarked($data["isBookmarked"]);
        $entityManager->persist($film);
        $entityManager->flush();
        return new JsonResponse(["success" => true, 'bookmarked' => $film->getIsBookmarked()]);
    }

    #[Route('/search', name: 'app_search_film')]
    public function search(Request $request, FilmRepository $filmRepository): Response
    {
        $data = json_decode($request->getContent(), true);
    
        $films = $filmRepository->createQueryBuilder('f')
           ->select('f.id')
           ->andWhere('f.nom LIKE :nom')
           ->setParameter('nom', '%' .($data['search'] ?? ''). '%')
           ->getQuery()
           ->getResult();
    
        $films = array_column($films, 'id');
        return new JsonResponse(["success" => true, 'films' => $films, 'data' => $data]);
    }

    #[Route('/filmHome', name: 'app_listhome_index')]
    public function indexHome(FilmRepository $filmRepository, RatingfilmRepository $ratingfilmRepository): Response
    {

        $youtube = new Youtube(array('key' => 'AIzaSyCj9EOgQTfpnnY5MDwcp91FkmyfXp2k1KY'));
        $films = $filmRepository->findAll();
        $videoUrls = array();
        $averageRatings = array();
        foreach ($films as $film) {
            $averageRatings[] = $ratingfilmRepository->getAverageRating($film->getId());
            $videoList = $youtube->searchVideos($film->getNom() . ' trailer');
            $videoList = json_decode(json_encode($videoList), true);
            $firstVideo = $videoList[0]->getId()->getVideoId();
            $videoUrls[] = "https://www.youtube.com/embed/{$firstVideo}";
        }

        return $this->render('front/film.html.twig', [
            'films' => $filmRepository->findAll(),
            'videoUrl' => $videoUrls,
            'averageRatings' => $averageRatings
        ]);
    }
    #[Route('/listactors', name: 'app_listactors_index')]
    public function indexactor(ActorRepository $filmRepository): Response
    {
        return $this->render('front/listactors.html.twig', [
            'actors' => $filmRepository->findAll(),
        ]);
    }
}
