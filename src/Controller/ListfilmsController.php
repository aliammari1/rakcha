<?php

namespace App\Controller;

use App\Repository\ActorRepository;
use App\Repository\FilmRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;
use Madcoda\Youtube\Youtube;


class ListfilmsController extends AbstractController
{
    #[Route('/listfilms', name: 'app_listfilms_index')]
    public function index(FilmRepository $filmRepository): Response
    {
        $youtube = new Youtube(array('key' => 'AIzaSyDtd3AAs9ukAs255nuuqOmGka4uakmfXH4'));
        $films = $filmRepository->findAll();
        $videoUrls = array();
        foreach ($films as $film) {
            $videoList = json_decode(json_encode($youtube->searchVideos($film->getNom() . ' trailer', 1)), true);
            if (!empty($videoList)) {
                $firstVideo = $videoList[0]['id']['videoId'];
            } else {
                $firstVideo = '';
            }
            $videoUrls[] = "https://www.youtube.com/embed/{$firstVideo}";
        }

        return $this->render('front/listfilms.html.twig', [
            'films' => $filmRepository->findAll(),
            'videoUrl' => $videoUrls

        ]);
    }
    #[Route('/filmHome', name: 'app_listhome_index')]
    public function indexHome(FilmRepository $filmRepository): Response
    {

        $youtube = new Youtube(array('key' => 'AIzaSyDtd3AAs9ukAs255nuuqOmGka4uakmfXH4'));
        $films = $filmRepository->findAll();
        $videoUrls = array();
        
        foreach ($films as $film) {
          
            $videoList = $youtube->searchVideos($film->getNom() . ' trailer');
            $videoList= json_decode(json_encode($videoList), true);
            $firstVideo = $videoList[0]->getId()->getVideoId();
            $videoUrls[] = "https://www.youtube.com/embed/{$firstVideo}";
            
        }
        dd( $videoList);

        return $this->render('front/film.html.twig', [
            'films' => $filmRepository->findAll(),
            'videoUrl' => $videoUrls
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
