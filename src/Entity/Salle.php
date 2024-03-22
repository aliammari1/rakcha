<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

use App\Repository\SalleRepository;

#[ORM\Entity(repositoryClass: SalleRepository::class)]
class Salle
{
    /**
     * @var int
     */
    #[ORM\Column(name: 'id_salle', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue]
    private int $idSalle;

    /**
     * @var int
     */
    #[ORM\Column(name: 'nb_places', type: 'integer', nullable: false)]
    private int $nbPlaces;

    /**
     * @var string
     */
    #[ORM\Column(name: 'nom_salle', type: 'string', length: 50, nullable: false)]
    private string $nomSalle;

    /**
     * @var Cinema
     */
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
