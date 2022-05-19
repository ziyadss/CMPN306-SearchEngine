package com.cmpn306.ranker;

import com.cmpn306.database.WebGraph;
import com.cmpn306.database.WebGraphNode;

import java.sql.SQLException;
import java.util.Hashtable;

public class PopularityRanker {

    PopularityRanker() {

    }

    void getPages() throws SQLException {
        WebGraph webGraph = new WebGraph();
        webGraph.getDocumentsUrl();
        webGraph.getDocumentsLinks();
    }
    void getPageLinks(){

    }

    void calculatePageRank() {

    }
}