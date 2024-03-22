<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * FeedbackEvenement
 *
 * @ORM\Table(name="feedback_evenement", indexes={@ORM\Index(name="fk_event_11", columns={"id_evenement"}), @ORM\Index(name="FK_user_feed", columns={"id_user"})})
 * @ORM\Entity
 */
class FeedbackEvenement
{
    /**
     * @var int
     *
     * @ORM\Column(name="ID", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $id;

    /**
     * @var string
     *
     * @ORM\Column(name="commentaire", type="string", length=500, nullable=false)
     */
    private $commentaire;

    /**
     * @var \Users
     *
     * @ORM\ManyToOne(targetEntity="Users")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="id_user", referencedColumnName="id")
     * })
     */
    private $idUser;

    /**
     * @var \Evenement
     *
     * @ORM\ManyToOne(targetEntity="Evenement")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="id_evenement", referencedColumnName="ID")
     * })
     */
    private $idEvenement;


}
