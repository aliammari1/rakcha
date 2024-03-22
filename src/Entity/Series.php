<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Series
 *
 * @ORM\Table(name="series", indexes={@ORM\Index(name="idcategorie", columns={"idcategorie"})})
 * @ORM\Entity
 */
class Series
{
    /**
     * @var int
     *
     * @ORM\Column(name="idserie", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $idserie;

    /**
     * @var string
     *
     * @ORM\Column(name="nom", type="string", length=30, nullable=false)
     */
    private $nom;

    /**
     * @var string
     *
     * @ORM\Column(name="resume", type="string", length=50, nullable=false)
     */
    private $resume;

    /**
     * @var string
     *
     * @ORM\Column(name="directeur", type="string", length=50, nullable=false)
     */
    private $directeur;

    /**
     * @var string
     *
     * @ORM\Column(name="pays", type="string", length=50, nullable=false)
     */
    private $pays;

    /**
     * @var string
     *
     * @ORM\Column(name="image", type="string", length=255, nullable=false)
     */
    private $image;

    /**
     * @var int
     *
     * @ORM\Column(name="liked", type="integer", nullable=false)
     */
    private $liked;

    /**
     * @var int
     *
     * @ORM\Column(name="nbLikes", type="integer", nullable=false)
     */
    private $nblikes;

    /**
     * @var int
     *
     * @ORM\Column(name="disliked", type="integer", nullable=false)
     */
    private $disliked;

    /**
     * @var int
     *
     * @ORM\Column(name="nbDislikes", type="integer", nullable=false)
     */
    private $nbdislikes;

    /**
     * @var int
     *
     * @ORM\Column(name="idcategorie", type="integer", nullable=false)
     */
    private $idcategorie;


}
