<?php

namespace App\Entity;

use App\Repository\ActorfilmRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;




#[ORM\Entity(repositoryClass: ActorfilmRepository::class)]
#[ORM\Table(name: 'actorfilm')]
#[ORM\Index(name: 'fk_idfilm', columns: ['idfilm'])]
class Actorfilm
{

    #[ORM\Column(name: 'idactor', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'NONE')]
    private int $idactor;


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
