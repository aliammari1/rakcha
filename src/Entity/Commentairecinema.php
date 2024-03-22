<?php

namespace App\Entity;

use Doctrine\ORM\Mapping as ORM;

/**
 * Commentairecinema
 *
 * @ORM\Table(name="commentairecinema", indexes={@ORM\Index(name="fk_user", columns={"idclient"}), @ORM\Index(name="fk_cinema", columns={"idcinema"})})
 * @ORM\Entity
 */
class Commentairecinema
{
    /**
     * @var int
     *
     * @ORM\Column(name="id", type="integer", nullable=false)
     * @ORM\Id
     * @ORM\GeneratedValue(strategy="IDENTITY")
     */
    private $id;

    /**
     * @var int
     *
     * @ORM\Column(name="idclient", type="integer", nullable=false)
     */
    private $idclient;

    /**
     * @var int
     *
     * @ORM\Column(name="idcinema", type="integer", nullable=false)
     */
    private $idcinema;

    /**
     * @var string
     *
     * @ORM\Column(name="commentaire", type="string", length=5000, nullable=false)
     */
    private $commentaire;

    /**
     * @var string
     *
     * @ORM\Column(name="sentiment", type="string", length=50, nullable=false)
     */
    private $sentiment;

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getIdclient(): ?int
    {
        return $this->idclient;
    }

    public function setIdclient(int $idclient): static
    {
        $this->idclient = $idclient;

        return $this;
    }

    public function getIdcinema(): ?int
    {
        return $this->idcinema;
    }

    public function setIdcinema(int $idcinema): static
    {
        $this->idcinema = $idcinema;

        return $this;
    }

    public function getCommentaire(): ?string
    {
        return $this->commentaire;
    }

    public function setCommentaire(string $commentaire): static
    {
        $this->commentaire = $commentaire;

        return $this;
    }

    public function getSentiment(): ?string
    {
        return $this->sentiment;
    }

    public function setSentiment(string $sentiment): static
    {
        $this->sentiment = $sentiment;

        return $this;
    }


}
