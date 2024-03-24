<?php

namespace App\Entity;

use App\Repository\CinemaRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;


#[ORM\Entity(repositoryClass: CinemaRepository::class)]
#[ORM\Table(name: 'cinema')]
class Cinema
{
    #[ORM\Column(name: 'id_cinema', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $idCinema;

    #[ORM\Column(name: 'nom', type: 'string', length: 50, nullable: false)]
    private string $nom;

    #[ORM\Column(name: 'adresse', type: 'string', length: 100, nullable: false)]
    private string $adresse;

    #[ORM\Column(name: 'responsable', type: 'integer', nullable: false)]
    private int $responsable;

    #[ORM\Column(name: 'logo', type: 'text', length: 0, nullable: true)]
    private ?string $logo = null;

    #[ORM\Column(name: 'Statut', type: 'string', length: 50, nullable: false)]
    private string $statut;

    /**
     * @var Collection<int, Users>
     */
    #[ORM\JoinTable(name: 'ratingcinema')]
    #[ORM\JoinColumn(name: 'id_cinema', referencedColumnName: 'id_cinema')]
    #[ORM\InverseJoinColumn(name: 'id_user', referencedColumnName: 'id')]
    #[ORM\ManyToMany(targetEntity: Users::class, inversedBy: 'idCinema')]
    private Collection $idUser;

    /**
     * Constructor
     */
    public function __construct()
    {
        $this->idUser = new ArrayCollection();
    }

}
