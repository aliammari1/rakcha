<?php

namespace App\Entity;

use App\Repository\UsersRepository;
use DateTime;
use DateTimeInterface;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: UsersRepository::class)]
#[ORM\Table(name: 'users')]
class Users
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;

    #[ORM\Column(name: 'nom', type: 'string', length: 50, nullable: false)]
    private string $nom;

    #[ORM\Column(name: 'prenom', type: 'string', length: 50, nullable: false)]
    private string $prenom;

    #[ORM\Column(name: 'num_telephone', type: 'integer', nullable: false)]
    private int $numTelephone;

    #[ORM\Column(name: 'password', type: 'string', length: 50, nullable: false)]
    private string $password;

    #[ORM\Column(name: 'role', type: 'string', length: 50, nullable: false)]
    private string $role;

    #[ORM\Column(name: 'adresse', type: 'string', length: 50, nullable: true)]
    private ?string $adresse = null;

    /**
     * @var DateTime|null
     */
    #[ORM\Column(name: 'date_de_naissance', type: 'date', nullable: true)]
    private ?DateTimeInterface $dateDeNaissance = null;

    #[ORM\Column(name: 'email', type: 'string', length: 50, nullable: false)]
    private string $email;

    #[ORM\Column(name: 'photo_de_profil', type: 'string', length: 255, nullable: true)]
    private ?string $photoDeProfil = null;

    /**
     * @var Collection<int, Cinema>
     */
    #[ORM\ManyToMany(targetEntity: Cinema::class, mappedBy: 'idUser')]
    private Collection $idCinema;

    /**
     * @var Collection<int, Seance>
     */
    #[ORM\JoinTable(name: 'ticket')]
    #[ORM\JoinColumn(name: 'id_user', referencedColumnName: 'id')]
    #[ORM\InverseJoinColumn(name: 'id_seance', referencedColumnName: 'id_seance')]
    #[ORM\ManyToMany(targetEntity: Seance::class, inversedBy: 'idUser')]
    private Collection $idSeance;

    /**
     * @var Collection<int, Produit>
     */
    #[ORM\ManyToMany(targetEntity: Produit::class, mappedBy: 'idClient')]
    private Collection $idProduit;

    /**
     * Constructor
     */
    public function __construct()
    {
        $this->idCinema = new ArrayCollection();
        $this->idSeance = new ArrayCollection();
        $this->idProduit = new ArrayCollection();
    }

}
