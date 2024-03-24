<?php

namespace App\Entity;

use App\Repository\SeriesRepository;
use Doctrine\ORM\Mapping as ORM;



#[ORM\Entity(repositoryClass: SeriesRepository::class)]
#[ORM\Table(name: 'series')]
#[ORM\Index(name: 'idcategorie', columns: ['idcategorie'])]
class Series
{
    #[ORM\Column(name: 'idserie', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $idserie;

    #[ORM\Column(name: 'nom', type: 'string', length: 30, nullable: false)]
    private string $nom;

    #[ORM\Column(name: 'resume', type: 'string', length: 50, nullable: false)]
    private string $resume;

    #[ORM\Column(name: 'directeur', type: 'string', length: 50, nullable: false)]
    private string $directeur;

    #[ORM\Column(name: 'pays', type: 'string', length: 50, nullable: false)]
    private string $pays;

    #[ORM\Column(name: 'image', type: 'string', length: 255, nullable: false)]
    private string $image;

    #[ORM\Column(name: 'liked', type: 'integer', nullable: false)]
    private int $liked;

    #[ORM\Column(name: 'nbLikes', type: 'integer', nullable: false)]
    private int $nblikes;

    #[ORM\Column(name: 'disliked', type: 'integer', nullable: false)]
    private int $disliked;

    #[ORM\Column(name: 'nbDislikes', type: 'integer', nullable: false)]
    private int $nbdislikes;

    #[ORM\Column(name: 'idcategorie', type: 'integer', nullable: false)]
    private int $idcategorie;


}
