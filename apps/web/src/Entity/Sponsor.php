<?php

namespace App\Entity;

use App\Repository\SponsorRepository;
use Doctrine\ORM\Mapping as ORM;


#[ORM\Entity(repositoryClass: SponsorRepository::class)]
#[ORM\Table(name: 'sponsor')]
class Sponsor
{

    #[ORM\Column(name: 'ID', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;


    #[ORM\Column(name: 'nomSociete', type: 'string', length: 50, nullable: false)]
    #[Assert\NotBlank(message: "The company's name is required.")]
    #[Assert\Length(
        min: 2,
        max: 50,
        minMessage: "The company's name must be at least {{ limit }} characters long.",
        maxMessage: "The company's name cannot exceed {{ limit }} characters."
    )]
    private string $nomsociete;


    #[ORM\Column(name: 'Logo', type: 'string', length: 500, nullable: false)]
    #[Assert\NotBlank(message: "The Logo's path is required.")]
    #[Assert\Length(
        min: 2,
        max: 50,
        minMessage: "The logo's path must be at least {{ limit }} characters long.",
        maxMessage: "The logo's path cannot exceed {{ limit }} characters."
    )]
    private string $logo;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getNomsociete(): ?string
    {
        return $this->nomsociete;
    }

    public function setNomsociete(string $nomsociete): static
    {
        $this->nomsociete = $nomsociete;

        return $this;
    }

    public function getLogo(): ?string
    {
        return $this->logo;
    }

    public function setLogo(string $logo): static
    {
        $this->logo = $logo;

        return $this;
    }
}
