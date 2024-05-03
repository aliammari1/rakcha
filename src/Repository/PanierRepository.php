<?php

namespace App\Repository;

use App\Entity\Panier;
use App\Entity\Produit;
use App\Entity\Users;


use Doctrine\Bundle\DoctrineBundle\Repository\ServiceEntityRepository;
use Doctrine\Persistence\ManagerRegistry;

/**
 * @extends ServiceEntityRepository<Panier>
 *
 * @method Panier|null find($id, $lockMode = null, $lockVersion = null)
 * @method Panier|null findOneBy(array $criteria, array $orderBy = null)
 * @method Panier[]    findAll()
 * @method Panier[]    findBy(array $criteria, array $orderBy = null, $limit = null, $offset = null)
 */
class PanierRepository extends ServiceEntityRepository
{
    public function __construct(ManagerRegistry $registry)
    {
        parent::__construct($registry, Panier::class);
    }

//    /**
//     * @return Panier[] Returns an array of Panier objects
//     */
//    public function findByExampleField($value): array
//    {
//        return $this->createQueryBuilder('p')
//            ->andWhere('p.exampleField = :val')
//            ->setParameter('val', $value)
//            ->orderBy('p.id', 'ASC')
//            ->setMaxResults(10)
//            ->getQuery()
//            ->getResult()
//        ;
//    }

//    public function findOneBySomeField($value): ?Panier
//    {
//        return $this->createQueryBuilder('p')
//            ->andWhere('p.exampleField = :val')
//            ->setParameter('val', $value)
//            ->getQuery()
//            ->getOneOrNullResult()
//        ;
//    }

/**
     * Calcule la quantité de produit dans le panier pour un utilisateur donné.
     *
     * @param Produit $produit Le produit pour lequel calculer la quantité dans le panier
     * @param Users $user L'utilisateur pour lequel calculer la quantité dans le panier
     * @return int La quantité de produit dans le panier pour l'utilisateur donné
     */
    public function getQuantiteDansPanier(Produit $produit, Users $user): int
    {
        $qb = $this->createQueryBuilder('p')
            ->select('SUM(p.quantite)')
            ->andWhere('p.produit = :produit')
            ->andWhere('p.user = :user')
            ->setParameter('produit', $produit)
            ->setParameter('user', $user);

        $result = $qb->getQuery()->getSingleScalarResult();

        return $result ? (int)$result : 0;
    }

    /**
     * Récupère la quantité de chaque produit dans le panier pour un utilisateur donné.
     *
     * @param Users $user L'utilisateur pour lequel récupérer les quantités dans le panier
     * @return array Un tableau associatif où les clés sont les IDs de produit et les valeurs sont les quantités dans le panier
     */
    public function getQuantitesDansPanierParProduit(Users $user): array
    {
        $qb = $this->createQueryBuilder('p')
            ->select('IDENTITY(p.idproduit) AS id_produit', 'SUM(p.quantite) AS quantite')
            ->andWhere('p.idclient = :user')
            ->groupBy('p.idproduit')
            ->setParameter('user', $user);
    
        $results = $qb->getQuery()->getResult();
    
        $quantitesParProduit = [];
        foreach ($results as $result) {
            $quantitesParProduit[$result['id_produit']] = (int)$result['quantite'];
        }
    
        return $quantitesParProduit;
    }

    

}
