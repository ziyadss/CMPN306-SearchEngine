package com.cmpn306.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;

public class Database {
    private static final String     SCHEMA_PATH   = "./schema.sql";
    private static final String     DATABASE_NAME = "searchEngineDatabase.db";
    protected static final Connection;


    public Database() throws SQLException, IOException {
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

    public static String getDataBaseName() {
        return DATABASE_NAME;
    }
}
