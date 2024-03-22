<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Series
 *
 * @ORM\Table(name="series", indexes={@ORM\Index(name="idcategorie", columns={"idcategorie"})})
 * @ORM\Entity
 */
class Series
{
    /**
     * @var int
     *
     * @ORM\Column(name="idserie", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $idserie;

    /**
     * @var string
     *
     * @ORM\Column(name="nom", type="string", length=30, nullable=false)
     */
    private $nom;

    /**
     * @var string
     *
     * @ORM\Column(name="resume", type="string", length=50, nullable=false)
     */
    private $resume;

    /**
     * @var string
     *
     * @ORM\Column(name="directeur", type="string", length=50, nullable=false)
     */
    private $directeur;

    /**
     * @var string
     *
     * @ORM\Column(name="pays", type="string", length=50, nullable=false)
     */
    private $pays;

    /**
     * @var string
     *
     * @ORM\Column(name="image", type="string", length=255, nullable=false)
     */
    private $image;

    /**
     * @var int
     *
     * @ORM\Column(name="liked", type="integer", nullable=false)
     */
    private $liked;

    /**
     * @var int
     *
     * @ORM\Column(name="nbLikes", type="integer", nullable=false)
     */
    private $nblikes;

    /**
     * @var int
     *
     * @ORM\Column(name="disliked", type="integer", nullable=false)
     */
    private $disliked;

    /**
     * @var int
     *
     * @ORM\Column(name="nbDislikes", type="integer", nullable=false)
     */
    private $nbdislikes;

    /**
     * @var int
     *
     * @ORM\Column(name="idcategorie", type="integer", nullable=false)
     */
    private $idcategorie;

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
