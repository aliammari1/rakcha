<?php

namespace App\Entity;

use App\Repository\SeriesRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;




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
    private string $nom;


    #[ORM\Column(name: 'resume', type: 'string', length: 50, nullable: false)]
    private string $resume;


    #[ORM\Column(name: 'directeur', type: 'string', length: 50, nullable: false)]
    private string $directeur;


    #[ORM\Column(name: 'pays', type: 'string', length: 50, nullable: false)]
    private string $pays;


    #[ORM\Column(name: 'image', type: 'string', length: 255, nullable: false)]
    private string $image;


    #[ORM\Column(name: 'liked', type: 'integer', nullable: false)]
    private int $liked;


    #[ORM\Column(name: 'nbLikes', type: 'integer', nullable: false)]
    private int $nblikes;


    #[ORM\Column(name: 'disliked', type: 'integer', nullable: false)]
    private int $disliked;


    #[ORM\Column(name: 'nbDislikes', type: 'integer', nullable: false)]
    private int $nbdislikes;


    #[ORM\Column(name: 'idcategorie', type: 'integer', nullable: false)]
    private int $idcategorie;

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

    public function getIdcategorie(): ?int
    {
        return $this->idcategorie;
    }

    public function setIdcategorie(int $idcategorie): static
    {
        $this->idcategorie = $idcategorie;

        return $this;
    }
}
