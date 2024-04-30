<?php

namespace App\Entity;

use App\Repository\FilmcinemaRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;




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

     
    #[ORM\ManyToOne(targetEntity: Cinema::class, inversedBy: "filmCinemas")]
    #[ORM\JoinColumn(name: 'id_cinema', referencedColumnName: 'id_cinema', nullable: false)]
    private $cinema;

    #[ORM\ManyToOne(targetEntity: Film::class, inversedBy: "filmCinemas")]
    #[ORM\JoinColumn(name: 'id_film', referencedColumnName: 'id', nullable: false)] 
    private $film;

    public function getIdFilm(): ?int
    {
        return $this->idFilm;
    }

    public function getIdCinema(): ?int
    {
        return $this->idCinema;
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

    public function getFilm(): ?Film
    {
        return $this->film;
    }

    public function setFilm(?Film $film): self
    {
        $this->film = $film;

        return $this;
    }


}
