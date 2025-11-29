package com.example.profis.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import lombok.Getter;

public class Connector {

    private String dbPath;

    private Connection connection;

    public Connection getConnection() {
        return connection;
    }

    public Connector(String dbPath) {
        this.dbPath = dbPath;
    }

    public void connect() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:sqlite:" + dbPath;
            connection = DriverManager.getConnection(url);
            connection.setAutoCommit(true);
            var statement = connection.createStatement();
            statement.execute("PRAGMA foreign_keys = ON;");
            statement.execute("PRAGMA journal_mode = WAL;");
            statement.close();
        }
    }

    public void disconnect() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public boolean isConnected() throws SQLException {
        return connection != null && !connection.isClosed();
    }
}