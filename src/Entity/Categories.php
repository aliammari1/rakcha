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

    public function getIdcategorie(): ?int
    {
        return $this->idcategorie;
    }

    public function getNom(): ?string
    {
        return $this->nom;
    }

    public function setNom(string $nom): static
    {
        $this->nom = $nom;

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
}
