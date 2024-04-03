<?php

namespace App\Entity;

use App\Repository\UsersRepository;
use DateTime;
use DateTimeInterface;
use Doctrine\Common\Collections\ArrayCollection;
use Doctrine\Common\Collections\Collection;
use Doctrine\DBAL\Types\Types;
use Doctrine\ORM\Mapping as ORM;
use Symfony\Bridge\Doctrine\Validator\Constraints\UniqueEntity;
use Symfony\Component\Security\Core\User\PasswordAuthenticatedUserInterface;
use Symfony\Component\Security\Core\User\UserInterface;
use Symfony\Component\Validator\Constraints as Assert;

#[ORM\Entity(repositoryClass: UsersRepository::class)]
#[ORM\Table(name: 'users')]
#[UniqueEntity(fields: ['email'], message: 'There is already an account with this email')]
class Users implements UserInterface, PasswordAuthenticatedUserInterface
{
    #[ORM\Column(name: 'id', type: 'integer')]
    #[ORM\Id]
    #[ORM\GeneratedValue(strategy: 'IDENTITY')]
    private int $id;

    #[ORM\Column(name: 'nom', type: 'string', length: 50)]
    #[Assert\NotNull(message: 'The name cannot be null')]
    #[Assert\Length(min: 2, max: 50, minMessage: 'The name must have at least {{ limit }} characters', maxMessage: 'The name cannot exceed {{ limit }} characters')]
    private string $nom;

    #[ORM\Column(name: 'prenom', type: 'string', length: 50)]
    #[Assert\NotNull(message: 'The surname cannot be null')]
    #[Assert\Length(min: 2, max: 50, minMessage: 'The surname must have at least {{ limit }} characters', maxMessage: 'The surname cannot exceed {{ limit }} characters')]
    private string $prenom;

    #[ORM\Column(name: 'num_telephone', type: 'integer')]
    #[Assert\NotNull(message: 'The telephone number cannot be null')]
    #[Assert\Positive(message: 'The telephone number must be a positive integer')]
    private int $numTelephone;

    #[ORM\Column(name: 'password', type: 'string', length: 180)]
    #[Assert\NotNull(message: 'The password cannot be null')]
    #[Assert\Length(min: 8, max: 180, minMessage: 'The password must have at least {{ limit }} characters', maxMessage: 'The password cannot exceed {{ limit }} characters')]
    private string $password;

    #[ORM\Column(name: 'role', type: 'string', length: 50)]
    #[Assert\NotNull(message: 'The role cannot be null')]
    #[Assert\Choice(choices: ['admin', 'client'], message: 'Invalid role. Choose either "admin" or "client".')]
    private string $role;

    #[ORM\Column(name: 'adresse', type: 'string', length: 50)]
    #[Assert\NotNull(message: 'The address cannot be null')]
    #[Assert\Length(min: 5, max: 50, minMessage: 'The address must have at least {{ limit }} characters', maxMessage: 'The address cannot exceed {{ limit }} characters')]
    private ?string $adresse = null;

    #[ORM\Column(name: 'date_de_naissance', type: 'date')]
    #[Assert\NotNull(message: 'The date of birth cannot be null')]
    #[Assert\NotBlank(message: 'The date of birth cannot be blank')]
    #[Assert\LessThanOrEqual(value: 'today', message: 'The date of birth cannot be in the future')]
    private ?DateTimeInterface $dateDeNaissance = null;

    #[ORM\Column(name: 'email', type: 'string', length: 180, unique: true)]
    #[Assert\NotBlank(message: 'The email cannot be blank')]
    #[Assert\NotNull(message: 'The email cannot be null')]
    #[Assert\Email(message: 'The email {{ value }} is not a valid email.')]
    private string $email;

    #[ORM\Column(name: 'photo_de_profil', type: 'string', length: 255)]
    private string $photoDeProfil;

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

    #[ORM\Column(name: 'is_verified', type: 'boolean')]
    private bool $isVerified;

    public function getIsVerified(): ?bool
    {
        return $this->isVerified;
    }

    public function setIsVerified(bool $isVerified): static
    {
        $this->isVerified = $isVerified;

        return $this;
    }


    private string $plainPassword;
    public function getPlainPassword(): string
    {
        return $this->plainPassword;
    }

    public function setPlainPassword(string $plainPassword): static
    {
        $this->plainPassword = $plainPassword;

        return $this;
    }
    /**
     * Constructor
     */
    public function __construct()
    {
        $this->idCinema = new ArrayCollection();
        $this->idSeance = new ArrayCollection();
        $this->idProduit = new ArrayCollection();
    }

    public function getId(): ?int
    {
        return $this->id;
    }

