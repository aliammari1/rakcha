<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

use App\Repository\ParticipationEvenementRepository;

#[ORM\Entity(repositoryClass: ParticipationEvenementRepository::class)]
class ParticipationEvenement
{
    /**
     * @var int
     */
    #[ORM\Column(name: 'id_participation', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue]
    private int $idParticipation;

    /**
     * @var int
     */
    #[ORM\Column(name: 'id_user', type: 'integer', nullable: false)]
    private int $idUser;

    /**
     * @var int
     */
    #[ORM\Column(name: 'quantity', type: 'integer', nullable: false)]
    private int $quantity;

    /**
     * @var Evenement
     */
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
