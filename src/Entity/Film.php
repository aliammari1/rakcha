<?php

namespace App\Entity;

use App\Repository\FilmRepository;
use DateTime;
use DateTimeInterface;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: FilmRepository::class)]
#[ORM\Table(name: 'film')]
class Film
{

    #[ORM\Column(name: 'id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;

    #[Assert\NotBlank(message: 'The film name is required.')]
    #[Assert\Length(max: 255, maxMessage: 'The film name cannot exceed {{ limit }} characters.')]
    #[Assert\Regex(
        pattern: "/^[A-Z][a-zA-Z0-9\s]*$/",
        message: 'The film name must start with an uppercase letter .'
    )]
    #[ORM\Column(name: 'nom', type: 'string', length: 255, nullable: false)]
    private string $nom;


    #[ORM\Column(name: 'image', type: 'text', length: 0, nullable: true)]
    private ?string $image = null;

    /**
     * @var DateTime
     */


    #[Assert\NotBlank(message: 'The film duration is required.')]
    // #[Assert\Expression(
    //     "value instanceof DateTimeInterface && value->format('H:i:s') !== '00:00:00'",
    //     message: 'The film duration must be a valid time.'
    // )]
    #[Assert\Range(
        min: 'first day of January -54 years + 30 minutes',
        max: 'first day of January -54 years +4 hours',
        notInRangeMessage: 'You must be between 30 minutes and 4 hours Time to add this film duration  ',
    )]
    #[ORM\Column(name: 'duree', type: 'time', nullable: false)]
    private DateTimeInterface $duree;

    #[Assert\NotBlank(message: 'The film description is required.')]
    #[Assert\Length(max: 1000, maxMessage: 'The film description cannot exceed {{ limit }} characters.')]
    #[ORM\Column(name: 'description', type: 'text', length: 0, nullable: false)]
    private string $description;

    #[Assert\NotBlank(message: 'The film release year is required.')]
    #[Assert\Range(
        min: 1800,
        max: 2024,
        notInRangeMessage: 'The film release year must be between {{ min }} and {{ max }}.'
    )]
    #[ORM\Column(name: 'annederalisation', type: 'integer', nullable: false)]
    private int $annederalisation;

    #[ORM\Column(name: 'isBookmarked', type: 'boolean')]
    private bool $isBookmarked;

    #[ORM\ManyToMany(targetEntity: Actor::class, inversedBy: 'films')]
    #[Assert\NotBlank(message: 'The film actors is required.')]
    #[Assert\Count(min: 1, minMessage: 'The film must have at least one actor.')]
    private Collection $actors;

    #[ORM\ManyToMany(targetEntity: Category::class, inversedBy: 'films')]
    #[Assert\NotBlank(message: 'The film category is required.')]
    #[Assert\Count(min: 1, minMessage: 'The film must have at least one category.')]
    private Collection $categorys;

    #[ORM\OneToMany(targetEntity: Filmcinema::class, mappedBy: "film")]
    
    private $filmCinemas;
    public function __construct()
    {
        $this->filmCinemas = new ArrayCollection();
        $this->actors = new ArrayCollection();
        $this->categorys = new ArrayCollection();
        $this->duree = new DateTime();
    }


    public function getId(): ?int
    {
        return $this->id;
    }
    public function getIsBookmarked(): ?bool
    {
        return $this->isBookmarked;
    }

    
    public function setIsBookmarked(bool $isBookmarked): static
    {
        $this->isBookmarked = $isBookmarked;

        return $this;
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
     * @return Collection<int, Actor>
     */
    public function getActors(): Collection
    {
        return $this->actors;
    }

    public function addActor(Actor $actor): static
    {
        if (!$this->actors->contains($actor)) {
            $this->actors->add($actor);
        }

        return $this;
    }

    public function removeActor(Actor $actor): static
    {
        $this->actors->removeElement($actor);

        return $this;
    }

    /**
     * @return Collection<int, Category>
     */
    public function getCategorys(): Collection
    {
        return $this->categorys;
    }

    public function addCategory(Category $category): static
    {
        if (!$this->categorys->contains($category)) {
            $this->categorys->add($category);
        }

        return $this;
    }

    public function removeCategory(Category $category): static
    {
        $this->categorys->removeElement($category);

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
