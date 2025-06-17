<?php

namespace App\Entity;

use App\Repository\PanierRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: PanierRepository::class)]
#[ORM\Index(name: 'FK_pro', columns: ['id_produit'])]
#[ORM\Index(name: 'FK_user', columns: ['idClient'])]
class Panier
{
    #[ORM\Column(name: 'idpanier', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $idpanier;

    #[ORM\ManyToOne(targetEntity: Produit::class)]
    #[ORM\JoinColumn(name: 'id_produit', referencedColumnName: 'id_produit')]
    private ?Produit $idproduit;

    #[ORM\ManyToOne(targetEntity: Users::class)]
    #[ORM\JoinColumn(name: 'idClient', referencedColumnName: 'id', onDelete: 'CASCADE')]
    private ?Users $idclient;

    #[ORM\Column(name: 'quantite', type: 'integer', nullable: true)]
    private ?int $quantite = null;
    private Collection $idClient;

    /**
     * Constructor
     */
    public function __construct()
    {
        $this->idClient = new ArrayCollection();
    }

    public function getIdpanier(): ?int
    {
        return $this->idpanier;
    }

    public function getIdproduit(): ?Produit
    {
        return $this->idproduit;
    }

    public function setIdproduit(?Produit $idproduit): static
    {
        $this->idproduit = $idproduit;

        return $this;
    }

    public function getClient(): ?Users
    {
        return $this->idclient;
    }

    public function setClient(?Users $client): static
    {
        $this->idclient = $client;

        return $this;
    }

    public function getQuantite(): ?int
    {
        return $this->quantite;
    }

    public function setQuantite(int $quantite): static
    {
        $this->quantite = $quantite;

        return $this;
    }

    /**
     * @return Collection<int, Users>
     */
    public function getIdClient(): Collection
    {
        return $this->idClient;
    }

    public function addIdClient(Users $idClient): static
    {
        if (!$this->idClient->contains($idClient)) {
            $this->idClient->add($idClient);
        }

        return $this;
    }

    public function removeIdClient(Users $idClient): static
    {
        $this->idClient->removeElement($idClient);

        return $this;
    }
}
