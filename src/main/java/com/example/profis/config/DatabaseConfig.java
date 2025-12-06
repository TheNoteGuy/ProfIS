package com.example.profis.config;

import com.example.profis.database.Connector;
import com.example.profis.database.InitTables;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class DatabaseConfig {

    @Bean
    public Connection databaseConnection() throws SQLException {
        Connector connector = new Connector("profis.db");
        connector.connect();

        InitTables.createTables(connector.getConnection());

        return connector.getConnection();
    }
}