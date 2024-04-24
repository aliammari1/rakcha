<?php

namespace App\Entity;

use App\Repository\ParticipationEvenementRepository;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;



#[ORM\Entity(repositoryClass: ParticipationEvenementRepository::class)]
#[ORM\Table(name: 'participation_evenement')]
#[ORM\Index(name: 'cle_secondaire2', columns: ['id_user'])]
#[ORM\Index(name: 'cle_secondaire1', columns: ['id_evenement'])]
class ParticipationEvenement
{

    #[ORM\Column(name: 'id_participation', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $idParticipation;


    #[ORM\Column(name: 'id_user', type: 'integer', nullable: false)]
    private int $idUser;


    #[ORM\Column(name: 'quantity', type: 'integer', nullable: false)]
    private int $quantity;

    #[ORM\ManyToOne(targetEntity: Evenement::class)]
    #[ORM\JoinColumn(name: 'id_evenement', referencedColumnName: 'ID')]
    private ?Evenement $idEvenement = null;

    public function getIdParticipation(): ?int
    {
        return $this->idParticipation;
    }

    public function getIdUser(): ?int
    {
        return $this->idUser;
    }

    public function setIdUser(int $idUser): static
    {
        $this->idUser = $idUser;

        return $this;
    }

    public function getQuantity(): ?int
    {
        return $this->quantity;
    }

    public function setQuantity(int $quantity): static
    {
        $this->quantity = $quantity;

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
