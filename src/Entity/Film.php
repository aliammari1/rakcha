<?php

namespace App\Entity;

use App\Repository\FilmRepository;
use DateTime;
use DateTimeInterface;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
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
        min: '00:30:00',
        max: '03:00:00',
        minMessage: 'The film duration must be at least 30 minutes.',
        maxMessage: 'The film duration cannot exceed 3 hours.'
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

    #[ORM\ManyToMany(targetEntity: Actor::class, inversedBy: 'films')]
    private Collection $actors;

    #[ORM\ManyToMany(targetEntity: Category::class, inversedBy: 'films')]
    private Collection $categorys;

    public function __construct()
    {
        $this->actors = new ArrayCollection();
        $this->categorys = new ArrayCollection();
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


}