<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

use App\Repository\CategoriesRepository;

#[ORM\Entity(repositoryClass: CategoriesRepository::class)]
class Categories
{
    /**
     * @var int
     */
    #[ORM\Column(name: 'idcategorie', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue]
    private int $idcategorie;

    /**
     * @var string
     */
    #[ORM\Column(name: 'nom', type: 'string', length: 50, nullable: false)]
    private string $nom;

    /**
     * @var string
     */
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
