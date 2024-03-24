<?php

namespace App\Entity;

use App\Repository\FavorisRepository;
use Doctrine\ORM\Mapping as ORM;



#[ORM\Entity(repositoryClass: FavorisRepository::class)]
#[ORM\Table(name: 'favoris')]
#[ORM\Index(name: 'fk_fav_serie', columns: ['id_serie'])]
#[ORM\Index(name: 'fk_fav_user', columns: ['id_user'])]
class Favoris
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;

    #[ORM\Column(name: 'id_user', type: 'integer', nullable: false)]
    private int $idUser;

    #[ORM\Column(name: 'id_serie', type: 'integer', nullable: false)]
    private int $idSerie;


}
