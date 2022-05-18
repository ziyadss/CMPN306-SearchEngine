package com.cmpn306.database;

import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.List;
import com.cmpn306.database.Database;
import java.sql.*;




public class DocumentsTable {
    String dataBaseName = Database.getDataBaseName();
    Connection connection = Database.connection;


    Statement statement;



    public DocumentsTable() throws SQLException {
         statement  = connection.createStatement();
    }
    //fix this text
    public void selectUrl() throws SQLException {
        String query = "SELECT * FROM documents";
        ResultSet rs = statement.executeQuery(query);
        while (rs.next()) {
            System.out.println(rs.getString("docUrl"));
        }
    }

    public List<Document> getIndexable(Integer limit) throws SQLException {
        String query = "SELECT docUrl, content, timeCurrent, pageRank, pageTitle FROM documents WHERE indexTime < timeCurrent limit " + limit;

        ResultSet rs = statement.executeQuery(query);
        List<Document> documents = new ArrayList<Document>();

        while (rs.next())  {
            String docUrl = rs.getString("docUrl");
            String content = rs.getString("content");
            Integer wordCount = 0;
            Long timeCurrent = rs.getLong("timeCurrent");
            Float pageRank = rs.getFloat("pageRank");
            String pageTitle = rs.getString("pageTitle");
            Document tmp_doc = new Document(docUrl, content, wordCount, timeCurrent, pageRank, pageTitle);
        }
        return documents;
    }

    public void updateCountByURL(String url, long count) throws SQLException {
        String query = "UPDATE documents SET wordCount = " + count + " " + "WHERE URL = " + url + " ;";

        statement.execute(query);
    }


}
