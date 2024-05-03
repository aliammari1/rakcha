<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20240502191604 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE filmcinema DROP FOREIGN KEY FK_C7D71678964A220');
        $this->addSql('ALTER TABLE filmcinema DROP FOREIGN KEY FK_C7D71678F2B4B159');
        $this->addSql('DROP TABLE filmcinema');
        $this->addSql('ALTER TABLE seance DROP FOREIGN KEY FK_DF7DFD0E964A220');
        $this->addSql('ALTER TABLE seance ADD CONSTRAINT FK_DF7DFD0E964A220 FOREIGN KEY (id_film) REFERENCES film (id)');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE filmcinema (id_film INT NOT NULL, id_cinema INT NOT NULL, INDEX fk_fc1 (id_film), INDEX fk_fc2 (id_cinema), PRIMARY KEY(id_film, id_cinema)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('ALTER TABLE filmcinema ADD CONSTRAINT FK_C7D71678964A220 FOREIGN KEY (id_film) REFERENCES film (id)');
        $this->addSql('ALTER TABLE filmcinema ADD CONSTRAINT FK_C7D71678F2B4B159 FOREIGN KEY (id_cinema) REFERENCES cinema (id_cinema)');
        $this->addSql('ALTER TABLE seance DROP FOREIGN KEY FK_DF7DFD0E964A220');
        $this->addSql('ALTER TABLE seance ADD CONSTRAINT FK_DF7DFD0E964A220 FOREIGN KEY (id_film) REFERENCES film (id) ON UPDATE CASCADE ON DELETE CASCADE');
    }
}
