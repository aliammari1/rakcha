<?php

namespace App\Repository;

use App\Entity\Filmcategory;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Filmcategory>
 *
 * @method Filmcategory|null find($id, $lockMode = null, $lockVersion = null)
 * @method Filmcategory|null findOneBy(array $criteria, array $orderBy = null)
 * @method Filmcategory[]    findAll()
 * @method Filmcategory[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class FilmcategoryRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Filmcategory::class);
    }

//    /**
//     * @return Filmcategory[] Returns an array of Filmcategory objects
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

//    public function findOneBySomeField($value): ?Filmcategory
//    {
//        return $this->createQueryBuilder('f')
//            ->andWhere('f.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
