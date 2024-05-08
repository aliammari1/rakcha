<?php

namespace App\Repository;

use App\Entity\FeedbackEvenement;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<FeedbackEvenement>
 *
 * @method FeedbackEvenement|null find($id, $lockMode = null, $lockVersion = null)
 * @method FeedbackEvenement|null findOneBy(array $criteria, array $orderBy = null)
 * @method FeedbackEvenement[]    findAll()
 * @method FeedbackEvenement[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class FeedbackEvenementRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, FeedbackEvenement::class);
    }

//    /**
//     * @return FeedbackEvenement[] Returns an array of FeedbackEvenement objects
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

//    public function findOneBySomeField($value): ?FeedbackEvenement
//    {
//        return $this->createQueryBuilder('f')
//            ->andWhere('f.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
