<?php

namespace App\Entity;

use App\Repository\CommentairecinemaRepository;
use Doctrine\ORM\Mapping as ORM;



#[ORM\Entity(repositoryClass: CommentairecinemaRepository::class)]
#[ORM\Table(name: 'commentairecinema')]
#[ORM\Index(name: 'fk_user', columns: ['idclient'])]
#[ORM\Index(name: 'fk_cinema', columns: ['idcinema'])]
class Commentairecinema
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;

    #[ORM\Column(name: 'idclient', type: 'integer', nullable: false)]
    private int $idclient;

    #[ORM\Column(name: 'idcinema', type: 'integer', nullable: false)]
    private int $idcinema;

    #[ORM\Column(name: 'commentaire', type: 'string', length: 5000, nullable: false)]
    private string $commentaire;

    #[ORM\Column(name: 'sentiment', type: 'string', length: 50, nullable: false)]
    private string $sentiment;


}
