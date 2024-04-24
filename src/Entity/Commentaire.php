<?php

namespace App\Entity;

use App\Repository\CommentaireRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;



#[ORM\Entity(repositoryClass: CommentaireRepository::class)]
#[ORM\Table(name: 'commentaire')]
#[ORM\Index(name: 'fk_clients_comment_1', columns: ['idClient'])]
class Commentaire
{

    #[ORM\Column(name: 'idcommentaire', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $idcommentaire;


    #[ORM\Column(name: 'commentaire', type: 'string', length: 1000, nullable: false)]
    private string $commentaire;

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
