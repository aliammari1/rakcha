<?php

namespace App\Entity;

use App\Repository\CommandeitemRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;



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

    public function getIdcommandeitem(): ?int
    {
        return $this->idcommandeitem;
    }

    public function getQuantity(): ?int
    {
        return $this->quantity;
    }

    public function setQuantity(int $quantity): static
    {
        $this->quantity = $quantity;

        return $this;
    }

    public function getIdcommande(): ?Commande
    {
        return $this->idcommande;
    }

    public function setIdcommande(?Commande $idcommande): static
    {
        $this->idcommande = $idcommande;

        return $this;
    }

    public function getIdProduit(): ?Produit
    {
        return $this->idProduit;
    }

    public function setIdProduit(?Produit $idProduit): static
    {
        $this->idProduit = $idProduit;

        return $this;
    }
}
