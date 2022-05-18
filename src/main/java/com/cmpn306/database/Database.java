package com.cmpn306.database;

import org.sqlite.SQLiteDataSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Arrays;

public enum Database {
    INSTANCE;

    private final String           SCHEMA_PATH   = "./schema.sql";
    private final String           DATABASE_NAME = "searchEngineDatabase.db";
    private final SQLiteDataSource dataSource    = new SQLiteDataSource();

    Database() {
        dataSource.setUrl("jdbc:sqlite:" + DATABASE_NAME);
        createTables();
    }

    void createTables() {
        try {
            String[] tokens = Arrays
                    .stream(Files.readString(Path.of(SCHEMA_PATH), StandardCharsets.UTF_8).split(";"))
                    .filter(s -> !s.isBlank())
                    .toArray(String[]::new);
            updateBatch(tokens);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
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

    int update(String query) throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                Statement stmt = connection.createStatement()
        ) {
            return stmt.executeUpdate(query);
        }
    }

    int[] updateBatch(String[] queries) throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                Statement stmt = connection.createStatement()
        ) {
            for (String query: queries)
                stmt.addBatch(query);
            return stmt.executeBatch();
        }
    }
}
