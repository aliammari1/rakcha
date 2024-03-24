<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20240324071802 extends AbstractMigration
{
    public function getDescription(): string
    {
        return '';
    }

    public function up(Schema $schema): void
    {
        // this up() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE commandeitem CHANGE id_produit id_produit INT DEFAULT NULL, CHANGE idCommande idCommande INT DEFAULT NULL');
        $this->addSql('ALTER TABLE commentaire CHANGE idClient idClient INT DEFAULT NULL');
        $this->addSql('ALTER TABLE episodes CHANGE idserie idserie INT DEFAULT NULL');
        $this->addSql('ALTER TABLE evenement DROP FOREIGN KEY evenement_ibfk_1');
        $this->addSql('ALTER TABLE evenement CHANGE id_categorie id_categorie INT DEFAULT NULL, CHANGE affiche_event affiche_event VARCHAR(255) NOT NULL');
        $this->addSql('ALTER TABLE evenement ADD CONSTRAINT FK_B26681EC9486A13 FOREIGN KEY (id_categorie) REFERENCES categorie_evenement (ID)');
        $this->addSql('ALTER TABLE feedback_evenement CHANGE id_evenement id_evenement INT DEFAULT NULL, CHANGE id_user id_user INT DEFAULT NULL');
        $this->addSql('ALTER TABLE participation_evenement DROP FOREIGN KEY participation_evenement_ibfk_1');
        $this->addSql('ALTER TABLE participation_evenement CHANGE id_evenement id_evenement INT DEFAULT NULL');
        $this->addSql('ALTER TABLE participation_evenement ADD CONSTRAINT FK_65A146758B13D439 FOREIGN KEY (id_evenement) REFERENCES evenement (ID)');
        $this->addSql('ALTER TABLE produit CHANGE id_categorieProduit id_categorieProduit INT DEFAULT NULL, CHANGE image image VARCHAR(255) NOT NULL');
        $this->addSql('ALTER TABLE panier DROP quantity');
        $this->addSql('ALTER TABLE panier RENAME INDEX fk_client TO IDX_24CC0DF2E173B1B8');
        $this->addSql('ALTER TABLE salle CHANGE id_cinema id_cinema INT DEFAULT NULL');
        $this->addSql('ALTER TABLE seance CHANGE id_cinema id_cinema INT DEFAULT NULL, CHANGE id_film id_film INT DEFAULT NULL, CHANGE id_salle id_salle INT DEFAULT NULL');
        $this->addSql('ALTER TABLE sponsor CHANGE Logo Logo VARCHAR(255) NOT NULL');
        $this->addSql('ALTER TABLE ticket DROP nbrdeplace, DROP prix');
        $this->addSql('ALTER TABLE ticket RENAME INDEX fk_user_seance TO IDX_97A0ADA3F94A48E3');
    }

    public function down(Schema $schema): void
    {
        // this down() migration is auto-generated, please modify it to your needs
        $this->addSql('ALTER TABLE produit CHANGE image image BLOB NOT NULL, CHANGE id_categorieProduit id_categorieProduit INT NOT NULL');
        $this->addSql('ALTER TABLE episodes CHANGE idserie idserie INT NOT NULL');
        $this->addSql('ALTER TABLE salle CHANGE id_cinema id_cinema INT NOT NULL');
        $this->addSql('ALTER TABLE commandeitem CHANGE id_produit id_produit INT NOT NULL, CHANGE idCommande idCommande INT NOT NULL');
        $this->addSql('ALTER TABLE seance CHANGE id_film id_film INT NOT NULL, CHANGE id_salle id_salle INT NOT NULL, CHANGE id_cinema id_cinema INT NOT NULL');
        $this->addSql('ALTER TABLE ticket ADD nbrdeplace INT DEFAULT NULL, ADD prix DOUBLE PRECISION DEFAULT NULL');
        $this->addSql('ALTER TABLE ticket RENAME INDEX idx_97a0ada3f94a48e3 TO fk_user_seance');
        $this->addSql('ALTER TABLE commentaire CHANGE idClient idClient INT NOT NULL');
        $this->addSql('ALTER TABLE evenement DROP FOREIGN KEY FK_B26681EC9486A13');
        $this->addSql('ALTER TABLE evenement CHANGE id_categorie id_categorie INT NOT NULL, CHANGE affiche_event affiche_event BLOB NOT NULL');
        $this->addSql('ALTER TABLE evenement ADD CONSTRAINT evenement_ibfk_1 FOREIGN KEY (id_categorie) REFERENCES categorie_evenement (ID) ON UPDATE CASCADE ON DELETE CASCADE');
        $this->addSql('ALTER TABLE panier ADD quantity INT NOT NULL');
        $this->addSql('ALTER TABLE panier RENAME INDEX idx_24cc0df2e173b1b8 TO FK_client');
        $this->addSql('ALTER TABLE participation_evenement DROP FOREIGN KEY FK_65A146758B13D439');
        $this->addSql('ALTER TABLE participation_evenement CHANGE id_evenement id_evenement INT NOT NULL');
        $this->addSql('ALTER TABLE participation_evenement ADD CONSTRAINT participation_evenement_ibfk_1 FOREIGN KEY (id_evenement) REFERENCES evenement (ID) ON UPDATE CASCADE ON DELETE CASCADE');
        $this->addSql('ALTER TABLE sponsor CHANGE Logo Logo BLOB NOT NULL');
        $this->addSql('ALTER TABLE feedback_evenement CHANGE id_user id_user INT NOT NULL, CHANGE id_evenement id_evenement INT NOT NULL');
    }
}
