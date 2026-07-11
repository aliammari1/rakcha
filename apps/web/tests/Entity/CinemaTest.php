<?php

namespace App\Tests\Entity;

use App\Entity\Cinema;
use App\Entity\Salle;
use PHPUnit\Framework\TestCase;

/**
 * Pure unit test for the Cinema entity — no kernel / database required.
 */
final class CinemaTest extends TestCase
{
    public function testScalarAccessors(): void
    {
        $cinema = new Cinema();
        $cinema->setNom('Le Colisée');
        $cinema->setAdresse('Avenue Habib Bourguiba, Tunis');
        $cinema->setResponsable(42);
        $cinema->setLogo('colisee.png');
        $cinema->setStatut('accepted');

        self::assertSame('Le Colisée', $cinema->getNom());
        self::assertSame('Avenue Habib Bourguiba, Tunis', $cinema->getAdresse());
        self::assertSame(42, $cinema->getResponsable());
        self::assertSame('colisee.png', $cinema->getLogo());
        self::assertSame('accepted', $cinema->getStatut());
    }

    public function testCollectionsInitializeEmpty(): void
    {
        $cinema = new Cinema();

        self::assertCount(0, $cinema->getSalles());
        self::assertCount(0, $cinema->getFilms());
        self::assertCount(0, $cinema->getIdUser());
    }

    public function testAddSalleSetsOwningSide(): void
    {
        $cinema = new Cinema();
        $salle = new Salle();

        $cinema->addSalle($salle);

        self::assertCount(1, $cinema->getSalles());
        self::assertSame($cinema, $salle->getCinema());

        $cinema->removeSalle($salle);
        self::assertCount(0, $cinema->getSalles());
    }
}
