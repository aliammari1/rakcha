<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Commentairecinema
 *
 * @ORM\Table(name="commentairecinema", indexes={@ORM\Index(name="fk_user", columns={"idclient"}), @ORM\Index(name="fk_cinema", columns={"idcinema"})})
 * @ORM\Entity
 */
class Commentairecinema
{
    /**
     * @var int
     *
     * @ORM\Column(name="id", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $id;

    /**
     * @var int
     *
     * @ORM\Column(name="idclient", type="integer", nullable=false)
     */
    private $idclient;

    /**
     * @var int
     *
     * @ORM\Column(name="idcinema", type="integer", nullable=false)
     */
    private $idcinema;

    /**
     * @var string
     *
     * @ORM\Column(name="commentaire", type="string", length=5000, nullable=false)
     */
    private $commentaire;

    /**
     * @var string
     *
     * @ORM\Column(name="sentiment", type="string", length=50, nullable=false)
     */
    private $sentiment;


}
