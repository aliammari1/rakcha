<?php

namespace App\Entity;

use DateTime;
use DateTimeInterface;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

use App\Repository\FilmRepository;

#[ORM\Entity(repositoryClass: FilmRepository::class)]
class Film
{
    /**
     * @var int
     */
    #[ORM\Column(name: 'id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue]
    private int $id;

    /**
     * @var string
     */
    #[ORM\Column(name: 'nom', type: 'string', length: 255, nullable: false)]
    private string $nom;

    /**
     * @var string|null
     */
    #[ORM\Column(name: 'image', type: 'text', length: 0, nullable: true)]
    private ?string $image = null;

    /**
     * @var DateTime
     */
    #[ORM\Column(name: 'duree', type: 'time', nullable: false)]
    private DateTimeInterface $duree;

    /**
     * @var string
     */
    #[ORM\Column(name: 'description', type: 'text', length: 0, nullable: false)]
    private string $description;

    /**
     * @var int
     */
    #[ORM\Column(name: 'annederalisation', type: 'integer', nullable: false)]
    private int $annederalisation;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getNom(): ?string
    {
        return $this->nom;
    }

    public function setNom(string $nom): static
    {
        $this->nom = $nom;

        return $this;
    }

    public function getImage(): ?string
    {
        return $this->image;
    }

    public function setImage(?string $image): static
    {
        $this->image = $image;

        return $this;
    }

    public function getDuree(): ?DateTimeInterface
    {
        return $this->duree;
    }

    public function setDuree(DateTimeInterface $duree): static
    {
        $this->duree = $duree;

        return $this;
    }

    public function getDescription(): ?string
    {
        return $this->description;
    }

    public function setDescription(string $description): static
    {
        $this->description = $description;

        return $this;
    }

    public function getAnnederalisation(): ?int
    {
        return $this->annederalisation;
    }

    public function setAnnederalisation(int $annederalisation): static
    {
        $this->annederalisation = $annederalisation;

        return $this;
    }
}
