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

public class Database {
    private static final String           SCHEMA_PATH   = "./schema.sql";
    private static final String           DATABASE_NAME = "searchEngineDatabase.db";
    private static final SQLiteDataSource dataSource    = new SQLiteDataSource();

    static {
        dataSource.setUrl("jdbc:sqlite:" + DATABASE_NAME);
        createTables();
    }

    public static void main(String[] args) {
        Database.createTables();
    }

    static void createTables() {
        try {
            String[] tokens = Arrays.stream(Files.readString(Path.of(SCHEMA_PATH), StandardCharsets.UTF_8).split(";"))
                                    .filter(s -> !s.isBlank())
                                    .toArray(String[]::new);
            updateBatch(tokens);
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> List<T> query(String query, Function<ResultSet, T> function) throws SQLException {
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

    public static int update(String query) throws SQLException {
        try (
                Connection connection = dataSource.getConnection();
                Statement stmt = connection.createStatement()
        ) {
            return stmt.executeUpdate(query);
        }
    }

    public static int[] updateBatch(String[] queries) throws SQLException {
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
