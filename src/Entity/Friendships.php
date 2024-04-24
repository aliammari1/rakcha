<?php

namespace App\Entity;

use App\Repository\FriendshipsRepository;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\ORM\Mapping as ORM;

#[ORM\Entity(repositoryClass: FriendshipsRepository::class)]
class Friendships
{
    #[ORM\Id]
    #[ORM\GeneratedValue]
    #[ORM\Column]
    private ?int $id = null;


    #[ORM\ManyToOne(inversedBy: 'incomingFriendRequests')]
    private ?Users $receiver = null;

    #[ORM\ManyToOne(inversedBy: 'sentFriendRequests')]
    private ?Users $sender = null;

    #[ORM\Column(length: 255)]
    private ?string $statut = null;

    public function __construct()
    {
        $this->statut = 'no friend request';
    }

    public function getId(): ?int
    {
        return $this->id;
    }

   
    public function getReceiver(): ?Users
    {
        return $this->receiver;
    }

    public function setReceiver(?Users $receiver): static
    {
        $this->receiver = $receiver;

        return $this;
    }

    public function getSender(): ?Users
    {
        return $this->sender;
    }

    public function setSender(?Users $sender): static
    {
        $this->sender = $sender;

        return $this;
    }

    public function getStatut(): ?string
    {
        return $this->statut;
    }

    public function setStatut(string $statut): static
    {
        $this->statut = $statut;

        return $this;
    }
}
