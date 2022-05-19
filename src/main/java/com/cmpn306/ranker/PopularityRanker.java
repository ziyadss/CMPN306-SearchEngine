package com.cmpn306.ranker;

import com.cmpn306.database.WebGraph;

import java.sql.SQLException;

public class PopularityRanker {
    private final double DAMPENING_FACTOR = 0.85;
    private final double ERROR_TOLERANCE  = 1.0e-6;

    PopularityRanker() {

    }

    void getPages() throws SQLException {
        WebGraph webGraph = new WebGraph();
        webGraph.getDocumentsUrl();
        webGraph.getDocumentsLinks();
    }

    void getPageLinks() {

    }

    void calculatePageRank() {

    }
}