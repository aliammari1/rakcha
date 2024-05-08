<?php

namespace App\Repository;

use App\Entity\Series;
use App\Entity\Categories;
use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Series>
 *
 * @method Series|null find($id, $lockMode = null, $lockVersion = null)
 * @method Series|null findOneBy(array $criteria, array $orderBy = null)
 * @method Series[]    findAll()
 * @method Series[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class SeriesRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Series::class);
    }

//    /**
//     * @return Series[] Returns an array of Series objects
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

//    public function findOneBySomeField($value): ?Series
//    {
//        return $this->createQueryBuilder('s')
//            ->andWhere('s.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }
public function findRelaxingSeries(int $comedyCategoryId): array
{
    $qb = $this->createQueryBuilder('s')
        ->join('s.idcategorie', 'c')
        ->andWhere('c.idcategorie = :categoryId')
        ->setParameter('categoryId', $comedyCategoryId);

    // Exécutez la requête et retournez le résultat
    return $qb->getQuery()->getResult();
}

    
        public function getStatisticsByCategory(): array
    {
        // Récupérer toutes les catégories
        $categories = $this->getEntityManager()->getRepository(Categories::class)->findAll();
        
        // Initialiser un tableau pour stocker les statistiques par catégorie
        $statistics = [];
        
        // Pour chaque catégorie, compter le nombre de séries associées
        foreach ($categories as $category) {
            $seriesCount = $this->count(['idcategorie' => $category->getIdcategorie()]);
            
            // Stocker les statistiques dans le tableau
            $statistics[] = [
                'categoryName' => $category->getNom(), // Supposant que le nom de la catégorie est stocké dans la propriété 'nom' de l'entité Categories
                'seriesCount' => $seriesCount,
            ];
        }
        
        return $statistics;
    }
    

    public function findMostLiked(int $limit = 3): array
    {
        return $this->createQueryBuilder('s')
            ->orderBy('s.nblikes', 'DESC')
            ->setMaxResults($limit)
            ->getQuery()
            ->getResult();
    }
      
    

    public function search(?string $nom, ?string $directeur, ?string $pays): array
    {
        $qb = $this->createQueryBuilder('s');

        if ($nom) {
            $qb->andWhere('s.nom LIKE :nom')
                ->setParameter('nom', '%' . $nom . '%');
        }

        if ($directeur) {
            $qb->andWhere('s.directeur LIKE :directeur')
                ->setParameter('directeur', '%' . $directeur . '%');
        }

        if ($pays) {
            $qb->andWhere('s.pays LIKE :pays')
                ->setParameter('pays', '%' . $pays . '%');
        }

        return $qb->getQuery()->getResult();
    }


}