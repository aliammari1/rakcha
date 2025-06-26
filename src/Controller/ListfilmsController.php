<?php

namespace App\Controller;

use App\Form\ReservationFormType;
use App\Repository\ActorRepository;
use App\Repository\CategoryRepository;
use App\Repository\FilmRepository;
use App\Repository\RatingfilmRepository;
use App\Repository\SeanceRepository;
use BaconQrCode\Renderer\Image\ImagickImageBackEnd;
use BaconQrCode\Renderer\ImageRenderer;
use BaconQrCode\Renderer\RendererStyle\RendererStyle;
use BaconQrCode\Writer;
use Doctrine\ORM\EntityManagerInterface;
use Endroid\QrCode\Builder\Builder;
use Endroid\QrCodeBundle\Response\QrCodeResponse;
use Exception;
use Madcoda\Youtube\Youtube;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\DomCrawler\Crawler;
use Symfony\Component\HttpClient\HttpClient;
use Symfony\Component\HttpFoundation\JsonResponse;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Annotation\Route;

class ListfilmsController extends AbstractController
{
    #[Route('/listfilms', name: 'app_listfilms_index')]
    public function index(FilmRepository $filmRepository, RatingfilmRepository $ratingfilmRepository, CategoryRepository $categoryRepository, SeanceRepository $seanceRepository): Response
    {
        $youtube = new Youtube(array('key' => $_ENV["YOUTUBE_API_KEY"]));
        $films = $filmRepository->findAll();
        $videoUrls = array();
        $averageRatings = array();
        $ratings = array();
        $seanceFilmMatrix = array();
        $categorys = $categoryRepository->findAll();
        $urls = array();
        foreach ($films as $film) {
            // Use web scraping to get IMDb URL
            $url = $filmRepository->getImdbUrlByFilmName($film->getNom());
            $urls[] = $url;
            $seanceFilmMatrix[] = $seanceRepository->findBy(['idFilm' => $film->getId()]);
            $averageRatings[] = $ratingfilmRepository->getAverageRating($film->getId());
            if ($this->getUser()) {
                $ratingfilmRepository->findOneBy(['idFilm' => $film->getId(), 'idUser' => $this->getUser()->getId()]);
            } else {
                $ratings[] = 0;
            }
            $videoList = json_decode(json_encode($youtube->searchVideos($film->getNom() . ' trailer', 1)), true);
            if (!empty($videoList)) {
                $firstVideo = $videoList[0]['id']['videoId'];
            } else {
                $firstVideo = '';
            }
            $videoUrls[] = "https://www.youtube.com/embed/{$firstVideo}";
        }
        return $this->render('front/listfilms.html.twig', [
            'films' => $films,
            'videoUrl' => $videoUrls,
            'averageRatings' => $averageRatings,
            'ratings' => $ratings,
            'categorys' => $categorys,
            'seances' => $seanceFilmMatrix,
            'urls' => $urls,
            'stripe_key' => $_ENV["STRIPE_KEY"],
        ]);
    }

    #[Route('/search', name: 'app_search_film')]
    public function search(Request $request, FilmRepository $filmRepository): Response
    {
        $data = json_decode($request->getContent(), true);

        $films = $filmRepository->createQueryBuilder('f')
            ->select('f.id')
            ->andWhere('f.nom LIKE :nom')
            ->setParameter('nom', '%' . ($data['search'] ?? '') . '%')
            ->getQuery()
            ->getResult();

        $films = array_column($films, 'id');
        return $this->json(["success" => true, 'films' => $films, 'data' => $data]);
    }

