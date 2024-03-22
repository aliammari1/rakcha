<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

use App\Repository\FilmcinemaRepository;

#[ORM\Entity(repositoryClass: FilmcinemaRepository::class)]
class Filmcinema
{
    /**
     * @var int
     */
    #[ORM\Column(name: 'id_film', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'NONE')]
    private int $idFilm;

    /**
     * @var int
     */
    #[ORM\Column(name: 'id_cinema', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'NONE')]
    private int $idCinema;

    public function getIdFilm(): ?int
    {
        return $this->idFilm;
    }

    public function getIdCinema(): ?int
    {
        return $this->idCinema;
    }
}
