<?php

namespace App\Entity;

use App\Repository\FilmRepository;
use DateTime;
use DateTimeInterface;
use Doctrine\ORM\Mapping as ORM;



#[ORM\Entity(repositoryClass: FilmRepository::class)]
#[ORM\Table(name: 'film')]
class Film
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;

    #[ORM\Column(name: 'nom', type: 'string', length: 255, nullable: false)]
    private string $nom;

    #[ORM\Column(name: 'image', type: 'text', length: 0, nullable: true)]
    private ?string $image = null;

    /**
     * @var DateTime
     */
    #[ORM\Column(name: 'duree', type: 'time', nullable: false)]
    private DateTimeInterface $duree;

    #[ORM\Column(name: 'description', type: 'text', length: 0, nullable: false)]
    private string $description;

    #[ORM\Column(name: 'annederalisation', type: 'integer', nullable: false)]
    private int $annederalisation;


}
