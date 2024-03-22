<?php

namespace App\Repository;

use App\Entity\Filmcinema;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Filmcinema>
 *
 * @method Filmcinema|null find($id, $lockMode = null, $lockVersion = null)
 * @method Filmcinema|null findOneBy(array $criteria, array $orderBy = null)
 * @method Filmcinema[]    findAll()
 * @method Filmcinema[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class FilmcinemaRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Filmcinema::class);
    }

//    /**
//     * @return Filmcinema[] Returns an array of Filmcinema objects
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

//    public function findOneBySomeField($value): ?Filmcinema
//    {
//        return $this->createQueryBuilder('f')
//            ->andWhere('f.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
