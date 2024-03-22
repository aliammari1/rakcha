<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

use App\Repository\ActorfilmRepository;
#[ORM\Entity(repositoryClass: ActorfilmRepository::class)]
class Actorfilm
{
    /**
     * @var int
     */
    #[ORM\Column(name: 'idactor', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'NONE')]
    private int $idactor;

    /**
     * @var int
     */
    #[ORM\Column(name: 'idfilm', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'NONE')]
    private int $idfilm;

    public function getIdactor(): ?int
    {
        return $this->idactor;
    }

    public function getIdfilm(): ?int
    {
        return $this->idfilm;
    }
}
