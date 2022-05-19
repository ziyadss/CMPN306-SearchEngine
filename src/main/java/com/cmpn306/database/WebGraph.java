package com.cmpn306.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Hashtable;

public class WebGraph {
    Hashtable<String, WebGraphNode> docs = new Hashtable<String,WebGraphNode>();
    public void getDocumentsUrl() throws SQLException {
        String                         query     = "SELECT docUrl, pageRank FROM documents";
        ResultSet                      rs        = Database.INSTANCE.query(query);
        Hashtable<String,WebGraphNode> documents = new Hashtable<String,WebGraphNode>();
        while (rs.next()) {
            String       docUrl  = rs.getString("docUrl");
            float pageRank = rs.getFloat("pageRank");
            WebGraphNode tmp_node = new WebGraphNode(docUrl,pageRank);
            documents.put(tmp_node.getDocUrl(), tmp_node);
        }
        docs = documents;
    }

    public void getDocumentsLinks() {
    }
}
