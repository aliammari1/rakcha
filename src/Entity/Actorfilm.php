<?php

namespace App\Entity;

use App\Repository\ActorfilmRepository;
use Doctrine\ORM\Mapping as ORM;



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


}
