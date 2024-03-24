<?php

namespace App\Entity;

use App\Repository\EpisodesRepository;
use Doctrine\ORM\Mapping as ORM;


#[ORM\Entity(repositoryClass: EpisodesRepository::class)]
#[ORM\Table(name: 'episodes')]
#[ORM\Index(name: 'idserie', columns: ['idserie'])]
class Episodes
{
    #[ORM\Column(name: 'idepisode', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $idepisode;

    #[ORM\Column(name: 'titre', type: 'string', length: 30, nullable: false)]
    private string $titre;

    #[ORM\Column(name: 'numeroepisode', type: 'integer', nullable: false)]
    private int $numeroepisode;

    #[ORM\Column(name: 'saison', type: 'integer', nullable: false)]
    private int $saison;

    #[ORM\Column(name: 'image', type: 'string', length: 255, nullable: false)]
    private string $image;

    #[ORM\Column(name: 'video', type: 'string', length: 255, nullable: false)]
    private string $video;

    #[ORM\ManyToOne(targetEntity: Series::class)]
    #[ORM\JoinColumn(name: 'idserie', referencedColumnName: 'idserie')]
    private ?Series $idserie = null;


}
