<?php

namespace App\Entity;

use App\Repository\FeedbackRepository;
use DateTime;
use DateTimeInterface;
use Doctrine\ORM\Mapping as ORM;



#[ORM\Entity(repositoryClass: FeedbackRepository::class)]
#[ORM\Table(name: 'feedback')]
#[ORM\Index(name: 'fk_feedback_episode', columns: ['id_episode'])]
#[ORM\Index(name: 'fk_feedback_user', columns: ['id_user'])]
class Feedback
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;

    #[ORM\Column(name: 'id_user', type: 'integer', nullable: false)]
    private int $idUser;

    #[ORM\Column(name: 'description', type: 'string', length: 255, nullable: false)]
    private string $description;

    /**
     * @var DateTime
     */
    #[ORM\Column(name: 'date', type: 'date', nullable: false)]
    private DateTimeInterface $date;

    #[ORM\Column(name: 'id_episode', type: 'integer', nullable: false)]
    private int $idEpisode;


}
