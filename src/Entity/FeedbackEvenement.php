<?php

namespace App\Entity;

use App\Repository\FeedbackEvenementRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;



#[ORM\Entity(repositoryClass: FeedbackEvenementRepository::class)]
#[ORM\Table(name: 'feedback_evenement')]
#[ORM\Index(name: 'fk_event_11', columns: ['id_evenement'])]
#[ORM\Index(name: 'FK_user_feed', columns: ['id_user'])]
class FeedbackEvenement
{

    #[ORM\Column(name: 'ID', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;


    #[ORM\Column(name: 'commentaire', type: 'string', length: 500, nullable: false)]
    private string $commentaire;

    #[ORM\ManyToOne(targetEntity: Users::class)]
    #[ORM\JoinColumn(name: 'id_user', referencedColumnName: 'id')]
    private ?Users $idUser = null;

    #[ORM\ManyToOne(targetEntity: Evenement::class)]
    #[ORM\JoinColumn(name: 'id_evenement', referencedColumnName: 'ID')]
    private ?Evenement $idEvenement = null;

    public function getId(): ?int
    {
        return $this->id;
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

    public function getIdUser(): ?Users
    {
        return $this->idUser;
    }

    public function setIdUser(?Users $idUser): static
    {
        $this->idUser = $idUser;

        return $this;
    }

    public function getIdEvenement(): ?Evenement
    {
        return $this->idEvenement;
    }

    public function setIdEvenement(?Evenement $idEvenement): static
    {
        $this->idEvenement = $idEvenement;

        return $this;
    }
}
