<?php

namespace App\Entity;

use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

/**
 * Evenement
 *
 * @ORM\Table(name="evenement", indexes={@ORM\Index(name="clé secondaire", columns={"id_categorie"})})
 * @ORM\Entity
 */
class Evenement
{
    /**
     * @var int
     *
     * @ORM\Column(name="ID", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $id;

    /**
     * @var string
     *
     * @ORM\Column(name="nom", type="string", length=500, nullable=false)
     */
    private $nom;

    /**
     * @var \DateTime
     *
     * @ORM\Column(name="dateDebut", type="date", nullable=false)
     */
    private $datedebut;

    /**
     * @var \DateTime
     *
     * @ORM\Column(name="dateFin", type="date", nullable=false)
     */
    private $datefin;

    /**
     * @var string
     *
     * @ORM\Column(name="lieu", type="string", length=500, nullable=false)
     */
    private $lieu;

    /**
     * @var string
     *
     * @ORM\Column(name="etat", type="string", length=500, nullable=false)
     */
    private $etat;

    /**
     * @var string
     *
     * @ORM\Column(name="description", type="string", length=500, nullable=false)
     */
    private $description;

    /**
     * @var string
     *
     * @ORM\Column(name="affiche_event", type="blob", length=65535, nullable=false)
     */
    private $afficheEvent;

    /**
     * @var \CategorieEvenement
     *
     * @ORM\ManyToOne(targetEntity="CategorieEvenement")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="id_categorie", referencedColumnName="ID")
     * })
     */
    private $idCategorie;

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

    public function getDatedebut(): ?\DateTimeInterface
    {
        return $this->datedebut;
    }

    public function setDatedebut(\DateTimeInterface $datedebut): static
    {
        $this->datedebut = $datedebut;

        return $this;
    }

    public function getDatefin(): ?\DateTimeInterface
    {
        return $this->datefin;
    }

    public function setDatefin(\DateTimeInterface $datefin): static
    {
        $this->datefin = $datefin;

        return $this;
    }

    public function getLieu(): ?string
    {
        return $this->lieu;
    }

    public function setLieu(string $lieu): static
    {
        $this->lieu = $lieu;

        return $this;
    }

    public function getEtat(): ?string
    {
        return $this->etat;
    }

    public function setEtat(string $etat): static
    {
        $this->etat = $etat;

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

    public function getAfficheEvent()
    {
        return $this->afficheEvent;
    }

    public function setAfficheEvent($afficheEvent): static
    {
        $this->afficheEvent = $afficheEvent;

        return $this;
    }

    public function getIdCategorie(): ?CategorieEvenement
    {
        return $this->idCategorie;
    }

    public function setIdCategorie(?CategorieEvenement $idCategorie): static
    {
        $this->idCategorie = $idCategorie;

        return $this;
    }


}
