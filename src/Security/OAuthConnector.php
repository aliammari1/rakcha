<?php

namespace App\Security;

use Doctrine\ORM\EntityManagerInterface;
use HWI\Bundle\OAuthBundle\Connect\AccountConnectorInterface;
use HWI\Bundle\OAuthBundle\OAuth\Response\UserResponseInterface;
use Symfony\Component\PropertyAccess\PropertyAccessor;
use Symfony\Component\Security\Core\User\UserInterface;

final class OAuthConnector implements AccountConnectorInterface
{
    public function __construct(
        private readonly EntityManagerInterface $entityManager,
        private readonly array $properties
    ) {
    }

    public function connect(UserInterface $user, UserResponseInterface $response)
    {
        if (!isset($this->properties[$response->getResourceOwner()->getName()])) {
            return;
        }

        $property = new PropertyAccessor();
        $property->setValue($user, $this->properties[$response->getResourceOwner()->getName()], $response->getUserIdentifier());

        $this->entityManager->persist($user);
        $this->entityManager->flush();
    }
}
