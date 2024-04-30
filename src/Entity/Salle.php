<?php

namespace App\Entity;

use App\Repository\SalleRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;
use Symfony\Component\Validator\Constraints as Assert ;




#[ORM\Entity(repositoryClass: SalleRepository::class)]
#[ORM\Table(name: 'salle')]
#[ORM\Index(name: 'fk_cinema_salle', columns: ['id_cinema'])]
class Salle
{

    #[ORM\Column(name: 'id_salle', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $idSalle;


    #[ORM\Column(name: 'nb_places', type: 'integer', nullable: false)]
    #[Assert\NotBlank(message: 'The number of seats is required.')]
    #[Assert\Positive(message: 'The number of seats must be a positive integer.')]
    private int $nbPlaces;


    #[ORM\Column(name: 'nom_salle', type: 'string', length: 50, nullable: false)]
    #[Assert\NotBlank(message: 'The room name is required.')]
    #[Assert\Length(
        min: 5,
        max: 10,
        minMessage: 'The room name must be at least {{ limit }} characters long.',
        maxMessage: 'The room name cannot exceed {{ limit }} characters.'
    )]      private string $nomSalle;

    #[ORM\Column(name: 'id_cinema', type: 'integer',  nullable: false)]
    private int $idCinema;

  

    
    #[ORM\ManyToOne(targetEntity: Cinema::class, inversedBy: "salles")]
    #[ORM\JoinColumn(name: "id_cinema", referencedColumnName: "id_cinema" , nullable: false)]
     
    private $cinema;

  
  

    public function getIdSalle(): ?int
    {
        return $this->idSalle;
    }

    public function getNbPlaces(): ?int
    {
        return $this->nbPlaces;
    }

    public function getIdCinema(): ?int
    {
        return $this->idCinema;
    }

    public function setNbPlaces(int $nbPlaces): static
    {
        $this->nbPlaces = $nbPlaces;

        return $this;
    }

    public function setIdCinema(int $idCinema): static
    {
        $this->idCinema = $idCinema;

        return $this;
    }

    public function getNomSalle(): ?string
    {
        return $this->nomSalle;
    }

    public function setNomSalle(string $nomSalle): static
    {
        $this->nomSalle = $nomSalle;

        return $this;
    }


    public function getCinema(): ?Cinema
    {
        return $this->cinema;
    }

    public function setCinema(?Cinema $cinema): self
    {
        $this->cinema = $cinema;

        return $this;
    }
}

