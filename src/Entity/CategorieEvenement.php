<?php

namespace App\Entity;

use App\Repository\CategorieEvenementRepository;
use Doctrine\ORM\Mapping as ORM;



#[ORM\Entity(repositoryClass: CategorieEvenementRepository::class)]
#[ORM\Table(name: 'categorie_evenement')]
class CategorieEvenement
{
    #[ORM\Column(name: 'ID', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;

    #[ORM\Column(name: 'Nom_categorie', type: 'string', length: 500, nullable: false)]
    private string $nomCategorie;

    #[ORM\Column(name: 'Description', type: 'string', length: 500, nullable: false)]
    private string $description;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getNomCategorie(): ?string
    {
        return $this->nomCategorie;
    }

    public function setNomCategorie(string $nomCategorie): static
    {
        $this->nomCategorie = $nomCategorie;

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
