<?php

namespace App\Entity;

use App\Repository\SalleRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;



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
    private int $nbPlaces;


    #[ORM\Column(name: 'nom_salle', type: 'string', length: 50, nullable: false)]
    private string $nomSalle;

    #[ORM\ManyToOne(targetEntity: Cinema::class)]
    #[ORM\JoinColumn(name: 'id_cinema', referencedColumnName: 'id_cinema')]
    private ?Cinema $idCinema = null;

    public function getIdSalle(): ?int
    {
        return $this->idSalle;
    }

    public function getNbPlaces(): ?int
    {
        return $this->nbPlaces;
    }

    public function setNbPlaces(int $nbPlaces): static
    {
        $this->nbPlaces = $nbPlaces;

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

    public function getIdCinema(): ?Cinema
    {
        return $this->idCinema;
    }

    public function setIdCinema(?Cinema $idCinema): static
    {
        $this->idCinema = $idCinema;

        return $this;
    }
}