    #[Route('/listfilms/bookmarks', name: 'app_film_bookmarks_index')]
    public function bookmarks(FilmRepository $filmRepository, RatingfilmRepository $ratingfilmRepository, CategoryRepository $categoryRepository, SeanceRepository $seanceRepository): Response
    {
        $youtube = new Youtube(array('key' => $_ENV["YOUTUBE_API_KEY"]));
        $films = $filmRepository->findBy(['isBookmarked' => true]);
        $videoUrls = array();
        $averageRatings = array();
        $ratings = array();
        $seanceFilmMatrix = array();
        $categorys = $categoryRepository->findAll();
        $urls = array();
        foreach ($films as $film) {
            // Use web scraping to get IMDb URL
            $url = $filmRepository->getImdbUrlByFilmName($film->getNom());
            $urls[] = $url;
            $seanceFilmMatrix[] = $seanceRepository->findBy(['idFilm' => $film->getId()]);
            $averageRatings[] = $ratingfilmRepository->getAverageRating($film->getId());
            $ratings[] = $ratingfilmRepository->findOneBy(['idFilm' => $film->getId(), 'idUser' => $this->getUser()->getId()]);
            $videoList = json_decode(json_encode($youtube->searchVideos($film->getNom() . ' trailer', 1)), true);
            if (!empty($videoList)) {
                $firstVideo = $videoList[0]['id']['videoId'];
            } else {
                $firstVideo = '';
            }
            $videoUrls[] = "https://www.youtube.com/embed/{$firstVideo}";
        }
        return $this->render('front/listfilms.html.twig', [
            'films' => $films,
            'videoUrl' => $videoUrls,
            'averageRatings' => $averageRatings,
            'ratings' => $ratings,
            'categorys' => $categorys,
            'seances' => $seanceFilmMatrix,
            'urls' => $urls,
            'stripe_key' => $_ENV["STRIPE_KEY"],
        ]);
    }

    public function generateQRCode($url)
    {
        $renderer = new ImageRenderer(
            new RendererStyle(400),
            new ImagickImageBackEnd()
        );
        $writer = new Writer($renderer);
        // Generate QR code image
        return $writer->writeString($url);
    }

    #[Route('/bookmark', name: 'app_bookmark_film')]
    public function bookmark(Request $request, FilmRepository $filmRepository, EntityManagerInterface $entityManager): Response
    {
        try {
            $data = json_decode($request->getContent(), true);
            $film = $filmRepository->findOneBy(['id' => $data["id"]]);
            $film->setIsBookmarked($data["isBookmarked"]);
            $entityManager->persist($film);
            $entityManager->flush();
        } catch (Exception $e) {
            return new JsonResponse(["success" => false, "message" => $e->getMessage()]);
        }
        return new JsonResponse(["success" => true, 'bookmarked' => $film->getIsBookmarked()]);
    }

    #[Route('/qrcode/{filmName}', name: 'app_qrcode_film')]
    public function qrcode($filmName, FilmRepository $filmRepository, EntityManagerInterface $entityManager): Response
    {
        $result = Builder::create()
            ->data($filmRepository->getImdbUrlByFilmName($filmName))
            ->size(150)
            ->build();
        return new QrCodeResponse($result);
    }

    #[Route('/qrcodeurl/{filmName}', name: 'app_qrcodeurl_film')]
    public function qrcodeurl($filmName, FilmRepository $filmRepository, EntityManagerInterface $entityManager): Response
    {
        return new Response();
    }

    #[Route('/reserve', name: 'app_reserve_film')]
    public function reserve(FilmRepository $filmRepository, RatingfilmRepository $ratingfilmRepository, CategoryRepository $categoryRepository): Response
    {
        return new JsonResponse(['success' => true]);
    }

