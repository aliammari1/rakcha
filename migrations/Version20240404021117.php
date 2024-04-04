<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20240404021117 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE users DROP google, CHANGE num_telephone num_telephone INT DEFAULT NULL, CHANGE adresse adresse VARCHAR(50) DEFAULT NULL, CHANGE date_de_naissance date_de_naissance DATE DEFAULT NULL');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE users ADD google VARCHAR(255) DEFAULT NULL, CHANGE num_telephone num_telephone INT NOT NULL, CHANGE adresse adresse VARCHAR(50) NOT NULL, CHANGE date_de_naissance date_de_naissance DATE NOT NULL');
    }
}
