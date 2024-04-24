<?php

namespace App\Entity;

use App\Repository\AvisRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;



#[ORM\Entity(repositoryClass: AvisRepository::class)]
#[ORM\Table(name: 'avis')]
#[ORM\Index(name: 'FK_clients', columns: ['idusers'])]
#[ORM\Index(name: 'id_produit', columns: ['id_produit'])]
class Avis
{

    #[ORM\Column(name: 'id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;


    #[ORM\Column(name: 'note', type: 'integer', nullable: false)]
    private int $note;


    #[ORM\Column(name: 'avis', type: 'string', length: 255, nullable: true)]
    private ?string $avis = null;

    #[ORM\ManyToOne(targetEntity: Users::class)]
    #[ORM\JoinColumn(name: 'idusers', referencedColumnName: 'id')]
    private ?Users $idusers = null;

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
