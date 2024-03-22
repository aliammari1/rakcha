<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Filmcinema
 *
 * @ORM\Table(name="filmcinema", indexes={@ORM\Index(name="fk_fc1", columns={"id_film"}), @ORM\Index(name="fk_fc2", columns={"id_cinema"})})
 * @ORM\Entity
 */
class Filmcinema
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
     * @ORM\Column(name="id_cinema", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="NONE")
     */
    private $idCinema;


}
