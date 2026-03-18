<?php

namespace App\Repository;

use App\Entity\Actorfilm;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Actorfilm>
 *
 * @method Actorfilm|null find($id, $lockMode = null, $lockVersion = null)
 * @method Actorfilm|null findOneBy(array $criteria, array $orderBy = null)
 * @method Actorfilm[]    findAll()
 * @method Actorfilm[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class ActorfilmRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Actorfilm::class);
    }

//    /**
//     * @return Actorfilm[] Returns an array of Actorfilm objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('a')
//            ->andWhere('a.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('a.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

//    public function findOneBySomeField($value): ?Actorfilm
//    {
//        return $this->createQueryBuilder('a')
//            ->andWhere('a.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
