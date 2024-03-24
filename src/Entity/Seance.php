<?php

namespace App\Entity;

use App\Repository\SeanceRepository;
use App\Entity\Cinema;
use DateTime;
use DateTimeInterface;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;
use App\Entity\Film;
use App\Entity\Salle;


#[ORM\Entity(repositoryClass: SeanceRepository::class)]
#[ORM\Table(name: 'seance')]
#[ORM\Index(name: 'fk_cinema_seance', columns: ['id_cinema'])]
#[ORM\Index(name: 'fk_film_seance', columns: ['id_film'])]
#[ORM\Index(name: 'fk_salle_seance', columns: ['id_salle'])]
class Seance
{
    #[ORM\Column(name: 'id_seance', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $idSeance;

    /**
     * @var DateTime
     */
    #[ORM\Column(name: 'HD', type: 'time', nullable: false)]
    private DateTimeInterface $hd;

    /**
     * @var DateTime
     */
    #[ORM\Column(name: 'HF', type: 'time', nullable: false)]
    private DateTimeInterface $hf;

    /**
     * @var DateTime
     */
    #[ORM\Column(name: 'date', type: 'date', nullable: false)]
    private DateTimeInterface $date;

    #[ORM\Column(name: 'prix', type: 'float', precision: 10, scale: 0, nullable: false)]
    private float $prix;

    /**
     * @var Film
     */
    #[ORM\ManyToOne(targetEntity: Film::class)]
    #[ORM\JoinColumn(name: 'id_film', referencedColumnName: 'id')]
    private Film $idFilm;

    /**
     * @var Salle
     */
    #[ORM\ManyToOne(targetEntity: Salle::class)]
    #[ORM\JoinColumn(name: 'id_salle', referencedColumnName: 'id_salle')]
    private Salle $idSalle;

    /**
     * @var Cinema
     */
    #[ORM\ManyToOne(targetEntity: Cinema::class)]
    #[ORM\JoinColumn(name: 'id_cinema', referencedColumnName: 'id_cinema')]
    private Cinema $idCinema;

    /**
     * @var Collection<int, Users>
     */
    #[ORM\ManyToMany(targetEntity: Users::class, mappedBy: 'idSeance')]
    private Collection $idUser;

    /**
     * Constructor
     */
    public function __construct()
    {
        $this->idUser = new ArrayCollection();
    }

}
