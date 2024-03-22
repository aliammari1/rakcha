<?php

namespace App\Entity;

use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;

use App\Repository\ProduitRepository;

#[ORM\Entity(repositoryClass: ProduitRepository::class)]
class Produit
{
    /**
     * @var int
     */
    #[ORM\Column(name: 'id_produit', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue]
    private int $idProduit;

    /**
     * @var string
     */
    #[ORM\Column(name: 'nom', type: 'string', length: 50, nullable: false)]
    private string $nom;

    /**
     * @var int
     */
    #[ORM\Column(name: 'prix', type: 'integer', nullable: false)]
    private int $prix;

    /**
     * @var string
     */
    #[ORM\Column(name: 'image', type: 'blob', length: 65535, nullable: false)]
    private string $image;

    /**
     * @var string
     */
    #[ORM\Column(name: 'description', type: 'string', length: 100, nullable: false)]
    private string $description;

    /**
     * @var int
     */
    #[ORM\Column(name: 'quantiteP', type: 'integer', nullable: false)]
    private int $quantitep;

    /**
     * @var CategorieProduit
     */
    #[ORM\ManyToOne(targetEntity: CategorieProduit::class)]
    #[ORM\JoinColumn(name: 'id_categorieProduit', referencedColumnName: 'id_categorie')]
    private ?CategorieProduit $idCategorieproduit = null;

    /**
     * @var Collection<int, Users>
     */
    #[ORM\JoinTable(name: 'panier')]
    #[ORM\JoinColumn(name: 'id_produit', referencedColumnName: 'id_produit')]
    #[ORM\InverseJoinColumn(name: 'id_client', referencedColumnName: 'id')]
    #[ORM\ManyToMany(targetEntity: Users::class, inversedBy: 'idProduit')]
    private Collection $idClient;

    /**
     * Constructor
     */
    public function __construct()
    {
        $this->idClient = new ArrayCollection();
    }

    public function getIdProduit(): ?int
    {
        return $this->idProduit;
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

    public function getPrix(): ?int
    {
        return $this->prix;
    }

    public function setPrix(int $prix): static
    {
        $this->prix = $prix;

        return $this;
    }

    public function getImage(): string
    {
        return $this->image;
    }

    public function setImage(string $image): static
    {
        $this->image = $image;

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

    public function getQuantitep(): ?int
    {
        return $this->quantitep;
    }

    public function setQuantitep(int $quantitep): static
    {
        $this->quantitep = $quantitep;

        return $this;
    }

    public function getIdCategorieproduit(): ?CategorieProduit
    {
        return $this->idCategorieproduit;
    }

    public function setIdCategorieproduit(?CategorieProduit $idCategorieproduit): static
    {
        $this->idCategorieproduit = $idCategorieproduit;

        return $this;
    }

    /**
     * @return Collection<int, Users>
     */
    public function getIdClient(): Collection
    {
        return $this->idClient;
    }

    public function addIdClient(Users $idClient): static
    {
        if (!$this->idClient->contains($idClient)) {
            $this->idClient->add($idClient);
        }

        return $this;
    }

    public function removeIdClient(Users $idClient): static
    {
        $this->idClient->removeElement($idClient);

        return $this;
    }
}
