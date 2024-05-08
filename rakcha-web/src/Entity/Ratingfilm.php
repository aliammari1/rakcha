<?php

namespace App\Entity;

use App\Repository\RatingfilmRepository;
use Doctrine\ORM\Mapping as ORM;


#[ORM\Entity(repositoryClass: RatingfilmRepository::class)]
#[ORM\Table(name: 'ratingfilm')]
#[ORM\Index(name: 'fk_user_ratin', columns: ['id_user'])]
#[ORM\Index(name: 'fk_film_rating', columns: ['id_film'])]
class Ratingfilm
{

    #[ORM\Column(name: 'id_film', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'NONE')]
    private int $idFilm;


    #[ORM\Column(name: 'id_user', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'NONE')]
    private int $idUser;


    #[ORM\Column(name: 'rate', type: 'integer', nullable: true)]
    private ?int $rate = null;

    public function getIdFilm(): ?int
    {
        return $this->idFilm;
    }

    public function getIdUser(): ?int
    {
        return $this->idUser;
    }

    public function getRate(): ?int
    {
        return $this->rate;
    }

    public function setRate(?int $rate): static
    {
        $this->rate = $rate;

        return $this;
    }



    public function setIdFilm(?int $idFilm): static
    {
        $this->idFilm = $idFilm;

        return $this;
    }

    public function setIdUser(?int $idUser): static
    {
        $this->idUser = $idUser;

        return $this;
    }

}
