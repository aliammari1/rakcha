<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * CategorieEvenement
 *
 * @ORM\Table(name="categorie_evenement")
 * @ORM\Entity
 */
class CategorieEvenement
{
    /**
     * @var int
     *
     * @ORM\Column(name="ID", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $id;

    /**
     * @var string
     *
     * @ORM\Column(name="Nom_categorie", type="string", length=500, nullable=false)
     */
    private $nomCategorie;

    /**
     * @var string
     *
     * @ORM\Column(name="Description", type="string", length=500, nullable=false)
     */
    private $description;

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
