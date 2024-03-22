<?php

namespace App\Entity;

use DateTime;
use DateTimeInterface;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

use App\Repository\FeedbackRepository;

#[ORM\Entity(repositoryClass: FeedbackRepository::class)]
class Feedback
{
    /**
     * @var int
     */
    #[ORM\Column(name: 'id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue]
    private int $id;

    /**
     * @var int
     */
    #[ORM\Column(name: 'id_user', type: 'integer', nullable: false)]
    private int $idUser;

    /**
     * @var string
     */
    #[ORM\Column(name: 'description', type: 'string', length: 255, nullable: false)]
    private string $description;

    /**
     * @var DateTime
     */
    #[ORM\Column(name: 'date', type: 'date', nullable: false)]
    private DateTimeInterface $date;

    /**
     * @var int
     */
    #[ORM\Column(name: 'id_episode', type: 'integer', nullable: false)]
    private int $idEpisode;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getIdUser(): ?int
    {
        return $this->idUser;
    }

    public function setIdUser(int $idUser): static
    {
        $this->idUser = $idUser;

        return $this;
    }

    public function getDescription(): ?string
    {
        return $this->description;
    }

    public function setDescription(string $description): static
    {
        $this->description = $description;

        return $this;
    }

    public function getDate(): ?DateTimeInterface
    {
        return $this->date;
    }

    public function setDate(DateTimeInterface $date): static
    {
        $this->date = $date;

        return $this;
    }

    public function getIdEpisode(): ?int
    {
        return $this->idEpisode;
    }

    public function setIdEpisode(int $idEpisode): static
    {
        $this->idEpisode = $idEpisode;

        return $this;
    }
}
