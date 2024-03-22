<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

use App\Repository\CommentaireRepository;

#[ORM\Entity(repositoryClass: CommentaireRepository::class)]
class Commentaire
{
    /**
     * @var int
     */
    #[ORM\Column(name: 'idcommentaire', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue]
    private int $idcommentaire;

    /**
     * @var string
     */
    #[ORM\Column(name: 'commentaire', type: 'string', length: 1000, nullable: false)]
    private string $commentaire;

    /**
     * @var Users
     */
    #[ORM\ManyToOne(targetEntity: Users::class)]
    #[ORM\JoinColumn(name: 'idClient', referencedColumnName: 'id')]
    private ?Users $idclient = null;

    public function getIdcommentaire(): ?int
    {
        return $this->idcommentaire;
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

    public function getIdclient(): ?Users
    {
        return $this->idclient;
    }

    public function setIdclient(?Users $idclient): static
    {
        $this->idclient = $idclient;

        return $this;
    }
}
