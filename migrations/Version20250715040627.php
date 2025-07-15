<?php

declare(strict_types=1);

namespace DoctrineMigrations;

use Doctrine\DBAL\Schema\Schema;
use Doctrine\Migrations\AbstractMigration;

/**
 * Auto-generated Migration: Please modify to your needs!
 */
final class Version20250715040627 extends AbstractMigration
{
    public function getDescription(): string
    {
        return 'Fix JSON encoding issue for users roles column';
    }

    public function up(Schema $schema): void
    {
        // Fix the double-encoded JSON issue in the roles column
        // The problem is that roles are stored as strings like "[\"ROLE_CLIENT\"]" 
        // but should be proper JSON arrays like ["ROLE_CLIENT"]

        // First, let's check if we're dealing with SQLite or MySQL
        $platform = $this->connection->getDatabasePlatform()->getName();

        if ($platform === 'sqlite') {
            // For SQLite, we need to decode and re-encode properly
            $this->addSql("
                UPDATE users 
                SET roles = CASE 
                    WHEN roles LIKE '[\\\"%' THEN json_array('ROLE_CLIENT')
                    WHEN roles LIKE '%ROLE_ADMIN%' THEN json_array('ROLE_ADMIN')
                    WHEN roles LIKE '%ROLE_RESPONSABLE%' THEN json_array('ROLE_RESPONSABLE_DE_CINEMA')
                    ELSE json_array('ROLE_USER')
                END
            ");
        } else {
            // For MySQL/MariaDB
            $this->addSql("
                UPDATE users 
                SET roles = CASE 
                    WHEN roles LIKE '[\\\"%ROLE_CLIENT%' THEN JSON_ARRAY('ROLE_CLIENT')
                    WHEN roles LIKE '%ROLE_ADMIN%' THEN JSON_ARRAY('ROLE_ADMIN') 
                    WHEN roles LIKE '%ROLE_RESPONSABLE%' THEN JSON_ARRAY('ROLE_RESPONSABLE_DE_CINEMA')
                    ELSE JSON_ARRAY('ROLE_USER')
                END
            ");
        }
    }

    public function down(Schema $schema): void
    {
        // This migration fixes data corruption, so no down migration is safe
        $this->addSql('-- No safe down migration for fixing data corruption');
    }
}
