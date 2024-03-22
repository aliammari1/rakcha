<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Ratingfilm
 *
 * @ORM\Table(name="ratingfilm", indexes={@ORM\Index(name="fk_user_ratin", columns={"id_user"}), @ORM\Index(name="fk_film_rating", columns={"id_film"})})
 * @ORM\Entity
 */
class Ratingfilm
{
    /**
     * @var int
     *
     * @ORM\Column(name="id_film", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="NONE")
     */
    private $idFilm;

    /**
     * @var int
     *
     * @ORM\Column(name="id_user", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="NONE")
     */
    private $idUser;

    /**
     * @var int|null
     *
     * @ORM\Column(name="rate", type="integer", nullable=true)
     */
    private $rate;


}
