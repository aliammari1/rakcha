<?php

namespace App\Entity;

use App\Repository\ProduitRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: ProduitRepository::class)]
#[ORM\Table(name: 'produit')]
#[ORM\Index(name: 'fk_categories', columns: ['id_categorieProduit'])]
class Produit
{
    #[ORM\Column(name: 'id_produit', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $idProduit;

    #[ORM\Column(name: 'nom', type: 'string', length: 50, nullable: false)]
    #[Assert\NotBlank(message: 'Please enter a name.')]
    #[Assert\Length(max: 50, maxMessage: 'Name cannot exceed {{ limit }} characters.')]
    #[Assert\NotNull(message: 'Name cannot be null.')]
    private string $nom;

    #[ORM\Column(name: 'prix', type: 'integer', nullable: false)]
    #[Assert\NotBlank(message: 'Please enter a price.')]
    #[Assert\Positive(message: 'Price must be a positive number.')]
    #[Assert\NotNull(message: 'Price cannot be null.')]
    private int $prix;

    #[ORM\Column(name: 'image', type: 'string', length: 255, nullable: false)]
    private string $image;

    #[ORM\Column(name: 'description', type: 'string', length: 100, nullable: false)]
    #[Assert\NotBlank(message: 'Please enter a description.')]
    #[Assert\Length(min: 20, max: 100, minMessage: 'Description must be at least {{ limit }} characters.', maxMessage: 'Description cannot exceed {{ limit }} characters.')]
    #[Assert\NotNull(message: 'Description cannot be null.')]
    private string $description;

    #[ORM\Column(name: 'quantiteP', type: 'integer', nullable: false)]
    #[Assert\NotBlank(message: 'Please enter a quantity.')]
    #[Assert\PositiveOrZero(message: 'Quantity must be a positive number or zero.')]
    #[Assert\NotNull(message: 'Quantity cannot be null.')]
    private int $quantitep;

    /**
     * @var CategorieProduit
     */
    #[ORM\ManyToOne(targetEntity: CategorieProduit::class)]
    #[ORM\JoinColumn(name: 'id_categorieProduit', referencedColumnName: 'id_categorie')]
    private CategorieProduit $idCategorieproduit;

    /**
     * @var Collection<int, Users>
     */

    private Collection $idClient;
    private Collection $paniers;
    private Collection $commentaires;

    /**
     * Constructor
     */
    public function __construct()
    {
        $this->idClient = new ArrayCollection();
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

    public function getImage(): ?string
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

    /* #[ORM\OneToMany(targetEntity:Panier::class, mappedBy:"idproduit")]*/

    public function removeIdClient(Users $idClient): static
    {
        $this->idClient->removeElement($idClient);

        return $this;
    }

    /**
     * @return Collection<int, Panier>
     */
    public function getPaniers(): Collection
    {
        return $this->paniers;
    }

    public function addPanier(Panier $panier): self
    {
        if (!$this->paniers->contains($panier)) {
            $this->paniers[] = $panier;
            $panier->setIdproduit($this);
        }

        return $this;
    }

    public function removePanier(Panier $panier): self
    {
        if ($this->paniers->removeElement($panier)) {
            // set the owning side to null (unless already changed)
            if ($panier->getIdproduit() === $this) {
                $panier->setIdproduit(null);
            }
        }

        return $this;
    }

    public function getIdProduit(): ?int
    {
        return $this->idProduit;
    }

    /**
     * @return Collection<int, Commentaire>
     */
    public function getCommentaires(): Collection
    {

        return $this->commentaires;
    }


}
