<?php

namespace App\Entity;

use App\Repository\FilmcategoryRepository;
use Doctrine\ORM\Mapping as ORM;



#[ORM\Entity(repositoryClass: FilmcategoryRepository::class)]
#[ORM\Table(name: 'filmcategory')]
#[ORM\Index(name: 'fk_filmCategorie_2', columns: ['category_id'])]
#[ORM\Index(name: 'fk_filmCategorie_1', columns: ['film_id'])]
class Filmcategory
{
    #[ORM\Column(name: 'film_id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'NONE')]
    private int $filmId;

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
