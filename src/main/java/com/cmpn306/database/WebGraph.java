package com.cmpn306.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;

public class WebGraph {
    Hashtable<String, WebGraphNode> docs = new Hashtable<>();

    public void getDocumentsUrl() throws SQLException {
        String                          query = "SELECT docUrl, pageRank FROM documents";
        Hashtable<String, WebGraphNode> documents;
        try (ResultSet rs = Database.INSTANCE.query(query)) {
            documents = new Hashtable<>();
            while (rs.next()) {
                String       docUrl   = rs.getString("docUrl");
                float        pageRank = rs.getFloat("pageRank");
                WebGraphNode tmp_node = new WebGraphNode(docUrl, pageRank);
                documents.put(tmp_node.getDocUrl(), tmp_node);
            }
        }
        docs = documents;
    }

    public void getDocumentsLinks() throws SQLException {
        String                         query     = "SELECT srcDocUrl, dstDocUrl FROM web_graph " ;
                // "SELECT srcDocUrl,d1.pageRank as srcPageRank, d2.pageRank,dstDocUrl AS dstPageRank FROM web_graph"+
                //                                    "JOIN documents AS d1 ON web_graph.srcDocUrl = d1.docUrl" +
                //                                    "JOIN documents AS d2 ON web_graph.dstDocUrl = d2.docUrl";
        ResultSet                      rs        = Database.INSTANCE.query(query);
        while(rs.next()){
            String srcDocUrl = rs.getString("srcDocUrl");
            String dstDocUrl = rs.getString("dstDocUrl");
            docs.get(srcDocUrl).outGoingUrls.put(dstDocUrl,docs.get(dstDocUrl));
        }
    }

    public void generateAdjacencyMatrix(){

    }
}
