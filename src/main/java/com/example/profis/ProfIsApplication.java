package com.example.profis;

import com.example.profis.database.Connector;
import com.example.profis.database.InitTables;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@SpringBootApplication
public class ProfIsApplication {

    public static void main(String[] args) {
        // Test der SQLite Datenbank
        //testDatabase();
        
        SpringApplication.run(ProfIsApplication.class, args);
    }

    private static void testDatabase() {
        Connector connector = new Connector("profis.db");

        try {
            connector.connect();
            System.out.println("✓ Verbindung hergestellt");

            InitTables.createTables(connector.getConnection());
            System.out.println("✓ Tabellen erstellt");

            Statement stmt = connector.getConnection().createStatement();

            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM Fachbereiche");
            rs.next();
            if (rs.getInt(1) == 0) {
                stmt.execute("INSERT INTO Fachbereiche (name) VALUES ('Informatik')");
                stmt.execute("INSERT INTO Art_Abschluesse (name) VALUES ('Bachelor')");
                stmt.execute("INSERT INTO Art_Abschluesse (name) VALUES ('Master')");
                stmt.execute("INSERT INTO Studiengaenge (id_fachbereich, titel_nach_abschluss, id_art_abschluss) VALUES (1, 'BSc Wirtschaftsinformatik', 1)");
                System.out.println("✓ Testdaten eingefügt");
            }

            rs.close();
            stmt.close();
            connector.disconnect();
            System.out.println("✓ Verbindung geschlossen");

        } catch (SQLException e) {
            System.err.println("✗ Fehler: " + e.getMessage());
            e.printStackTrace();
        }
    }
}