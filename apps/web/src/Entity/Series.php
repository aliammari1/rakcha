<?php

namespace App\Entity;

use App\Repository\SeriesRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;


#[ORM\Entity(repositoryClass: SeriesRepository::class)]
#[ORM\Table(name: 'series')]
#[ORM\Index(name: 'idcategorie', columns: ['idcategorie'])]
class Series
{
    #[ORM\Column(name: 'idserie', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $idserie;

    #[ORM\Column(name: 'nom', type: 'string', length: 30, nullable: false)]
    #[Assert\NotBlank(message: 'The name cannot be blank')]
    #[Assert\NotNull(message: 'The name cannot be null')]
    #[Assert\Length(min: 2, max: 50, minMessage: 'The name must have at least {{ limit }} characters', maxMessage: 'The surname cannot exceed {{ limit }} characters')]
    #[Assert\Type(type: 'string', message: 'The name must be a string')]
    private string $nom;

    #[ORM\Column(name: 'resume', type: 'string', length: 50, nullable: false)]
    #[Assert\NotBlank(message: 'The summary cannot be blank')]
    #[Assert\NotNull(message: 'The summary cannot be null')]
    #[Assert\Length(min: 2, max: 50, minMessage: 'The summary must have at least {{ limit }} characters', maxMessage: 'The summary cannot exceed {{ limit }} characters')]
    #[Assert\Type(type: 'string', message: 'The summary must be a string')]
    private string $resume;

    #[ORM\Column(name: 'directeur', type: 'string', length: 50, nullable: false)]
    #[Assert\NotBlank(message: 'The director cannot be blank')]
    #[Assert\NotNull(message: 'The director cannot be null')]
    #[Assert\Length(min: 2, max: 50, minMessage: 'The director must have at least {{ limit }} characters', maxMessage: 'The director cannot exceed {{ limit }} characters')]
    #[Assert\Type(type: 'string', message: 'The director must be a string')]
    private string $directeur;

    #[ORM\Column(name: 'pays', type: 'string', length: 50, nullable: false)]
    #[Assert\NotBlank(message: 'The country cannot be blank')]
    #[Assert\NotNull(message: 'The country cannot be null')]
    #[Assert\Length(min: 2, max: 50, minMessage: 'The country must have at least {{ limit }} characters', maxMessage: 'The country cannot exceed {{ limit }} characters')]
    #[Assert\Type(type: 'string', message: 'The country must be a string')]
    private string $pays;

    #[ORM\Column(name: 'image', type: 'string', length: 255, nullable: false)]
    private string $image;

    #[ORM\Column(name: 'liked', type: 'integer', nullable: true)]
    private ?int $liked = 0; // Initialiser à zéro

    #[ORM\Column(name: 'nbLikes', type: 'integer', nullable: true)]
    private ?int $nblikes = 0; // Initialiser à zéro

    #[ORM\Column(name: 'disliked', type: 'integer', nullable: true)]
    private ?int $disliked = 0; // Initialiser à zéro

    #[ORM\Column(name: 'nbDislikes', type: 'integer', nullable: true)]
    private ?int $nbdislikes = 0; // Initialiser à zéro


    #[ORM\ManyToOne(targetEntity: Categories::class)]
    #[ORM\JoinColumn(name: 'idcategorie', referencedColumnName: 'idcategorie')]
    private ?Categories $idcategorie = null;

    public function getIdserie(): ?int
    {
        return $this->idserie;
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

    public function getResume(): ?string
    {
        return $this->resume;
    }

    public function setResume(string $resume): static
    {
        $this->resume = $resume;

        return $this;
    }

    public function getDirecteur(): ?string
    {
        return $this->directeur;
    }

    public function setDirecteur(string $directeur): static
    {
        $this->directeur = $directeur;

        return $this;
    }

    public function getPays(): ?string
    {
        return $this->pays;
    }

    public function setPays(string $pays): static
    {
        $this->pays = $pays;

        return $this;
    }

    public function getImage(): ?string
    {
        return $this->image;
    }

    public function setImage(string $image): static
    {
        $this->image = $image;

        return $this;
    }

    public function getLiked(): ?int
    {
        return $this->liked;
    }

    public function setLiked(int $liked): static
    {
        $this->liked = $liked;

        return $this;
    }

    public function getNblikes(): ?int
    {
        return $this->nblikes;
    }

    public function setNblikes(int $nblikes): static
    {
        $this->nblikes = $nblikes;

        return $this;
    }

    public function getDisliked(): ?int
    {
        return $this->disliked;
    }

    public function setDisliked(int $disliked): static
    {
        $this->disliked = $disliked;

        return $this;
    }

    public function getNbdislikes(): ?int
    {
        return $this->nbdislikes;
    }

    public function setNbdislikes(int $nbdislikes): static
    {
        $this->nbdislikes = $nbdislikes;

        return $this;
    }

    public function getIdcategorie(): ?Categories
    {
        return $this->idcategorie;
    }

    public function setIdcategorie(?Categories $idcategorie): static
    {
        $this->idcategorie = $idcategorie;

        return $this;
    }


}