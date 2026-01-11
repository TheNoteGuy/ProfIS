package com.example.profis.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InitTables {

    public static void createTables(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {

            // Tabelle: Fachbereiche
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Fachbereiche (
                    id_fachbereich INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL
                )
            """);

            // Tabelle: Art_Abschluesse
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Art_Abschluesse (
                    id_art_abschluss INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL
                )
            """);

            // Tabelle: Studiengaenge
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Studiengaenge (
                    id_studiengang INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_fachbereich INTEGER NOT NULL,
                    titel_nach_abschluss TEXT NOT NULL,
                    id_art_abschluss INTEGER NOT NULL,
                    FOREIGN KEY (id_fachbereich) REFERENCES Fachbereiche(id_fachbereich),
                    FOREIGN KEY (id_art_abschluss) REFERENCES Art_Abschluesse(id_art_abschluss)
                )
            """);

            // Tabelle: Studenten
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Studenten (
                    matrikelnummer INTEGER PRIMARY KEY AUTOINCREMENT,
                    vorname TEXT NOT NULL,
                    nachname TEXT NOT NULL,
                    mail TEXT NOT NULL,
                    scheinfrei INTEGER,
                    id_studiengang INTEGER NOT NULL,
                    FOREIGN KEY (id_studiengang) REFERENCES Studiengaenge(id_studiengang)
                )
            """);

            // Tabelle: Referenten
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Referenten (
                    id_referent INTEGER PRIMARY KEY AUTOINCREMENT,
                    kommentar TEXT,
                    anrede TEXT CHECK(anrede IN ('Herr', 'Frau')),
                    vorname TEXT NOT NULL,
                    nachname TEXT NOT NULL,
                    ist_extern INTEGER,
                    telefon TEXT NOT NULL,
                    mail TEXT NOT NULL,
                    addresse TEXT NOT NULL
                )
            """);

            // NEU: Tabelle: Semester
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Semester (
                    id_semester INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    type TEXT CHECK(type IN ('WINTER', 'SUMMER')),
                    start_date TEXT NOT NULL,
                    end_date TEXT NOT NULL,
                    is_current INTEGER DEFAULT 0
                )
            """);

            // Tabelle: Wissenschaftliche_Arbeiten
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS Wissenschaftliche_Arbeiten (
                    id_wissenschaftliche_arbeiten INTEGER PRIMARY KEY AUTOINCREMENT,
                    matrikelnummer INTEGER NOT NULL,
                    id_erstreferent INTEGER NOT NULL,
                    id_korreferent INTEGER NOT NULL,
                    titel TEXT NOT NULL,
                    start_datum TEXT NOT NULL,
                    abgabe_datum TEXT,
                    ende_korrektur TEXT,
                    kolloquim_beginn TEXT,
                    kolloquim_ende TEXT,
                    note_arbeit_referent REAL,
                    note_arbeit_korreferent REAL,
                    note_kolloquium_referent REAL,
                    note_kolloquium_korreferent REAL,
                    FOREIGN KEY (matrikelnummer) REFERENCES Studenten(matrikelnummer),
                    FOREIGN KEY (id_erstreferent) REFERENCES Referenten(id_referent),
                    FOREIGN KEY (id_korreferent) REFERENCES Referenten(id_referent)
                )
            """);

            // Testdaten einfügen, falls Tabellen leer sind
            insertTestDataIfNeeded(connection);
        }
    }

    private static void insertTestDataIfNeeded(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Prüfen ob bereits Daten vorhanden sind
            var rs = stmt.executeQuery("SELECT COUNT(*) FROM Fachbereiche");
            rs.next();
            if (rs.getInt(1) == 0) {
                // Fachbereiche
                stmt.execute("INSERT INTO Fachbereiche (name) VALUES ('Informatik')");
                stmt.execute("INSERT INTO Fachbereiche (name) VALUES ('Wirtschaft')");

                // Art_Abschluesse
                stmt.execute("INSERT INTO Art_Abschluesse (name) VALUES ('Bachelor')");
                stmt.execute("INSERT INTO Art_Abschluesse (name) VALUES ('Master')");

                // Studiengänge
                stmt.execute("INSERT INTO Studiengaenge (id_fachbereich, titel_nach_abschluss, id_art_abschluss) VALUES (1, 'BSc Wirtschaftsinformatik', 1)");
                stmt.execute("INSERT INTO Studiengaenge (id_fachbereich, titel_nach_abschluss, id_art_abschluss) VALUES (1, 'MSc Wirtschaftsinformatik', 2)");

                // Referenten
                stmt.execute("""
                    INSERT INTO Referenten (anrede, vorname, nachname, ist_extern, telefon, mail, addresse, kommentar) 
                    VALUES ('Herr', 'Max', 'Grüne', 0, '0641-309-1234', 'max.gruene@th-mittelhessen.de', 'THM Campus Gießen', 'Professor für Informatik')
                """);
                stmt.execute("""
                    INSERT INTO Referenten (anrede, vorname, nachname, ist_extern, telefon, mail, addresse, kommentar) 
                    VALUES ('Frau', 'Anna', 'Müller', 0, '0641-309-5678', 'anna.mueller@th-mittelhessen.de', 'THM Campus Gießen', 'Professorin für Wirtschaftsinformatik')
                """);

                // Semester
                stmt.execute("INSERT INTO Semester (name, type, start_date, end_date, is_current) VALUES ('WS 2024/25', 'WINTER', '2024-10-01', '2025-03-31', 1)");
                stmt.execute("INSERT INTO Semester (name, type, start_date, end_date, is_current) VALUES ('SS 2025', 'SUMMER', '2025-04-01', '2025-09-30', 0)");
            }
            rs.close();
        }
    }

    public static void dropTables(Connection connection) throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS Wissenschaftliche_Arbeiten");
            stmt.execute("DROP TABLE IF EXISTS Studenten");
            stmt.execute("DROP TABLE IF EXISTS Referenten");
            stmt.execute("DROP TABLE IF EXISTS Studiengaenge");
            stmt.execute("DROP TABLE IF EXISTS Art_Abschluesse");
            stmt.execute("DROP TABLE IF EXISTS Fachbereiche");
            stmt.execute("DROP TABLE IF EXISTS Semester");
        }
    }
}