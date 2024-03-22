<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

use App\Repository\FavorisRepository;

#[ORM\Entity(repositoryClass: FavorisRepository::class)]
class Favoris
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
     * @var int
     */
    #[ORM\Column(name: 'id_serie', type: 'integer', nullable: false)]
    private int $idSerie;

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

    public function getIdSerie(): ?int
    {
        return $this->idSerie;
    }

    public function setIdSerie(int $idSerie): static
    {
        $this->idSerie = $idSerie;

        return $this;
    }
}
