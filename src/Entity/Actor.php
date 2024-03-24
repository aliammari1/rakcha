<?php

namespace App\Entity;

use App\Repository\ActorRepository;
use Doctrine\ORM\Mapping as ORM;



#[ORM\Entity(repositoryClass: ActorRepository::class)]
#[ORM\Table(name: 'actor')]
class Actor
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;

    #[ORM\Column(name: 'nom', type: 'string', length: 255, nullable: false)]
    private string $nom;

    #[ORM\Column(name: 'image', type: 'text', length: 0, nullable: false)]
    private string $image;

    #[ORM\Column(name: 'biographie', type: 'text', length: 0, nullable: false)]
    private string $biographie;


}
