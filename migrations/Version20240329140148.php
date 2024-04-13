<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20240329140148 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE users DROP is_verified, CHANGE password password VARCHAR(50) NOT NULL, CHANGE adresse adresse VARCHAR(50) DEFAULT NULL, CHANGE date_de_naissance date_de_naissance DATE DEFAULT NULL, CHANGE photo_de_profil photo_de_profil VARCHAR(255) DEFAULT NULL');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE users ADD is_verified TINYINT(1) NOT NULL, CHANGE password password VARCHAR(255) NOT NULL, CHANGE adresse adresse VARCHAR(50) NOT NULL, CHANGE date_de_naissance date_de_naissance DATE NOT NULL, CHANGE photo_de_profil photo_de_profil VARCHAR(255) NOT NULL');
    }
}
