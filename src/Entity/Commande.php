<?php

namespace App\Entity;

use DateTime;
use DateTimeInterface;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

use App\Repository\CommandeRepository;

#[ORM\Entity(repositoryClass: CommandeRepository::class)]
class Commande
{
    /**
     * @var int
     */
    #[ORM\Column(name: 'idCommande', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue]
    private int $idcommande;

    /**
     * @var DateTime
     */
    #[ORM\Column(name: 'dateCommande', type: 'date', nullable: false)]
    private DateTimeInterface $datecommande;

    /**
     * @var string
     */
    #[ORM\Column(name: 'statu', type: 'string', length: 50, nullable: false, options: ['default' => 'En cours'])]
    private string $statu = 'En cours';

    /**
     * @var int
     */
    #[ORM\Column(name: 'num_telephone', type: 'integer', nullable: false)]
    private int $numTelephone;

    /**
     * @var string
     */
    #[ORM\Column(name: 'adresse', type: 'string', length: 50, nullable: false)]
    private string $adresse;

    /**
     * @var Users
     */
    #[ORM\ManyToOne(targetEntity: Users::class)]
    #[ORM\JoinColumn(name: 'idClient', referencedColumnName: 'id')]
    private ?Users $idclient = null;

    public function getIdcommande(): ?int
    {
        return $this->idcommande;
    }

    public function getDatecommande(): ?DateTimeInterface
    {
        return $this->datecommande;
    }

    public function setDatecommande(DateTimeInterface $datecommande): static
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
