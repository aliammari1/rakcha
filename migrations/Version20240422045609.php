<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20240422045609 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE friendships (id INT AUTO_INCREMENT NOT NULL, receiver_id INT DEFAULT NULL, sender_id INT DEFAULT NULL, INDEX IDX_E0A8B7CACD53EDB6 (receiver_id), INDEX IDX_E0A8B7CAF624B39D (sender_id), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB');
        $this->addSql('ALTER TABLE friendships ADD CONSTRAINT FK_E0A8B7CACD53EDB6 FOREIGN KEY (receiver_id) REFERENCES users (id)');
        $this->addSql('ALTER TABLE friendships ADD CONSTRAINT FK_E0A8B7CAF624B39D FOREIGN KEY (sender_id) REFERENCES users (id)');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE friendships DROP FOREIGN KEY FK_E0A8B7CACD53EDB6');
        $this->addSql('ALTER TABLE friendships DROP FOREIGN KEY FK_E0A8B7CAF624B39D');
        $this->addSql('DROP TABLE friendships');
    }
}
