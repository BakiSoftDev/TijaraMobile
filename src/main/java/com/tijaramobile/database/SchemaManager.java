package com.tijaramobile.database;

import java.sql.Connection;
import java.sql.Statement;

public class SchemaManager {

    public static void initializeDatabase() {
        String createProduitTable = """
            CREATE TABLE IF NOT EXISTS produit (
                idP INTEGER PRIMARY KEY AUTOINCREMENT,
                nomP TEXT,
                ref TEXT,
                unit TEXT,
                prixA REAL NOT NULL DEFAULT '0.00',
                prixV REAL NOT NULL DEFAULT '0.00',
                qnt REAL NOT NULL DEFAULT '0.000',
                img TEXT,
                packPrix REAL DEFAULT '0.00',
                packNb INTEGER
            )
            """;

        String createClientTable = """
            CREATE TABLE IF NOT EXISTS client (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                phone TEXT,
                email TEXT,
                address TEXT,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        String createFournisseurTable = """
            CREATE TABLE IF NOT EXISTS fournisseur (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                name TEXT,
                phone TEXT,
                email TEXT,
                address TEXT,
                tva_exempt INTEGER DEFAULT 0,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        String createCmdTable = """
            CREATE TABLE IF NOT EXISTS cmd (
                idcmd INTEGER PRIMARY KEY AUTOINCREMENT,
                idclient INTEGER NOT NULL DEFAULT 0,
                num TEXT NOT NULL DEFAULT '0',
                code TEXT NOT NULL DEFAULT '0',
                tot REAL NOT NULL DEFAULT '0.00',
                init_paye REAL NOT NULL DEFAULT '0.00',
                paye REAL NOT NULL DEFAULT '0.00',
                created_by INTEGER,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                id_session INTEGER
            )
            """;

        String createCmdLineTable = """
            CREATE TABLE IF NOT EXISTS cmdline (
                idL INTEGER PRIMARY KEY AUTOINCREMENT,
                idcmd INTEGER NOT NULL,
                idprod INTEGER NOT NULL,
                qnt REAL NOT NULL DEFAULT '0.000',
                mode TEXT NOT NULL DEFAULT 'U',
                pAchat REAL NOT NULL DEFAULT '0.00',
                pVente REAL NOT NULL DEFAULT '0.00',
                FOREIGN KEY (idcmd) REFERENCES cmd (idcmd) ON UPDATE RESTRICT ON DELETE RESTRICT
            )
            """;

        String createFacAchatTable = """
            CREATE TABLE IF NOT EXISTS facachat (
                idfa INTEGER PRIMARY KEY AUTOINCREMENT,
                idfour INTEGER,
                num TEXT,
                code TEXT,
                dt DATE DEFAULT CURRENT_TIMESTAMP,
                tot_ht REAL DEFAULT '0.00',
                tot_tva REAL DEFAULT '0.00',
                tot_ttc REAL DEFAULT '0.00',
                tva INTEGER DEFAULT 1,
                init_paye REAL DEFAULT '0.00',
                paye REAL DEFAULT '0.00',
                created_by INTEGER,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        String createAchatLineTable = """
            CREATE TABLE IF NOT EXISTS achatline (
                idL INTEGER PRIMARY KEY AUTOINCREMENT,
                idfacAchat INTEGER NOT NULL,
                idprod INTEGER NOT NULL,
                qnt REAL NOT NULL DEFAULT '0.000',
                mode TEXT NOT NULL DEFAULT 'U',
                pAchat REAL NOT NULL,
                tot REAL NOT NULL,
                FOREIGN KEY (idfacAchat) REFERENCES facachat (idfa) ON UPDATE RESTRICT ON DELETE CASCADE,
                FOREIGN KEY (idprod) REFERENCES produit (idP) ON UPDATE RESTRICT ON DELETE CASCADE
            )
            """;

        String createVersementTable = """
            CREATE TABLE IF NOT EXISTS versement (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                id_entity INTEGER NOT NULL,
                entity_type TEXT NOT NULL,
                id_document INTEGER NOT NULL,
                document_type TEXT NOT NULL,
                amount REAL NOT NULL DEFAULT '0.00',
                payment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                payment_method TEXT DEFAULT 'Cash',
                reference TEXT,
                notes TEXT,
                created_by INTEGER,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
            """;

        String createUsersTable = """
            CREATE TABLE IF NOT EXISTS users (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                full_name TEXT,
                password TEXT NOT NULL,
                email TEXT
            )
            """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(createProduitTable);
            stmt.execute(createClientTable);
            stmt.execute(createFournisseurTable);
            stmt.execute(createCmdTable);
            stmt.execute(createCmdLineTable);
            stmt.execute(createFacAchatTable);
            stmt.execute(createAchatLineTable);
            stmt.execute(createVersementTable);
            stmt.execute(createUsersTable);

            // Migration: add email column if missing (handles old DBs)
            try { stmt.execute("ALTER TABLE users ADD COLUMN email TEXT"); }
            catch (Exception ignored) { /* column already exists */ }

            insertDefaultAdmin(conn);

            System.out.println("Database initialized successfully at: " + DatabaseConnection.getDbPath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void insertDefaultAdmin(Connection conn) {
        String checkAdmin = "SELECT COUNT(*) FROM users WHERE username = 'admin'";
        String insertAdmin = """
            INSERT INTO users (username, password, full_name, email) 
            VALUES ('admin', '1234', 'Administrator', 'admin@example.com')
            """;

        try (var stmt = conn.createStatement();
             var rs = stmt.executeQuery(checkAdmin)) {

            if (rs.next() && rs.getInt(1) == 0) {
                stmt.executeUpdate(insertAdmin);
                System.out.println("Default admin user created");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}