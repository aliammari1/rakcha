<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

use App\Repository\AvisRepository;

#[ORM\Entity(repositoryClass: AvisRepository::class)]
class Avis
{
    /**
     * @var int
     */
    #[ORM\Column(name: 'id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue]
    private int $id;

    /**
     * @var int
     */
    #[ORM\Column(name: 'note', type: 'integer', nullable: false)]
    private int $note;

    /**
     * @var string|null
     */
    #[ORM\Column(name: 'avis', type: 'string', length: 255, nullable: true)]
    private ?string $avis = null;

    /**
     * @var Users
     */
    #[ORM\ManyToOne(targetEntity: Users::class)]
    #[ORM\JoinColumn(name: 'idusers', referencedColumnName: 'id')]
    private ?Users $idusers = null;

    /**
     * @var Produit
     */
    #[ORM\ManyToOne(targetEntity: Produit::class)]
    #[ORM\JoinColumn(name: 'id_produit', referencedColumnName: 'id_produit')]
    private ?Produit $idProduit = null;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getNote(): ?int
    {
        return $this->note;
    }

    public function setNote(int $note): static
    {
        $this->note = $note;

        return $this;
    }

    public function getAvis(): ?string
    {
        return $this->avis;
    }

    public function setAvis(?string $avis): static
    {
        $this->avis = $avis;

        return $this;
    }

    public function getIdusers(): ?Users
    {
        return $this->idusers;
    }

    public function setIdusers(?Users $idusers): static
    {
        $this->idusers = $idusers;

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
