<?php

namespace App\Repository;

use App\Entity\Ratingfilm;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Ratingfilm>
 *
 * @method Ratingfilm|null find($id, $lockMode = null, $lockVersion = null)
 * @method Ratingfilm|null findOneBy(array $criteria, array $orderBy = null)
 * @method Ratingfilm[]    findAll()
 * @method Ratingfilm[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class RatingfilmRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Ratingfilm::class);
    }

    public function getAverageRating($id)
    {
        $query = $this->createQueryBuilder('r')
            ->select('AVG(r.rate) as rate_avg')
            ->where('r.idFilm = :idFilm')
            ->groupBy('r.idFilm')
            ->setParameter('idFilm', $id)
            ->getQuery();

        return $query->getResult();
    }


//    /**
//     * @return Ratingfilm[] Returns an array of Ratingfilm objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('r')
//            ->andWhere('r.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('r.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

    //    public function findOneBySomeField($value): ?Ratingfilm
//    {
//        return $this->createQueryBuilder('r')
//            ->andWhere('r.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
