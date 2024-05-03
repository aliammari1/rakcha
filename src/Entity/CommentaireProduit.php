<?php

namespace App\Entity;

use App\Repository\CommentaireProduitRepository;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: CommentaireProduitRepository::class)]
class CommentaireProduit
{
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    #[ORM\Column(name: 'id', type: 'integer', nullable: false)]
    private ?int $id = null;

    
   
    #[ORM\ManyToOne(targetEntity: Users::class)]
    #[ORM\JoinColumn(name: 'id_client_id', referencedColumnName: 'id')]
    private ?Users $idClient ;

    
    #[ORM\Column(name: 'commentaire', type: 'string', length: 255)]
    private ?string $commentaire ;

    #[ORM\ManyToOne(targetEntity: Produit::class)]
    #[ORM\JoinColumn(name: 'id_produit', referencedColumnName: 'id_produit')]
    private ?Produit $idproduit = null;


    public function __construct()
    {
        
    }

    public function getId(): ?int
    {
        return $this->id;
    }

   

    public function getIdClient(): ?Users
    {
        return $this->idClient;
    }

    public function setIdClient(?Users $idClient): static
    {
        $this->idClient = $idClient;

        return $this;
    }

    public function getCommentaire(): ?string
    {
        return $this->commentaire;
    }

    public function setCommentaire(string $commentaire): static
    {
        $this->commentaire = $commentaire;

        return $this;
    }

    public function getIdproduit(): ?Produit
    {
        return $this->idproduit;
    }

    public function setIdproduit(?Produit $idproduit): static
    {
        $this->idproduit = $idproduit;

        return $this;
    }
}