    public function getNom(): ?string
    {
        return $this->nom;
    }

    public function setNom(string $nom): static
    {
        $this->nom = $nom;

        return $this;
    }

    public function getPrenom(): ?string
    {
        return $this->prenom;
    }

    public function setPrenom(string $prenom): static
    {
        $this->prenom = $prenom;

        return $this;
    }

    public function getNumTelephone(): ?int
    {
        return $this->numTelephone;
    }

    public function setNumTelephone(int $numTelephone): static
    {
        $this->numTelephone = $numTelephone;

        return $this;
    }


    public function getRole(): ?string
    {
        return $this->role;
    }

    public function setRole(string $role): static
    {
        $this->role = $role;

        return $this;
    }

    public function getAdresse(): ?string
    {
        return $this->adresse;
    }

    public function setAdresse(?string $adresse): static
    {
        $this->adresse = $adresse;

        return $this;
    }

    public function getDateDeNaissance(): ?DateTimeInterface
    {
        return $this->dateDeNaissance;
    }

    public function setDateDeNaissance(?DateTimeInterface $dateDeNaissance): static
    {
        $this->dateDeNaissance = $dateDeNaissance;

        return $this;
    }

    public function getEmail(): ?string
    {
        return $this->email;
    }

    public function setEmail(string $email): static
    {
        $this->email = $email;

        return $this;
    }

    public function getPhotoDeProfil(): ?string
    {
        return $this->photoDeProfil;
    }

    public function setPhotoDeProfil(?string $photoDeProfil): static
    {
        $this->photoDeProfil = $photoDeProfil;

        return $this;
    }

    /**
     * @return Collection<int, Cinema>
     */
    public function getIdCinema(): Collection
    {
        return $this->idCinema;
    }

    public function addIdCinema(Cinema $idCinema): static
    {
        if (!$this->idCinema->contains($idCinema)) {
            $this->idCinema->add($idCinema);
            $idCinema->addIdUser($this);
        }

        return $this;
    }

    public function removeIdCinema(Cinema $idCinema): static
    {
        if ($this->idCinema->removeElement($idCinema)) {
            $idCinema->removeIdUser($this);
        }

        return $this;
    }

    /**
     * @return Collection<int, Seance>
     */
    public function getIdSeance(): Collection
    {
        return $this->idSeance;
    }

    public function addIdSeance(Seance $idSeance): static
    {
        if (!$this->idSeance->contains($idSeance)) {
            $this->idSeance->add($idSeance);
        }

        return $this;
    }

    public function removeIdSeance(Seance $idSeance): static
    {
        $this->idSeance->removeElement($idSeance);

        return $this;
    }

    /**
     * @return Collection<int, Produit>
     */
    public function getIdProduit(): Collection
    {
        return $this->idProduit;
    }

    public function addIdProduit(Produit $idProduit): static
    {
        if (!$this->idProduit->contains($idProduit)) {
            $this->idProduit->add($idProduit);
            $idProduit->addIdClient($this);
        }

        return $this;
    }

    public function removeIdProduit(Produit $idProduit): static
    {
        if ($this->idProduit->removeElement($idProduit)) {
            $idProduit->removeIdClient($this);
        }

        return $this;
    }


    #[ORM\Column]
    private array $roles = [];



    /**
     * A visual identifier that represents this user.
     *
     * @see UserInterface
     */
    public function getUserIdentifier(): string
    {
        return (string) $this->email;
    }

    /**
     * @deprecated since Symfony 5.3, use getUserIdentifier instead
     */
    public function getUsername(): string
    {
        return (string) $this->email;
    }

    /**
     * @see UserInterface
     */
    public function getRoles(): array
    {
        $roles = $this->roles;

        $roles[] = 'ROLE_USER';

        return array_unique($roles);
    }

    public function setRoles(array $roles): static
    {
        $this->roles = $roles;

        return $this;
    }

    /**
     * @see PasswordAuthenticatedUserInterface
     */
    public function getPassword(): string
    {
        return $this->password;
    }

    public function setPassword(string $password): static
    {
        $this->password = $password;

        return $this;
    }

    /**
     * Returning a salt is only needed, if you are not using a modern
     * hashing algorithm (e.g. bcrypt or sodium) in your security.yaml.
     *
     * @see UserInterface
     */
    public function getSalt(): ?string
    {
        return null;
    }

    /**
     * @see UserInterface
     */
    public function eraseCredentials(): void
    {
        // If you store any temporary, sensitive data on the user, clear it here
        // $this->plainPassword = null;
    }

    public function isVerified(): bool
    {
        return $this->isVerified;
    }
}
