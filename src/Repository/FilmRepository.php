<?php

namespace App\Repository;

use App\Entity\Film;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;
use Symfony\Component\Serializer\SerializerInterface;
use Symfony\Contracts\HttpClient\HttpClientInterface;


/**
 * @extends ServiceEntityRepository<Film>
 *
 * @method Film|null find($id, $lockMode = null, $lockVersion = null)
 * @method Film|null findOneBy(array $criteria, array $orderBy = null)
 * @method Film[]    findAll()
 * @method Film[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class FilmRepository extends ServiceEntityRepository
{
    private $client;
    private $serializer;

    public function __construct(ManagerRegistry $registry, HttpClientInterface $client, SerializerInterface $serializer)
    {
        parent::__construct($registry, Film::class);

        $this->client = $client;
        $this->serializer = $serializer;
    }

    /**
     * Scrape IMDb search to get the first result's URL for a film name using the /search/title endpoint and the new selector.
     */
    public function getImdbUrlByFilmName(string $filmName): ?string
    {
        try {
            $query = urlencode($filmName);
            $url = "https://www.imdb.com/search/title/?title={$query}&title_type=feature,tv_movie";
            $response = $this->client->request('GET', $url, [
                'headers' => [
                    'User-Agent' => 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36',
                    'Accept' => 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
                ]
            ]);
            $html = $response->getContent();
            $crawler = new \Symfony\Component\DomCrawler\Crawler($html);
            // Use the new selector for the first result's link
            $link = $crawler->filter('li.ipc-metadata-list-summary-item a.ipc-lockup-overlay')->first();
            if ($link->count() > 0) {
                $href = $link->attr('href');
                return 'https://www.imdb.com' . $href;
            }
            // Optionally log HTML for debugging
            // file_put_contents(__DIR__.'/imdb_debug.html', $html);
        } catch (\Exception $e) {
            // Optionally log: error_log($e->getMessage());
        }
        return "";
    }

    //    /**
//     * @return Film[] Returns an array of Film objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('f')
//            ->andWhere('f.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('f.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

    //    public function findOneBySomeField($value): ?Film
//    {
//        return $this->createQueryBuilder('f')
//            ->andWhere('f.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
