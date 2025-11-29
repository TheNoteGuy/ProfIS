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
        testDatabase();
        
        SpringApplication.run(ProfIsApplication.class, args);
    }
    
    private static void testDatabase() {
        Connector connector = new Connector("test.db");
        
        try {
            connector.connect();
            System.out.println("✓ Verbindung hergestellt");
            
            InitTables.createTables(connector.getConnection());
            System.out.println("✓ Tabellen erstellt");
            
            // Test-Insert
            Statement stmt = connector.getConnection().createStatement();
            stmt.execute("INSERT INTO Fachbereiche (name) VALUES ('Informatik')");
            
            // Test-Select
            ResultSet rs = stmt.executeQuery("SELECT * FROM Fachbereiche");
            if (rs.next()) {
                System.out.println("✓ Test erfolgreich: " + rs.getString("name"));
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