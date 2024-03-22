<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Episodes
 *
 * @ORM\Table(name="episodes", indexes={@ORM\Index(name="idserie", columns={"idserie"})})
 * @ORM\Entity
 */
class Episodes
{
    /**
     * @var int
     *
     * @ORM\Column(name="idepisode", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $idepisode;

    /**
     * @var string
     *
     * @ORM\Column(name="titre", type="string", length=30, nullable=false)
     */
    private $titre;

    /**
     * @var int
     *
     * @ORM\Column(name="numeroepisode", type="integer", nullable=false)
     */
    private $numeroepisode;

    /**
     * @var int
     *
     * @ORM\Column(name="saison", type="integer", nullable=false)
     */
    private $saison;

    /**
     * @var string
     *
     * @ORM\Column(name="image", type="string", length=255, nullable=false)
     */
    private $image;

    /**
     * @var string
     *
     * @ORM\Column(name="video", type="string", length=255, nullable=false)
     */
    private $video;

    /**
     * @var \Series
     *
     * @ORM\ManyToOne(targetEntity="Series")
     * @ORM\JoinColumns({
     *   @ORM\JoinColumn(name="idserie", referencedColumnName="idserie")
     * })
     */
    private $idserie;


}
