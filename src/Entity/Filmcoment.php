<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

use App\Repository\FilmcomentRepository;

#[ORM\Entity(repositoryClass: FilmcomentRepository::class)]
class Filmcoment
{
    /**
     * @var int
     */
    #[ORM\Column(name: 'id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue]
    private int $id;

    /**
     * @var string
     */
    #[ORM\Column(name: 'comment', type: 'string', length: 255, nullable: false)]
    private string $comment;

    /**
     * @var int
     */
    #[ORM\Column(name: 'user_id', type: 'integer', nullable: false)]
    private int $userId;

    /**
     * @var int
     */
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
