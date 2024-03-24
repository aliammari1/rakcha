<?php

namespace App\Entity;

use App\Repository\CategoriesRepository;
use Doctrine\ORM\Mapping as ORM;



#[ORM\Entity(repositoryClass: CategoriesRepository::class)]
#[ORM\Table(name: 'categories')]
class Categories
{
    #[ORM\Column(name: 'idcategorie', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $idcategorie;

    #[ORM\Column(name: 'nom', type: 'string', length: 50, nullable: false)]
    private string $nom;

    #[ORM\Column(name: 'description', type: 'string', length: 50, nullable: false)]
    private string $description;


}
