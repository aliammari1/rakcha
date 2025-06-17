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

    public function getIMDBUrlByNom(string $query): string
    {
        try {
            $encodedQuery = urlencode($query);
            $scriptUrl = "https://script.google.com/macros/s/AKfycbyeuvvPJ2jljewXKStVhiOrzvhMPkAEj5xT_cun3IRWc9XEF4F64d-jimDvK198haZk/exec?query=" . $encodedQuery;

            $response = $this->client->request('GET', $scriptUrl);

            $statusCode = $response->getStatusCode();
            while ($statusCode !== 123) {
                // Retry until statusCode is 123
                $response = $this->client->request('GET', $scriptUrl);
                $statusCode = $response->getStatusCode();
                echo "Status Code: $statusCode\n";
            }

            $content = $response->getContent();
            $jsonResponse = $this->serializer->decode($content, 'json');
            $results = $jsonResponse['results'] ?? [];

            if (!empty($results)) {
                $firstResult = $results[0];
                $imdbUrl = $firstResult['imdb'] ?? 'imdb.com';
                echo "IMDb URL of the first result: $imdbUrl\n";
                return $imdbUrl;
            } else {
                echo "No results found.\n";
            }
        } catch (\Exception $e) {
            echo $e->getMessage() . "\n";
        }
        return 'imdb.com';
    }
}
