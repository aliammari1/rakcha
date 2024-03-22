<?php

namespace App\Entity;

use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

/**
 * Users
 *
 * @ORM\Table(name="users")
 * @ORM\Entity
 */
class Users
{
    /**
     * @var int
     *
     * @ORM\Column(name="id", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $id;

    /**
     * @var string
     *
     * @ORM\Column(name="nom", type="string", length=50, nullable=false)
     */
    private $nom;

    /**
     * @var string
     *
     * @ORM\Column(name="prenom", type="string", length=50, nullable=false)
     */
    private $prenom;

    /**
     * @var int
     *
     * @ORM\Column(name="num_telephone", type="integer", nullable=false)
     */
    private $numTelephone;

    /**
     * @var string
     *
     * @ORM\Column(name="password", type="string", length=50, nullable=false)
     */
    private $password;

    /**
     * @var string
     *
     * @ORM\Column(name="role", type="string", length=50, nullable=false)
     */
    private $role;

    /**
     * @var string|null
     *
     * @ORM\Column(name="adresse", type="string", length=50, nullable=true)
     */
    private $adresse;

    /**
     * @var \DateTime|null
     *
     * @ORM\Column(name="date_de_naissance", type="date", nullable=true)
     */
    private $dateDeNaissance;

    /**
     * @var string
     *
     * @ORM\Column(name="email", type="string", length=50, nullable=false)
     */
    private $email;

    /**
     * @var string|null
     *
     * @ORM\Column(name="photo_de_profil", type="blob", length=65535, nullable=true)
     */
    private $photoDeProfil;

    /**
     * @var \Doctrine\Common\Collections\Collection
     *
     * @ORM\ManyToMany(targetEntity="Cinema", mappedBy="idUser")
     */
    private $idCinema = array();

    /**
     * @var \Doctrine\Common\Collections\Collection
     *
     * @ORM\ManyToMany(targetEntity="Seance", inversedBy="idUser")
     * @ORM\JoinTable(name="ticket",
     *   joinColumns={
     *     @ORM\JoinColumn(name="id_user", referencedColumnName="id")
     *   },
     *   inverseJoinColumns={
     *     @ORM\JoinColumn(name="id_seance", referencedColumnName="id_seance")
     *   }
     * )
     */
    private $idSeance = array();

    /**
     * @var \Doctrine\Common\Collections\Collection
     *
     * @ORM\ManyToMany(targetEntity="Produit", mappedBy="idClient")
     */
    private $idProduit = array();

    /**
     * Constructor
     */
    public function __construct()
    {
        $this->idCinema = new \Doctrine\Common\Collections\ArrayCollection();
        $this->idSeance = new \Doctrine\Common\Collections\ArrayCollection();
        $this->idProduit = new \Doctrine\Common\Collections\ArrayCollection();
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

    public function getPrenom(): ?string
    {
        return $this->prenom;
    }

    public function setPrenom(string $prenom): static
    {
        $this->prenom = $prenom;

        return $this;
    }

    public function getNumTelephone(): ?int
    {
        return $this->numTelephone;
    }

    public function setNumTelephone(int $numTelephone): static
    {
        $this->numTelephone = $numTelephone;

        return $this;
    }

    public function getPassword(): ?string
    {
        return $this->password;
    }

    public function setPassword(string $password): static
    {
        $this->password = $password;

        return $this;
    }

    public function getRole(): ?string
    {
        return $this->role;
    }

    public function setRole(string $role): static
    {
        $this->role = $role;

        return $this;
    }

    public function getAdresse(): ?string
    {
        return $this->adresse;
    }

    public function setAdresse(?string $adresse): static
    {
        $this->adresse = $adresse;

        return $this;
    }

    public function getDateDeNaissance(): ?\DateTimeInterface
    {
        return $this->dateDeNaissance;
    }

    public function setDateDeNaissance(?\DateTimeInterface $dateDeNaissance): static
    {
        $this->dateDeNaissance = $dateDeNaissance;

        return $this;
    }

    public function getEmail(): ?string
    {
        return $this->email;
    }

    public function setEmail(string $email): static
    {
        $this->email = $email;

        return $this;
    }

    public function getPhotoDeProfil()
    {
        return $this->photoDeProfil;
    }

    public function setPhotoDeProfil($photoDeProfil): static
    {
        $this->photoDeProfil = $photoDeProfil;

        return $this;
    }

    /**
     * @return Collection<int, Cinema>
     */
    public function getIdCinema(): Collection
    {
        return $this->idCinema;
    }

    public function addIdCinema(Cinema $idCinema): static
    {
        if (!$this->idCinema->contains($idCinema)) {
            $this->idCinema->add($idCinema);
            $idCinema->addIdUser($this);
        }

        return $this;
    }

    public function removeIdCinema(Cinema $idCinema): static
    {
        if ($this->idCinema->removeElement($idCinema)) {
            $idCinema->removeIdUser($this);
        }

        return $this;
    }

    /**
     * @return Collection<int, Seance>
     */
    public function getIdSeance(): Collection
    {
        return $this->idSeance;
    }

    public function addIdSeance(Seance $idSeance): static
    {
        if (!$this->idSeance->contains($idSeance)) {
            $this->idSeance->add($idSeance);
        }

        return $this;
    }

    public function removeIdSeance(Seance $idSeance): static
    {
        $this->idSeance->removeElement($idSeance);

        return $this;
    }

    /**
     * @return Collection<int, Produit>
     */
    public function getIdProduit(): Collection
    {
        return $this->idProduit;
    }

    public function addIdProduit(Produit $idProduit): static
    {
        if (!$this->idProduit->contains($idProduit)) {
            $this->idProduit->add($idProduit);
            $idProduit->addIdClient($this);
        }

        return $this;
    }

    public function removeIdProduit(Produit $idProduit): static
    {
        if ($this->idProduit->removeElement($idProduit)) {
            $idProduit->removeIdClient($this);
        }

        return $this;
    }

}
