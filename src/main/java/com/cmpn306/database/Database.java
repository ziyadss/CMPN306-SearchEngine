package com.cmpn306.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class Database {
    private final String     SCHEMA_PATH   = "./schema.sql";
    private final String     DATABASE_NAME = "searchEngineDatabase.db";
    private final Connection connection;

    public Database() throws SQLException, IOException {
        connection = DriverManager.getConnection("jdbc:sqlite:" + DATABASE_NAME);
        createTables();
    }

    void createTables() throws IOException, SQLException {
        Statement stmt = connection.createStatement();

        String   sql    = Files.readString(Path.of(SCHEMA_PATH));
        String[] tokens = sql.split(";");

        for (String token: tokens) {
            if (!token.isBlank())
                stmt.addBatch(token);
        }
        
        stmt.executeBatch();
    }
}
