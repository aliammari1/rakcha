<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

use App\Repository\FilmcategoryRepository;

#[ORM\Entity(repositoryClass: FilmcategoryRepository::class)]
class Filmcategory
{
    /**
     * @var int
     */
    #[ORM\Column(name: 'film_id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'NONE')]
    private int $filmId;

    /**
     * @var int
     */
    #[ORM\Column(name: 'category_id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'NONE')]
    private int $categoryId;

    public function getFilmId(): ?int
    {
        return $this->filmId;
    }

    public function getCategoryId(): ?int
    {
        return $this->categoryId;
    }
}
