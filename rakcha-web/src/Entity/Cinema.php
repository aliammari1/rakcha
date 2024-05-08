<?php

namespace App\Entity;

use App\Repository\CinemaRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;


#[ORM\Entity(repositoryClass: CinemaRepository::class)]
#[ORM\Table(name: 'cinema')]
class Cinema
{

    #[ORM\Column(name: 'id_cinema', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $idCinema;

    #[ORM\Column(name: 'nom', type: 'string', length: 50, nullable: false)]
    #[Assert\NotBlank(message: 'The cinema name is required.')]
    #[Assert\Length(
        min: 2,
        max: 50,
        minMessage: 'The cinema name must be at least {{ limit }} characters long.',
        maxMessage: 'The cinema name cannot exceed {{ limit }} characters.'
    )]
    private string $nom;



    #[ORM\Column(name: 'adresse', type: 'string', length: 100, nullable: false)]
    #[Assert\NotBlank(message: 'The cinema adresse is required.')]
    #[Assert\Length(
        min: 3,
        minMessage: 'The cinema address must be at least {{ limit }} characters long.',
    )]
    private string $adresse;


    #[ORM\Column(name: 'responsable', type: 'integer', nullable: false)]

    private int $responsable;


    #[ORM\Column(name: 'logo', type: 'string', length: 1000, nullable: false)]
    #[Assert\NotBlank(message: 'The cinema logo is required.')]
    #[Assert\File(
        mimeTypes: ["image/jpeg", "image/png", "image/jpg"],
        mimeTypesMessage: "Only JPEG, JPG and PNG images are allowed.",
        maxSize: "5M", // Taille maximale de 5 Mo
        maxSizeMessage: "The file is too large. Maximum allowed size is {{ limit }} {{ suffix }}."
    )]
    private ?string $logo = null;


    #[ORM\Column(name: 'Statut', type: 'string', length: 50, nullable: false)]
    private string $statut;

    /**
     * @var Collection<int, Users>
     */
    #[ORM\JoinTable(name: 'ratingcinema')]
    #[ORM\JoinColumn(name: 'id_cinema', referencedColumnName: 'id_cinema')]
    #[ORM\InverseJoinColumn(name: 'id_user', referencedColumnName: 'id')]
    #[ORM\ManyToMany(targetEntity: Users::class, inversedBy: 'idCinema')]
    private Collection $idUser;


    #[ORM\OneToMany(targetEntity: Salle::class, mappedBy: "cinema")]
    #[ORM\JoinColumn(name: 'cinema_id', referencedColumnName: 'id_cinema', onDelete: 'CASCADE')]
    private $salles;


    #[ORM\ManyToMany(targetEntity: Film::class, mappedBy: 'cinemas')]
    private Collection $films;

    /**
     * Constructor
     */
    public function __construct()
    {
        $this->idUser = new ArrayCollection();
        $this->salles = new ArrayCollection();
        $this->films = new ArrayCollection();
    }



    public function getIdCinema(): ?int
    {
        return $this->idCinema;
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

    public function getAdresse(): ?string
    {
        return $this->adresse;
    }

    public function setAdresse(string $adresse): static
    {
        $this->adresse = $adresse;

        return $this;
    }

    public function getResponsable(): ?int
    {
        return $this->responsable;
    }

    public function setResponsable(int $responsable): static
    {
        $this->responsable = $responsable;

        return $this;
    }

    public function getLogo(): ?string
    {
        return $this->logo;
    }

    public function setLogo(?string $logo): static
    {
        $this->logo = $logo;

        return $this;
    }

    public function getStatut(): ?string
    {
        return $this->statut;
    }

    public function setStatut(string $statut): static
    {
        $this->statut = $statut;

        return $this;
    }

    /**
     * @return Collection<int, Users>
     */
    public function getIdUser(): Collection
    {
        return $this->idUser;
    }

    public function addIdUser(Users $idUser): static
    {
        if (!$this->idUser->contains($idUser)) {
            $this->idUser->add($idUser);
        }

        return $this;
    }

    public function removeIdUser(Users $idUser): static
    {
        $this->idUser->removeElement($idUser);

        return $this;
    }

    /**
     * @return Collection|Salle[]
     */
    public function getSalles(): Collection
    {
        return $this->salles;
    }

    public function addSalle(Salle $salle): self
    {
        if (!$this->salles->contains($salle)) {
            $this->salles[] = $salle;
            $salle->setCinema($this);
        }

        return $this;
    }

    public function removeSalle(Salle $salle): self
    {
        if ($this->salles->removeElement($salle)) {
            // set the owning side to null (unless already changed)
            if ($salle->getCinema() === $this) {
                $salle->setCinema(null);
            }
        }

        return $this;
    }

    /**
     * @return Collection<int, Film>
     */
    public function getFilms(): Collection
    {
        return $this->films;
    }

    public function addFilm(Film $film): static
    {
        if (!$this->films->contains($film)) {
            $this->films->add($film);
            $film->addCinema($this);
        }

        return $this;
    }

    public function removeFilm(Film $film): static
    {
        if ($this->films->removeElement($film)) {
            $film->removeCinema($this);
        }

        return $this;
    }
}
