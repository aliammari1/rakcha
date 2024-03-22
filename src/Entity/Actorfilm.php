<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Actorfilm
 *
 * @ORM\Table(name="actorfilm", indexes={@ORM\Index(name="fk_idfilm", columns={"idfilm"})})
 * @ORM\Entity
 */
class Actorfilm
{
    /**
     * @var int
     *
     * @ORM\Column(name="idactor", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="NONE")
     */
    private $idactor;

    /**
     * @var int
     *
     * @ORM\Column(name="idfilm", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="NONE")
     */
    private $idfilm;


}
