<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

use App\Repository\EpisodesRepository;

#[ORM\Entity(repositoryClass: EpisodesRepository::class)]
class Episodes
{
    /**
     * @var int
     */
    #[ORM\Column(name: 'idepisode', type: 'integer', nullable: false)]
    #[ORM\Id]
    #[ORM\GeneratedValue]
    private int $idepisode;

    /**
     * @var string
     */
    #[ORM\Column(name: 'titre', type: 'string', length: 30, nullable: false)]
    private string $titre;

    /**
     * @var int
     */
    #[ORM\Column(name: 'numeroepisode', type: 'integer', nullable: false)]
    private int $numeroepisode;

    /**
     * @var int
     */
    #[ORM\Column(name: 'saison', type: 'integer', nullable: false)]
    private int $saison;

    /**
     * @var string
     */
    #[ORM\Column(name: 'image', type: 'string', length: 255, nullable: false)]
    private string $image;

    /**
     * @var string
     */
    #[ORM\Column(name: 'video', type: 'string', length: 255, nullable: false)]
    private string $video;

    /**
     * @var Series
     */
    #[ORM\ManyToOne(targetEntity: Series::class)]
    #[ORM\JoinColumn(name: 'idserie', referencedColumnName: 'idserie')]
    private ?Series $idserie = null;

    public function getIdepisode(): ?int
    {
        return $this->idepisode;
    }

    public function getTitre(): ?string
    {
        return $this->titre;
    }

    public function setTitre(string $titre): static
    {
        $this->titre = $titre;

        return $this;
    }

    public function getNumeroepisode(): ?int
    {
        return $this->numeroepisode;
    }

    public function setNumeroepisode(int $numeroepisode): static
    {
        $this->numeroepisode = $numeroepisode;

        return $this;
    }

    public function getSaison(): ?int
    {
        return $this->saison;
    }

    public function setSaison(int $saison): static
    {
        $this->saison = $saison;

        return $this;
    }

    public function getImage(): ?string
    {
        return $this->image;
    }

    public function setImage(string $image): static
    {
        $this->image = $image;

        return $this;
    }

    public function getVideo(): ?string
    {
        return $this->video;
    }

    public function setVideo(string $video): static
    {
        $this->video = $video;

        return $this;
    }

    public function getIdserie(): ?Series
    {
        return $this->idserie;
    }

    public function setIdserie(?Series $idserie): static
    {
        $this->idserie = $idserie;

        return $this;
    }
}
