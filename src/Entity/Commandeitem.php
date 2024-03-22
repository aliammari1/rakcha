<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

use App\Repository\CommandeitemRepository;

#[ORM\Entity(repositoryClass: CommandeitemRepository::class)]
class Commandeitem
{
    /**
     * @var int
     */
    #[ORM\Column(name: 'idCommandeItem', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue]
    private int $idcommandeitem;

    /**
     * @var int
     */
    #[ORM\Column(name: 'quantity', type: 'integer', nullable: false)]
    private int $quantity;

    /**
     * @var Commande
     */
    #[ORM\ManyToOne(targetEntity: Commande::class)]
    #[ORM\JoinColumn(name: 'idCommande', referencedColumnName: 'idCommande')]
    private ?Commande $idcommande = null;

    /**
     * @var Produit
     */
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
