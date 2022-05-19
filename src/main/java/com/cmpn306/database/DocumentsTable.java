package com.cmpn306.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DocumentsTable {

    //deprecated, for testing only
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

        List<Document> documents;
        try (ResultSet rs = Database.INSTANCE.query(query)) {
            documents = new ArrayList<>();

            while (rs.next()) {
                String docUrl      = rs.getString("docUrl");
                String content     = rs.getString("content");
                String pageTitle   = rs.getString("pageTitle");
                int    wordCount   = 0;
                long   indexedTime = 0;
                long   timeCurrent = rs.getLong("timeCurrent");
                float  pageRank    = rs.getFloat("pageRank");

                // TODO: Ask Khaled?
                Document tmp_doc = new Document(docUrl, content, pageTitle, wordCount, 0, timeCurrent, pageRank);
            }
        }
        return documents;
    }

    public void updateCountByURL(String url, long count) throws SQLException {
        String query = "UPDATE documents SET wordCount = " + count + " " + "WHERE URL = " + url + " ;";

        Database.INSTANCE.update(query);
    }

    public void updateIndexTime(List<Document> documents) throws SQLException {
        StringBuilder query = new StringBuilder("UPDATE documents SET indexTime = " + System.currentTimeMillis() + " WHERE docUrl in (");

        for (int i = 0; i < documents.size(); ++i) {
            query.append(documents.get(i).docUrl());
            if (i != documents.size() - 1)
                query.append(", ");
        }
        query.append(");");

        Database.INSTANCE.update(query.toString());
    }
}
