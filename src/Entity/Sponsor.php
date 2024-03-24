<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

use App\Repository\SponsorRepository;

#[ORM\Entity(repositoryClass: SponsorRepository::class)]
#[ORM\Table(name: 'sponsor')]
class Sponsor
{
    #[ORM\Column(name: 'ID', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;

    #[ORM\Column(name: 'nomSociete', type: 'string', length: 500, nullable: false)]
    private string $nomsociete;

    #[ORM\Column(name: 'Logo', type: 'string', length: 255, nullable: false)]
    private string $logo;


}
