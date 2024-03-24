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


}
