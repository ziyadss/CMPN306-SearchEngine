package com.cmpn306.database;

import org.sqlite.SQLiteDataSource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;

public enum Database {
    INSTANCE;

    private final String           SCHEMA_PATH   = "./schema.sql";
    private final String           DATABASE_NAME = "searchEngineDatabase.db";
    private final SQLiteDataSource dataSource    = new SQLiteDataSource();

    Database() {
        dataSource.setUrl("jdbc:sqlite:" + DATABASE_NAME);
        createTables();
    }

    public static void main(String[] args) {
        Database.INSTANCE.createTables();
    }

    void createTables() {
        try {
            String[] tokens = Arrays.stream(Files.readString(Path.of(SCHEMA_PATH), StandardCharsets.UTF_8).split(";"))
                                    .filter(s -> !s.isBlank())
                                    .toArray(String[]::new);
            updateBatch(tokens);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> List<T> query(String query, Function<ResultSet, T> function) throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                Statement stmt = connection.createStatement();
                ResultSet rs = stmt.executeQuery(query)
        ) {
            List<T> results = new ArrayList<>();
            while (rs.next())
                results.add(function.apply(rs));
            return results;
        }
    }

    public int update(String query) throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                Statement stmt = connection.createStatement()
        ) {
            return stmt.executeUpdate(query);
        }
    }

    public int[] updateBatch(String[] queries) throws SQLException {
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
