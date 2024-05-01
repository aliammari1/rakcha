<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20240501104445 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        //  $this->addSql('DROP TABLE commentaire_produit');
        //  $this->addSql('ALTER TABLE panier ADD CONSTRAINT FK_24CC0DF2E173B1B8 FOREIGN KEY (id_client) REFERENCES users (id)');
        $this->addSql('CREATE INDEX IDX_24CC0DF2E173B1B8 ON panier (id_client)');
        $this->addSql('ALTER TABLE panier RENAME INDEX fk_pro TO IDX_24CC0DF2F7384557');
        $this->addSql('ALTER TABLE seat DROP FOREIGN KEY FK_3D5C3666A0123F6C');
        $this->addSql('ALTER TABLE seat ADD CONSTRAINT FK_3D5C3666A0123F6C FOREIGN KEY (id_salle) REFERENCES salle (id_salle)');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('CREATE TABLE commentaire_produit (id INT AUTO_INCREMENT NOT NULL, id_client_id INT DEFAULT NULL, commentaire VARCHAR(255) CHARACTER SET utf8mb4 NOT NULL COLLATE `utf8mb4_unicode_ci`, id_produit INT DEFAULT NULL, INDEX IDX_5A6D7E7499DED506 (id_client_id), INDEX IDX_5A6D7E74F7384557 (id_produit), PRIMARY KEY(id)) DEFAULT CHARACTER SET utf8mb4 COLLATE `utf8mb4_unicode_ci` ENGINE = InnoDB COMMENT = \'\' ');
        $this->addSql('ALTER TABLE panier DROP FOREIGN KEY FK_24CC0DF2E173B1B8');
        $this->addSql('DROP INDEX IDX_24CC0DF2E173B1B8 ON panier');
        $this->addSql('ALTER TABLE panier RENAME INDEX idx_24cc0df2f7384557 TO FK_pro');
        $this->addSql('ALTER TABLE seat DROP FOREIGN KEY FK_3D5C3666A0123F6C');
        $this->addSql('ALTER TABLE seat ADD CONSTRAINT FK_3D5C3666A0123F6C FOREIGN KEY (id_salle) REFERENCES salle (id_salle) ON UPDATE CASCADE ON DELETE CASCADE');
    }
}
