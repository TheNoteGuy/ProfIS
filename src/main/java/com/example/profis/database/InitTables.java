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
        }
    }
}