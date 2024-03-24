<?php

namespace App\Entity;

use App\Repository\EvenementRepository;
use DateTime;
use DateTimeInterface;
use Doctrine\ORM\Mapping as ORM;


#[ORM\Entity(repositoryClass: EvenementRepository::class)]
#[ORM\Table(name: 'evenement')]
#[ORM\Index(name: 'cle_secondaire', columns: ['id_categorie'])]
class Evenement
{
    #[ORM\Column(name: 'ID', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;

    #[ORM\Column(name: 'nom', type: 'string', length: 500, nullable: false)]
    private string $nom;

    /**
     * @var DateTime
     */
    #[ORM\Column(name: 'dateDebut', type: 'date', nullable: false)]
    private DateTimeInterface $datedebut;

    /**
     * @var DateTime
     */
    #[ORM\Column(name: 'dateFin', type: 'date', nullable: false)]
    private DateTimeInterface $datefin;

    #[ORM\Column(name: 'lieu', type: 'string', length: 500, nullable: false)]
    private string $lieu;

    #[ORM\Column(name: 'etat', type: 'string', length: 500, nullable: false)]
    private string $etat;

    #[ORM\Column(name: 'description', type: 'string', length: 500, nullable: false)]
    private string $description;

    #[ORM\Column(name: 'affiche_event', type: 'string', length: 255, nullable: false)]
    private string $afficheEvent;

    #[ORM\ManyToOne(targetEntity: CategorieEvenement::class)]
    #[ORM\JoinColumn(name: 'id_categorie', referencedColumnName: 'ID')]
    private ?CategorieEvenement $idCategorie = null;


}
