<?php

namespace App\Entity;

use App\Repository\FilmRepository;
use DateTime;
use DateTimeInterface;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;





#[ORM\Entity(repositoryClass: FilmRepository::class)]
#[ORM\Table(name: 'film')]
class Film
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;

    #[ORM\Column(name: 'nom', type: 'string', length: 255, nullable: false)]
    private string $nom;

    #[ORM\Column(name: 'image', type: 'text', length: 0, nullable: true)]
    private ?string $image = null;

    /**
     * @var DateTime
     */
    #[ORM\Column(name: 'duree', type: 'time', nullable: false)]
    private DateTimeInterface $duree;

    #[ORM\Column(name: 'description', type: 'text', length: 0, nullable: false)]
    private string $description;

    #[ORM\Column(name: 'annederalisation', type: 'integer', nullable: false)]
    private int $annederalisation;

    
    #[ORM\OneToMany(targetEntity: Filmcinema::class, mappedBy: "film")]
    
    private $filmCinemas;

    public function __construct()
    {
        $this->filmCinemas = new ArrayCollection();
    }

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

    public function getDuree(): ?\DateTimeInterface
    {
        return $this->duree;
    }

    public function setDuree(\DateTimeInterface $duree): static
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

     /**
     * @return Collection|Filmcinema[]
     */
    public function getFilmCinemas(): Collection
    {
        return $this->filmCinemas;
    }

    public function addFilmCinema(Filmcinema $filmCinema): self
    {
        if (!$this->filmCinemas->contains($filmCinema)) {
            $this->filmCinemas[] = $filmCinema;
            $filmCinema->setFilm($this);
        }

        return $this;
    }

    public function removeFilmCinema(Filmcinema $filmCinema): self
    {
        if ($this->filmCinemas->removeElement($filmCinema)) {
            // set the owning side to null (unless already changed)
            if ($filmCinema->getFilm() === $this) {
                $filmCinema->setFilm(null);
            }
        }

        return $this;
    }


}