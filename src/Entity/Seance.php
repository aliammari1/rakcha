<?php

namespace App\Entity;

use App\Repository\SeanceRepository;
use App\Entity\Cinema;
use DateTime;
use DateTimeInterface;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Component\Serializer\Annotation\Groups;

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

    public function getIdSeance(): ?int
    {
        return $this->idSeance;
    }

    public function getHd(): ?\DateTimeInterface
    {
        return $this->hd;
    }

    public function setHd(\DateTimeInterface $hd): static
    {
        $this->hd = $hd;

        return $this;
    }

    public function getHf(): ?\DateTimeInterface
    {
        return $this->hf;
    }

    public function setHf(\DateTimeInterface $hf): static
    {
        $this->hf = $hf;

        return $this;
    }

    public function getDate(): ?\DateTimeInterface
    {
        return $this->date;
    }

    public function setDate(\DateTimeInterface $date): static
    {
        $this->date = $date;

        return $this;
    }

    public function getPrix(): ?float
    {
        return $this->prix;
    }

    public function setPrix(float $prix): static
    {
        $this->prix = $prix;

        return $this;
    }

    public function getIdFilm(): ?Film
    {
        return $this->idFilm;
    }

    public function setIdFilm(?Film $idFilm): static
    {
        $this->idFilm = $idFilm;

        return $this;
    }

    public function getIdSalle(): ?Salle
    {
        return $this->idSalle;
    }

    public function setIdSalle(?Salle $idSalle): static
    {
        $this->idSalle = $idSalle;

        return $this;
    }

    public function getIdCinema(): ?Cinema
    {
        return $this->idCinema;
    }

    public function setIdCinema(?Cinema $idCinema): static
    {
        $this->idCinema = $idCinema;

        return $this;
    }

    /**
     * @return Collection<int, Users>
     */
    public function getIdUser(): Collection
    {
        return $this->idUser;
    }

    public function addIdUser(Users $idUser): static
    {
        if (!$this->idUser->contains($idUser)) {
            $this->idUser->add($idUser);
            $idUser->addIdSeance($this);
        }

        return $this;
    }

    public function removeIdUser(Users $idUser): static
    {
        if ($this->idUser->removeElement($idUser)) {
            $idUser->removeIdSeance($this);
        }

        return $this;
    }
}
