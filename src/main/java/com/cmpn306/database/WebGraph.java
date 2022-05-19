package com.cmpn306.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

public class WebGraph {
    Hashtable<String, WebGraphNode> docs = new Hashtable<>();

    private Boolean documentProcess(ResultSet rs) {
        try {
            String srcDocUrl = rs.getString("srcDocUrl");
            String dstDocUrl = rs.getString("dstDocUrl");
            docs.get(srcDocUrl).outGoingUrls.put(dstDocUrl, docs.get(dstDocUrl));
            docs.get(dstDocUrl).incomingUrls.put(srcDocUrl, docs.get(srcDocUrl));
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Hashtable<String, WebGraphNode> getDocs() {
        return docs;
    }

    private Boolean urlProcess(ResultSet rs) {
        try {
            String docUrl   = rs.getString("docUrl");
            double pageRank = rs.getDouble("pageRank");
            if (pageRank == -1)
                pageRank = 1;
            WebGraphNode tmp_node = new WebGraphNode(docUrl, pageRank);
            docs.put(tmp_node.getDocUrl(), tmp_node);
            return true;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void getDocumentsUrl() throws SQLException {
        String query = "SELECT docUrl, pageRank FROM documents";

        Database.query(query, this::urlProcess);

    }

    public void getDocumentsLinks() throws SQLException {
        String query = "SELECT srcDocUrl, dstDocUrl FROM web_graph ";
        // "SELECT srcDocUrl,d1.pageRank as srcPageRank, d2.pageRank,dstDocUrl AS dstPageRank FROM web_graph"+
        //                                    "JOIN documents AS d1 ON web_graph.srcDocUrl = d1.docUrl" +
        //                                    "JOIN documents AS d2 ON web_graph.dstDocUrl = d2.docUrl";

        Database.query(query, this::documentProcess);
    }
}
