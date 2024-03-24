<?php

namespace App\Entity;

use App\Repository\FilmcomentRepository;
use Doctrine\ORM\Mapping as ORM;



#[ORM\Entity(repositoryClass: FilmcomentRepository::class)]
#[ORM\Table(name: 'filmcoment')]
#[ORM\Index(name: 'pk_comment_film', columns: ['film_id'])]
#[ORM\Index(name: 'pk_comment_user', columns: ['user_id'])]
class Filmcoment
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;

    #[ORM\Column(name: 'comment', type: 'string', length: 255, nullable: false)]
    private string $comment;

    #[ORM\Column(name: 'user_id', type: 'integer', nullable: false)]
    private int $userId;

    #[ORM\Column(name: 'film_id', type: 'integer', nullable: false)]
    private int $filmId;


}
