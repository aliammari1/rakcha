<?php

namespace App\Entity;

use App\Repository\AvisRepository;
use Doctrine\ORM\Mapping as ORM;


#[ORM\Entity(repositoryClass: AvisRepository::class)]
#[ORM\Table(name: 'avis')]
#[ORM\Index(name: 'FK_clients', columns: ['idusers'])]
#[ORM\Index(name: 'id_produit', columns: ['id_produit'])]
class Avis
{
    #[ORM\Column(name: 'id', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;

    #[ORM\Column(name: 'note', type: 'integer', nullable: false)]
    private int $note;

    #[ORM\Column(name: 'avis', type: 'string', length: 255, nullable: true)]
    private ?string $avis = null;

    #[ORM\ManyToOne(targetEntity: Users::class)]
    #[ORM\JoinColumn(name: 'idusers', referencedColumnName: 'id')]
    private ?Users $idusers = null;

    #[ORM\ManyToOne(targetEntity: Produit::class)]
    #[ORM\JoinColumn(name: 'id_produit', referencedColumnName: 'id_produit')]
    private ?Produit $idProduit = null;


}
