package com.cmpn306.database;

import org.sqlite.SQLiteDataSource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public enum Database {
    INSTANCE;

    private final String           SCHEMA_PATH   = "./schema.sql";
    private final String           DATABASE_NAME = "searchEngineDatabase.db";
    private final SQLiteDataSource dataSource    = new SQLiteDataSource();

    Database() {
        dataSource.setUrl("jdbc:sqlite:" + DATABASE_NAME);
        try {
            createTables();
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void createTables() throws IOException, SQLException {
        try (
                Connection connection = dataSource.getConnection();
                Statement stmt = connection.createStatement()
        ) {
            String[] tokens = Files.readString(Path.of(SCHEMA_PATH)).split(";");

            for (String token: tokens) {
                if (!token.isBlank())
                    stmt.addBatch(token);
            }
            stmt.executeBatch();
        }
    }

    ResultSet query(String query) throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                Statement stmt = connection.createStatement()
        ) {
            return stmt.executeQuery(query);
        }
    }

    void update(String query) throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                Statement stmt = connection.createStatement()
        ) {
            stmt.executeUpdate(query);
        }
    }
}
