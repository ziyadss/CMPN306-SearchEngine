package com.cmpn306.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DocumentsTable {
    public void selectUrl() throws SQLException {
        String query = "SELECT * FROM documents";
        try (ResultSet rs = Database.INSTANCE.query(query)) {
            while (rs.next()) {
                System.out.println(rs.getString("docUrl"));
            }
        }
    }

    public List<Document> getIndexable(Integer limit) throws SQLException {
        String query = "SELECT docUrl, content, timeCurrent, pageRank, pageTitle FROM documents WHERE indexTime < timeCurrent limit " + limit;

        ResultSet      rs        = Database.INSTANCE.query(query);
        List<Document> documents = new ArrayList<Document>();

        while (rs.next()) {
            String   docUrl      = rs.getString("docUrl");
            String   content     = rs.getString("content");
            int      wordCount   = 0;
            long     timeCurrent = rs.getLong("timeCurrent");
            float    pageRank    = rs.getFloat("pageRank");
            String   pageTitle   = rs.getString("pageTitle");
            Document tmp_doc     = new Document(docUrl, content, wordCount, timeCurrent, pageRank, pageTitle);
        }
        return documents;
    }

    public void updateCountByURL(String url, long count) throws SQLException {
        String query = "UPDATE documents SET wordCount = " + count + " " + "WHERE URL = " + url + " ;";

        Database.INSTANCE.update(query);
    }
}
