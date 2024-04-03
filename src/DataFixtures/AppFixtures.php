<?php

namespace App\DataFixtures;

use App\Factory\UsersFactory;
use Doctrine\Bundle\FixturesBundle\Fixture;
use Doctrine\Persistence\ObjectManager;

class AppFixtures extends Fixture
{
    public function load(ObjectManager $manager): void
    {
        // $product = new Product();
        // $manager->persist($product);
        UsersFactory::createOne(['email' => 'client@gmail.com', 'role' => 'admin']);
        UsersFactory::createOne(['email' => 'admin@gmail.com', 'role' => 'client']);
        UsersFactory::createMany(1000);
        $manager->flush();
    }
}
