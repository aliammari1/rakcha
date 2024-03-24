<?php

namespace App\Entity;

use App\Repository\CommandeitemRepository;
use Doctrine\ORM\Mapping as ORM;


#[ORM\Entity(repositoryClass: CommandeitemRepository::class)]
#[ORM\Table(name: 'commandeitem')]
#[ORM\Index(name: 'fk_produit', columns: ['id_produit'])]
#[ORM\Index(name: 'fk_commande', columns: ['idCommande'])]
class Commandeitem
{
    #[ORM\Column(name: 'idCommandeItem', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $idcommandeitem;

    #[ORM\Column(name: 'quantity', type: 'integer', nullable: false)]
    private int $quantity;

    #[ORM\ManyToOne(targetEntity: Commande::class)]
    #[ORM\JoinColumn(name: 'idCommande', referencedColumnName: 'idCommande')]
    private ?Commande $idcommande = null;

    #[ORM\ManyToOne(targetEntity: Produit::class)]
    #[ORM\JoinColumn(name: 'id_produit', referencedColumnName: 'id_produit')]
    private ?Produit $idProduit = null;


}
