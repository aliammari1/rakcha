<?php

namespace App\Factory;

use App\Entity\Users;
use App\Repository\UsersRepository;
use Symfony\Component\PasswordHasher\Hasher\UserPasswordHasherInterface;
use Zenstruck\Foundry\ModelFactory;
use Zenstruck\Foundry\Proxy;
use Zenstruck\Foundry\RepositoryProxy;

/**
 * @extends ModelFactory<Users>
 *
 * @method        Users|Proxy create(array|callable $attributes = [])
 * @method static Users|Proxy createOne(array $attributes = [])
 * @method static Users|Proxy find(object|array|mixed $criteria)
 * @method static Users|Proxy findOrCreate(array $attributes)
 * @method static Users|Proxy first(string $sortedField = 'id')
 * @method static Users|Proxy last(string $sortedField = 'id')
 * @method static Users|Proxy random(array $attributes = [])
 * @method static Users|Proxy randomOrCreate(array $attributes = [])
 * @method static UsersRepository|RepositoryProxy repository()
 * @method static Users[]|Proxy[] all()
 * @method static Users[]|Proxy[] createMany(int $number, array|callable $attributes = [])
 * @method static Users[]|Proxy[] createSequence(iterable|callable $sequence)
 * @method static Users[]|Proxy[] findBy(array $attributes)
 * @method static Users[]|Proxy[] randomRange(int $min, int $max, array $attributes = [])
 * @method static Users[]|Proxy[] randomSet(int $number, array $attributes = [])
 *
 * @phpstan-method        Proxy<Users> create(array|callable $attributes = [])
 * @phpstan-method static Proxy<Users> createOne(array $attributes = [])
 * @phpstan-method static Proxy<Users> find(object|array|mixed $criteria)
 * @phpstan-method static Proxy<Users> findOrCreate(array $attributes)
 * @phpstan-method static Proxy<Users> first(string $sortedField = 'id')
 * @phpstan-method static Proxy<Users> last(string $sortedField = 'id')
 * @phpstan-method static Proxy<Users> random(array $attributes = [])
 * @phpstan-method static Proxy<Users> randomOrCreate(array $attributes = [])
 * @phpstan-method static RepositoryProxy<Users> repository()
 * @phpstan-method static list<Proxy<Users>> all()
 * @phpstan-method static list<Proxy<Users>> createMany(int $number, array|callable $attributes = [])
 * @phpstan-method static list<Proxy<Users>> createSequence(iterable|callable $sequence)
 * @phpstan-method static list<Proxy<Users>> findBy(array $attributes)
 * @phpstan-method static list<Proxy<Users>> randomRange(int $min, int $max, array $attributes = [])
 * @phpstan-method static list<Proxy<Users>> randomSet(int $number, array $attributes = [])
 */
final class UsersFactory extends ModelFactory
{

    private UserPasswordHasherInterface $passwordHasher;
    /**
     * @see https://symfony.com/bundles/ZenstruckFoundryBundle/current/index.html#factories-as-services
     *
     * @todo inject services if required
     */
    public function __construct(UserPasswordHasherInterface $passwordHasher)
    {
        parent::__construct();
        $this->passwordHasher = $passwordHasher;
    }

    /**
     * @see https://symfony.com/bundles/ZenstruckFoundryBundle/current/index.html#model-factories
     *
     * @todo add your default values here
     */
    protected function getDefaults(): array
    {
        return [
            'adresse' => self::faker()->address(),
            'dateDeNaissance' => self::faker()->dateTime(),
            'email' => self::faker()->email(),
            'isVerified' => self::faker()->boolean(0),
            'nom' => self::faker()->firstName(),
            'numTelephone' => intval(self::faker()->phoneNumber()),
            'plainPassword' => "tada",
            'photoDeProfil' => self::faker()->image("./public/img/users", format: 'jpg'),
            'prenom' => self::faker()->lastName(50),
            'role' => "client",
            'roles' => ["ROLE_USER"],
        ];
    }

    /**
     * @see https://symfony.com/bundles/ZenstruckFoundryBundle/current/index.html#initialization
     */
    protected function initialize(): self
    {
        return $this
            ->afterInstantiate(function (Users $user): void {
                if ($user->getPlainPassword()) {
                    $user->setPassword($this->passwordHasher->hashPassword($user, $user->getPlainPassword()));
                }
            })
        ;
    }

    protected static function getClass(): string
    {
        return Users::class;
    }
}
