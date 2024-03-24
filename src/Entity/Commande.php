<?php

namespace App\Entity;

use App\Repository\CommandeRepository;
use DateTime;
use DateTimeInterface;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;


#[ORM\Entity(repositoryClass: CommandeRepository::class)]
#[ORM\Table(name: 'commande')]
#[ORM\Index(name: 'fk_idClient', columns: ['idClient'])]
class Commande
{
    #[ORM\Column(name: 'idCommande', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $idcommande;

    /**
     * @var DateTime
     */
    #[ORM\Column(name: 'dateCommande', type: 'date', nullable: false)]
    private DateTimeInterface $datecommande;

    #[ORM\Column(name: 'statu', type: 'string', length: 50, nullable: false, options: ['default' => 'En cours'])]
    private string $statu = 'En cours';

    #[ORM\Column(name: 'num_telephone', type: 'integer', nullable: false)]
    private int $numTelephone;

    #[ORM\Column(name: 'adresse', type: 'string', length: 50, nullable: false)]
    private string $adresse;

    #[ORM\ManyToOne(targetEntity: Users::class)]
    #[ORM\JoinColumn(name: 'idClient', referencedColumnName: 'id')]
    private ?Users $idclient = null;

    public function getIdcommande(): ?int
    {
        return $this->idcommande;
    }

    public function getDatecommande(): ?\DateTimeInterface
    {
        return $this->datecommande;
    }

    public function setDatecommande(\DateTimeInterface $datecommande): static
    {
        $this->datecommande = $datecommande;

        return $this;
    }

    public function getStatu(): ?string
    {
        return $this->statu;
    }

    public function setStatu(string $statu): static
    {
        $this->statu = $statu;

        return $this;
    }

    public function getNumTelephone(): ?int
    {
        return $this->numTelephone;
    }

    public function setNumTelephone(int $numTelephone): static
    {
        $this->numTelephone = $numTelephone;

        return $this;
    }

    public function getAdresse(): ?string
    {
        return $this->adresse;
    }

    public function setAdresse(string $adresse): static
    {
        $this->adresse = $adresse;

        return $this;
    }

    public function getIdclient(): ?Users
    {
        return $this->idclient;
    }

    public function setIdclient(?Users $idclient): static
    {
        $this->idclient = $idclient;

        return $this;
    }


}
