<?php

namespace App\Entity;

use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

/**
 * Cinema
 *
 * @ORM\Table(name="cinema")
 * @ORM\Entity
 */
class Cinema
{
    /**
     * @var int
     *
     * @ORM\Column(name="id_cinema", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $idCinema;

    /**
     * @var string
     *
     * @ORM\Column(name="nom", type="string", length=50, nullable=false)
     */
    private $nom;

    /**
     * @var string
     *
     * @ORM\Column(name="adresse", type="string", length=100, nullable=false)
     */
    private $adresse;

    /**
     * @var int
     *
     * @ORM\Column(name="responsable", type="integer", nullable=false)
     */
    private $responsable;

    /**
     * @var string|null
     *
     * @ORM\Column(name="logo", type="text", length=0, nullable=true)
     */
    private $logo;

    /**
     * @var string
     *
     * @ORM\Column(name="Statut", type="string", length=50, nullable=false)
     */
    private $statut;

    /**
     * @var \Doctrine\Common\Collections\Collection
     *
     * @ORM\ManyToMany(targetEntity="Users", inversedBy="idCinema")
     * @ORM\JoinTable(name="ratingcinema",
     *   joinColumns={
     *     @ORM\JoinColumn(name="id_cinema", referencedColumnName="id_cinema")
     *   },
     *   inverseJoinColumns={
     *     @ORM\JoinColumn(name="id_user", referencedColumnName="id")
     *   }
     * )
     */
    private $idUser = array();

    /**
     * Constructor
     */
    public function __construct()
    {
        $this->idUser = new \Doctrine\Common\Collections\ArrayCollection();
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

}
