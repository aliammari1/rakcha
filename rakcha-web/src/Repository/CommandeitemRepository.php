<?php

namespace App\Repository;

use App\Entity\Commandeitem;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Commandeitem>
 *
 * @method Commandeitem|null find($id, $lockMode = null, $lockVersion = null)
 * @method Commandeitem|null findOneBy(array $criteria, array $orderBy = null)
 * @method Commandeitem[]    findAll()
 * @method Commandeitem[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class CommandeitemRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Commandeitem::class);
    }

//    /**
//     * @return Commandeitem[] Returns an array of Commandeitem objects
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

//    public function findOneBySomeField($value): ?Commandeitem
//    {
//        return $this->createQueryBuilder('c')
//            ->andWhere('c.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
