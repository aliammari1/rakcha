<?php

namespace App\Tests\Entity;

use App\Entity\Category;
use App\Entity\Film;
use PHPUnit\Framework\TestCase;

/**
 * Pure unit test for the Category entity — no kernel / database required.
 */
final class CategoryTest extends TestCase
{
    public function testAccessors(): void
    {
        $category = new Category();
        $category->setNom('Action');
        $category->setDescription('High-octane films');

        self::assertSame('Action', $category->getNom());
        self::assertSame('High-octane films', $category->getDescription());
        self::assertCount(0, $category->getFilms());
    }

    public function testFluentSettersReturnSelf(): void
    {
        $category = new Category();

        self::assertSame($category, $category->setNom('Drama'));
        self::assertSame($category, $category->setDescription('Tearjerkers'));
    }

    public function testAddingAFilmIsIdempotentAndBidirectional(): void
    {
        $category = new Category();
        $film = new Film();

        $category->addFilm($film);
        $category->addFilm($film); // adding twice must not duplicate

        self::assertCount(1, $category->getFilms());
        self::assertTrue($category->getFilms()->contains($film));
        // The inverse side is kept in sync by addFilm().
        self::assertTrue($film->getCategorys()->contains($category));
    }

    public function testRemovingAFilmDetachesBothSides(): void
    {
        $category = new Category();
        $film = new Film();

        $category->addFilm($film);
        $category->removeFilm($film);

        self::assertCount(0, $category->getFilms());
        self::assertFalse($film->getCategorys()->contains($category));
    }
}
