<?php

namespace App\Repository;

use App\Entity\Friendships;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Friendships>
 *
 * @method Friendships|null find($id, $lockMode = null, $lockVersion = null)
 * @method Friendships|null findOneBy(array $criteria, array $orderBy = null)
 * @method Friendships[]    findAll()
 * @method Friendships[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class FriendshipsRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Friendships::class);
    }

//    /**
//     * @return Friendships[] Returns an array of Friendships objects
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

//    public function findOneBySomeField($value): ?Friendships
//    {
//        return $this->createQueryBuilder('f')
//            ->andWhere('f.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
