<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Filmcategory
 *
 * @ORM\Table(name="filmcategory", indexes={@ORM\Index(name="fk_filmCategorie_1", columns={"film_id"}), @ORM\Index(name="fk_filmCategorie_2", columns={"category_id"})})
 * @ORM\Entity
 */
class Filmcategory
{
    /**
     * @var int
     *
     * @ORM\Column(name="film_id", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="NONE")
     */
    private $filmId;

    /**
     * @var int
     *
     * @ORM\Column(name="category_id", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="NONE")
     */
    private $categoryId;


}
