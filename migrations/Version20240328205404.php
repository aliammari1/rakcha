<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20240328205404 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        // $this->addSql('ALTER TABLE series DROP FOREIGN KEY FK_3A10012D37667FC1');
        $this->addSql('ALTER TABLE series CHANGE idcategorie idcategorie INT NOT NULL, CHANGE liked liked INT NOT NULL, CHANGE nbLikes nbLikes INT NOT NULL, CHANGE disliked disliked INT NOT NULL, CHANGE nbDislikes nbDislikes INT NOT NULL');
        //$this->addSql('ALTER TABLE users ADD is_verified TINYINT(1) NOT NULL, CHANGE password password VARCHAR(255) NOT NULL, CHANGE adresse adresse VARCHAR(50) NOT NULL, CHANGE date_de_naissance date_de_naissance DATE NOT NULL, CHANGE photo_de_profil photo_de_profil VARCHAR(255) NOT NULL');
        // $this->addSql('CREATE UNIQUE INDEX UNIQ_1483A5E9E7927C74 ON users (email)');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE series CHANGE liked liked INT DEFAULT NULL, CHANGE nbLikes nbLikes INT DEFAULT NULL, CHANGE disliked disliked INT DEFAULT NULL, CHANGE nbDislikes nbDislikes INT DEFAULT NULL, CHANGE idcategorie idcategorie INT DEFAULT NULL');
        $this->addSql('ALTER TABLE series ADD CONSTRAINT FK_3A10012D37667FC1 FOREIGN KEY (idcategorie) REFERENCES category (id)');
        $this->addSql('DROP INDEX UNIQ_1483A5E9E7927C74 ON users');
        $this->addSql('ALTER TABLE users DROP is_verified, CHANGE password password VARCHAR(50) NOT NULL, CHANGE adresse adresse VARCHAR(50) DEFAULT NULL, CHANGE date_de_naissance date_de_naissance DATE DEFAULT NULL, CHANGE photo_de_profil photo_de_profil VARCHAR(255) DEFAULT NULL');
    }
}
