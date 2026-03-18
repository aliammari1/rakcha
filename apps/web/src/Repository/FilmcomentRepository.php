<?php

namespace App\Repository;

use App\Entity\Filmcoment;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Filmcoment>
 *
 * @method Filmcoment|null find($id, $lockMode = null, $lockVersion = null)
 * @method Filmcoment|null findOneBy(array $criteria, array $orderBy = null)
 * @method Filmcoment[]    findAll()
 * @method Filmcoment[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class FilmcomentRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Filmcoment::class);
    }

//    /**
//     * @return Filmcoment[] Returns an array of Filmcoment objects
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

//    public function findOneBySomeField($value): ?Filmcoment
//    {
//        return $this->createQueryBuilder('f')
//            ->andWhere('f.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
