package com.cmpn306;

import com.cmpn306.database.Database;
import com.cmpn306.database.DocumentsTable;

import java.io.IOException;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        System.out.println("Hello world!");
        Database db = new Database();

        DocumentsTable dt = new DocumentsTable();
        dt.selectUrl();

    }
}