    #[Route('/filterByCategory', name: 'app_filter_category_film')]
    public function filterByCategory(Request $request, FilmRepository $filmRepository): Response
    {
        $data = json_decode($request->getContent(), true);

        // Retrieve films from the database
        $films = $filmRepository->findAll();
        // Initialize an empty array to store categorized films
        $filmsCategorized = [];

        foreach ($films as $film) {
            $categories = $film->getCategorys();

            $allCategoriesPresent = true;

            foreach ($data["checkboxes"] as $categoryName) {
                $categoryFound = false;
                foreach ($categories as $category) {
                    if ($category->getNom() === $categoryName) {
                        $categoryFound = true;
                        break;
                    }
                }
                if (!$categoryFound) {
                    // If any category is not found, set the flag to false and exit the loop
                    $allCategoriesPresent = false;
                    break;
                }
            }

            // If all categories are present for the current film, add it to the categorized films array
            if ($allCategoriesPresent) {
                $filmsCategorized[] = $film->getId();
            }
        }

        return $this->json(["success" => true, 'filmsCategorized' => $filmsCategorized, 'data' => $data["checkboxes"], 'ids' => array_column($filmRepository->findAll(), 'id')]);
    }

    #[Route('/filmHome', name: 'app_listhome_index')]
    public function indexHome(FilmRepository $filmRepository, RatingfilmRepository $ratingfilmRepository): Response
    {

        $youtube = new Youtube(array('key' => $_ENV["YOUTUBE_API_KEY"]));
        $films = $filmRepository->findAll();
        $videoUrls = array();
        $averageRatings = array();
        foreach ($films as $film) {
            $averageRatings[] = $ratingfilmRepository->getAverageRating($film->getId());
            $videoList = $youtube->searchVideos($film->getNom() . ' trailer');
            $videoList = json_decode(json_encode($youtube->searchVideos($film->getNom() . ' trailer', 1)), true);
            if (!empty($videoList)) {
                $firstVideo = $videoList[0]['id']['videoId'];
            } else {
                $firstVideo = '';
            }
            $videoUrls[] = "https://www.youtube.com/embed/{$firstVideo}";
        }

        return $this->render('front/film.html.twig', [
            'films' => $filmRepository->findAll(),
            'videoUrl' => $videoUrls,
            'averageRatings' => $averageRatings
        ]);
    }

    #[Route('/listactors', name: 'app_listactors_index')]
    public function indexactor(ActorRepository $actorRepository): Response
    {
        return $this->render('front/listactors.html.twig', [
            'actors' => $actorRepository->findAll(),
        ]);
    }

    #[Route('/filShow/{id}', name: 'app_filShow_index')]
    public function filmShow($id, FilmRepository $filmRepository, SeanceRepository $seanceRepository, RatingfilmRepository $ratingfilmRepository): Response
    {
        $youtube = new Youtube(array('key' => $_ENV["YOUTUBE_API_KEY"]));
        $film = $filmRepository->find($id);
        $url = $filmRepository->getImdbUrlByFilmName($film->getNom());
        $averageRating = $ratingfilmRepository->getAverageRating($film->getId());
        $videoList = json_decode(json_encode($youtube->searchVideos($film->getNom() . ' trailer', 1)), true);
        if (!empty($videoList)) {
            $firstVideo = $videoList[0]['id']['videoId'];
        } else {
            $firstVideo = '';
        }
        $videoUrl = "https://www.youtube.com/embed/{$firstVideo}";
        return $this->render('front/filmDetails.html.twig', [
            'film' => $film,
            'averageRating' => $averageRating,
            'videoUrl' => $videoUrl,
            'imdbUrl' => $url
        ]);
    }

    #[Route('/seanceSeats', name: 'app_seance_seats_film')]
    public function getSeatsBySeance(Request $request, SeanceRepository $seanceRepository): Response
    {
        try {
            $data = json_decode($request->getContent(), true);
            $seance = $seanceRepository->findOneBy(['idSeance' => $data['seanceId']]);
            $seanceSeats = $seance->getIdSalle()->getSeats();
            $seatsArray = [];

            foreach ($seanceSeats as $seat) {
                $seatsArray[] = [
                    'id' => $seat->getId(),
                    'status' => $seat->getStatut(),
                    'prix' => $seance->getPrix()
                ];
            }
        } catch (Exception $e) {
            return $this->json(["success" => false, "message" => $e->getMessage()]);
        }
        return $this->json(["success" => true, 'seatsArray' => $seatsArray, 'data' => $data]);
    }
}
