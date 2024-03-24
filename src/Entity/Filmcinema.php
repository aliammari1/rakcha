<?php

namespace App\Entity;

use App\Repository\FilmcinemaRepository;
use Doctrine\ORM\Mapping as ORM;



#[ORM\Entity(repositoryClass: FilmcinemaRepository::class)]
#[ORM\Table(name: 'filmcinema')]
#[ORM\Index(name: 'fk_fc1', columns: ['id_film'])]
#[ORM\Index(name: 'fk_fc2', columns: ['id_cinema'])]
class Filmcinema
{
    #[ORM\Column(name: 'id_film', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'NONE')]
    private int $idFilm;

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
