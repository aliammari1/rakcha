<?php

namespace App\Entity;

use App\Repository\ParticipationEvenementRepository;
use Doctrine\ORM\Mapping as ORM;


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


}
