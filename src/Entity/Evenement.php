<?php

namespace App\Entity;

use App\Repository\EvenementRepository;
use DateTime;
use DateTimeInterface;
use Doctrine\ORM\Mapping as ORM;


#[ORM\Entity(repositoryClass: EvenementRepository::class)]
#[ORM\Table(name: 'evenement')]
#[ORM\Index(name: 'cle_secondaire', columns: ['id_categorie'])]
class Evenement
{

    #[ORM\Column(name: 'ID', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;



    #[ORM\Column(name: 'nom', type: 'string', length: 50, nullable: false)]
    #[Assert\NotBlank(message: "Event's name is required.")]
    #[Assert\Length(
        min: 2,
        max: 50,
        minMessage: "The event's name must be at least {{ limit }} characters long.",
        maxMessage: "The event's name cannot exceed {{ limit }} characters."
    )]
    private string $nom;


    /**
     * @var DateTime
     */

    #[ORM\Column(name: 'dateDebut', type: 'date', nullable: false)]
    private DateTimeInterface $datedebut;

    /**
     * @var DateTime
     */

    #[ORM\Column(name: 'dateFin', type: 'date', nullable: false)]
    private DateTimeInterface $datefin;


    #[ORM\Column(name: 'lieu', type: 'string', length: 50, nullable: false)]
    #[Assert\NotBlank(message: 'Location is required.')]
    #[Assert\Length(
        min: 2,
        max: 50,
        minMessage: "The event's location must be at least {{ limit }} characters long.",
        maxMessage: "The event's location cannot exceed {{ limit }} characters."
    )]
    private string $lieu;


    #[ORM\Column(name: 'etat', type: 'string', length: 50, nullable: false)]
    #[Assert\NotBlank(message: 'Events status is required.')]
    #[Assert\Length(
        min: 2,
        max: 50,
        minMessage: "The event's status must be at least {{ limit }} characters long.",
        maxMessage: "The event's status cannot exceed {{ limit }} characters."
    )]
    private string $etat;

    #[ORM\Column(name: 'description', type: 'string', length: 500, nullable: false)]
    #[Assert\NotBlank(message: "Event's description is required.")]
    #[Assert\Length(
        min: 2,
        max: 500,
        minMessage: "The event's description must be at least {{ limit }} characters long.",
        maxMessage: "The event's description cannot exceed {{ limit }} characters."
    )]
    private string $description;    


    #[ORM\Column(name: 'affiche_event', type: 'string', length: 500, nullable: false)]
    private string $afficheEvent;

    #[ORM\ManyToOne(targetEntity: CategorieEvenement::class)]
    #[ORM\JoinColumn(name: 'id_categorie', referencedColumnName: 'ID')]
    private ?CategorieEvenement $idCategorie = null;

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

    public function getAfficheEvent(): ?string
    {
        return $this->afficheEvent;
    }

    public function setAfficheEvent(string $afficheEvent): static
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
