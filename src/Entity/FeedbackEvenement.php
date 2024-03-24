<?php

namespace App\Entity;

use App\Repository\FeedbackEvenementRepository;
use Doctrine\ORM\Mapping as ORM;


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


}
