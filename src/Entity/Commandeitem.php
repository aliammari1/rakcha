<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Commandeitem
 *
 * @ORM\Table(name="commandeitem", indexes={@ORM\Index(name="fk_produit", columns={"id_produit"}), @ORM\Index(name="fk_commande", columns={"idCommande"})})
 * @ORM\Entity
 */
class Commandeitem
{
    /**
     * @var int
     *
     * @ORM\Column(name="idCommandeItem", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $idcommandeitem;

    /**
     * @var int
     *
     * @ORM\Column(name="quantity", type="integer", nullable=false)
     */
    private $quantity;

    /**
     * @var \Commande
     *
     * @ORM\ManyToOne(targetEntity="Commande")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="idCommande", referencedColumnName="idCommande")
     * })
     */
    private $idcommande;

    /**
     * @var \Produit
     *
     * @ORM\ManyToOne(targetEntity="Produit")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="id_produit", referencedColumnName="id_produit")
     * })
     */
    private $idProduit;

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
