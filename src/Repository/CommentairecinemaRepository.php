<?php

namespace App\Repository;

use App\Entity\Commentairecinema;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Commentairecinema>
 *
 * @method Commentairecinema|null find($id, $lockMode = null, $lockVersion = null)
 * @method Commentairecinema|null findOneBy(array $criteria, array $orderBy = null)
 * @method Commentairecinema[]    findAll()
 * @method Commentairecinema[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class CommentairecinemaRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Commentairecinema::class);
    }

//    /**
//     * @return Commentairecinema[] Returns an array of Commentairecinema objects
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

//    public function findOneBySomeField($value): ?Commentairecinema
//    {
//        return $this->createQueryBuilder('c')
//            ->andWhere('c.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
}
