<?php

namespace App\Entity;

use App\Repository\CategorieProduitRepository;
use Doctrine\ORM\Mapping as ORM;



#[ORM\Entity(repositoryClass: CategorieProduitRepository::class)]
#[ORM\Table(name: 'categorie_produit')]
class CategorieProduit
{
    #[ORM\Column(name: 'id_categorie', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $idCategorie;

    #[ORM\Column(name: 'nom_categorie', type: 'string', length: 50, nullable: false)]
    private string $nomCategorie;

    #[ORM\Column(name: 'description', type: 'string', length: 255, nullable: false)]
    private string $description;


}
