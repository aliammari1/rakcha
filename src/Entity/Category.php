<?php

namespace App\Entity;

use App\Repository\CategoryRepository;
use Doctrine\ORM\Mapping as ORM;



#[ORM\Entity(repositoryClass: CategoryRepository::class)]
#[ORM\Table(name: 'category')]
#[ORM\UniqueConstraint(name: 'nom', columns: ['nom'])]
class Category
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;

    #[ORM\Column(name: 'nom', type: 'string', length: 255, nullable: false)]
    private string $nom;

    #[ORM\Column(name: 'description', type: 'text', length: 0, nullable: false)]
    private string $description;


}
