<?php

namespace App\Entity;

use App\Repository\SalleRepository;
use Doctrine\ORM\Mapping as ORM;


#[ORM\Entity(repositoryClass: SalleRepository::class)]
#[ORM\Table(name: 'salle')]
#[ORM\Index(name: 'fk_cinema_salle', columns: ['id_cinema'])]
class Salle
{
    #[ORM\Column(name: 'id_salle', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $idSalle;

    #[ORM\Column(name: 'nb_places', type: 'integer', nullable: false)]
    private int $nbPlaces;

    #[ORM\Column(name: 'nom_salle', type: 'string', length: 50, nullable: false)]
    private string $nomSalle;

    #[ORM\ManyToOne(targetEntity: Cinema::class)]
    #[ORM\JoinColumn(name: 'id_cinema', referencedColumnName: 'id_cinema')]
    private ?Cinema $idCinema = null;


}
