<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

use App\Repository\RatingfilmRepository;

#[ORM\Entity(repositoryClass: RatingfilmRepository::class)]
class Ratingfilm
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
    #[ORM\Column(name: 'id_user', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'NONE')]
    private int $idUser;

    /**
     * @var int|null
     */
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
}
