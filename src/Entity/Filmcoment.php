<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Filmcoment
 *
 * @ORM\Table(name="filmcoment", indexes={@ORM\Index(name="pk_comment_film", columns={"film_id"}), @ORM\Index(name="pk_comment_user", columns={"user_id"})})
 * @ORM\Entity
 */
class Filmcoment
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
     * @var string
     *
     * @ORM\Column(name="comment", type="string", length=255, nullable=false)
     */
    private $comment;

    /**
     * @var int
     *
     * @ORM\Column(name="user_id", type="integer", nullable=false)
     */
    private $userId;

    /**
     * @var int
     *
     * @ORM\Column(name="film_id", type="integer", nullable=false)
     */
    private $filmId;


}
