<?php

namespace App\Repository;

use App\Entity\Seance;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\DBAL\Types\Types;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Seance>
 *
 * @method Seance|null find($id, $lockMode = null, $lockVersion = null)
 * @method Seance|null findOneBy(array $criteria, array $orderBy = null)
 * @method Seance[]    findAll()
 * @method Seance[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class SeanceRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Seance::class);
    }

    /** @return list<Seance> */
    public function findUpcoming(?int $filmId = null, ?int $cinemaId = null, int $limit = 24): array
    {
        $now = new \DateTime('now', new \DateTimeZone('Africa/Tunis'));
        $query = $this->createQueryBuilder('s')
            ->addSelect('film', 'hall', 'cinema')
            ->innerJoin('s.idFilm', 'film')
            ->innerJoin('s.idSalle', 'hall')
            ->innerJoin('s.idCinema', 'cinema')
            ->andWhere('cinema.statut = :status')
            ->andWhere('(s.date > :today OR (s.date = :today AND s.hd >= :time))')
            ->andWhere('s.hf IS NOT NULL AND s.prix IS NOT NULL')
            ->setParameter('status', 'Accepted')
            ->setParameter('today', $now, Types::DATE_MUTABLE)
            ->setParameter('time', $now, Types::TIME_MUTABLE)
            ->orderBy('s.date', 'ASC')
            ->addOrderBy('s.hd', 'ASC')
            ->setMaxResults($limit);

        if ($filmId !== null) {
            $query->andWhere('film.id = :film')->setParameter('film', $filmId);
        }
        if ($cinemaId !== null) {
            $query->andWhere('cinema.idCinema = :cinema')->setParameter('cinema', $cinemaId);
        }

        return $query->getQuery()->getResult();
    }

    //    /**
    //     * @return Seance[] Returns an array of Seance objects
    //     */
    //    public function findByExampleField($value): array
    //    {
    //        return $this->createQueryBuilder('s')
    //            ->andWhere('s.exampleField = :val')
    //            ->setParameter('val', $value)
    //            ->orderBy('s.id', 'ASC')
    //            ->setMaxResults(10)
    //            ->getQuery()
    //            ->getResult()
    //        ;
    //    }

    //    public function findOneBySomeField($value): ?Seance
    //    {
    //        return $this->createQueryBuilder('s')
    //            ->andWhere('s.exampleField = :val')
    //            ->setParameter('val', $value)
    //            ->getQuery()
    //            ->getOneOrNullResult()
    //        ;
    //    }
}
