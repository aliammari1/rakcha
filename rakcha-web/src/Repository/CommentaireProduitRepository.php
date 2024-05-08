<?php

namespace App\Repository;

use App\Entity\CommentaireProduit;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<CommentaireProduit>
 *
 * @method CommentaireProduit|null find($id, $lockMode = null, $lockVersion = null)
 * @method CommentaireProduit|null findOneBy(array $criteria, array $orderBy = null)
 * @method CommentaireProduit[]    findAll()
 * @method CommentaireProduit[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class CommentaireProduitRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, CommentaireProduit::class);
    }

//    /**
//     * @return CommentaireProduit[] Returns an array of CommentaireProduit objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('c')
//            ->andWhere('c.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('c.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

//    public function findOneBySomeField($value): ?CommentaireProduit
//    {
//        return $this->createQueryBuilder('c')
//            ->andWhere('c.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
