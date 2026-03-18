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

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getComment(): ?string
    {
        return $this->comment;
    }

    public function setComment(string $comment): static
    {
        $this->comment = $comment;

        return $this;
    }

    public function getUserId(): ?int
    {
        return $this->userId;
    }

    public function setUserId(int $userId): static
    {
        $this->userId = $userId;

        return $this;
    }

    public function getFilmId(): ?int
    {
        return $this->filmId;
    }

    public function setFilmId(int $filmId): static
    {
        $this->filmId = $filmId;

        return $this;
    }
}
