package com.cmpn306.ranker;

import com.cmpn306.database.Database;
import com.cmpn306.database.WebGraph;

import java.sql.SQLException;
import java.util.Hashtable;

public class PopularityRanker {
    private final double DAMPENING_FACTOR = 0.85;
    private final double ERROR_TOLERANCE  = 1.0e-6;
    private final int    MAX_ITERATIONS   = 15;
    WebGraph                  webGraph;
    double                    offset;
    Hashtable<String, Double> pageRankOld;

    public PopularityRanker() {
        offset = (1 - DAMPENING_FACTOR);
    }

    public void setWebGraph(WebGraph webGraph) {
        this.webGraph = webGraph;
    }

    void getPages() throws SQLException {
        webGraph = new WebGraph();
        webGraph.getDocumentsUrl();
        webGraph.getDocumentsLinks();
    }

    void pageRank() throws SQLException {
        getPages();
        int iterCount = 0;
        while (!isConverged(iterCount)) {
            calculatePageRank();
            iterCount++;
        }
        for (String node: webGraph.getDocs().keySet())
            updatePageRankQuery(node, webGraph.getDocs().get(node).getPageRank());

    }

    private void updatePageRankQuery(String node, double pageRank) throws SQLException {
        String query = "UPDATE documents SET pageRank = " + pageRank + "WHERE docUrl=\"" + node + "\";";
        Database.INSTANCE.update(query);
    }

    public void calculatePageRank() {
        setPageRankOld(webGraph);
        for (String node: webGraph.getDocs().keySet()) {
            double tmp_calc = 0.0;
            for (String link: webGraph.getDocs().get(node).getIncomingUrls().keySet()) {
                tmp_calc += webGraph.getDocs().get(link).getPageRank() / webGraph.getDocs()
                                                                                 .get(link)
                                                                                 .getOutGoingUrls()
                                                                                 .size();
            }
            webGraph.getDocs().get(node).setPageRank(offset + DAMPENING_FACTOR * tmp_calc);
        }

    }

    private void setPageRankOld(WebGraph webGraph) {
        pageRankOld = new Hashtable<String, Double>();
        for (String node: webGraph.getDocs().keySet())
            pageRankOld.put(webGraph.getDocs().get(node).getDocUrl(), webGraph.getDocs().get(node).getPageRank());
    }

    public boolean isConverged(int i) {
        if (i == 0)
            return false;
        if (i >= MAX_ITERATIONS)
            return true;
        Double err = 0.0;
        for (String node: webGraph.getDocs().keySet())
            err += Math.abs(webGraph.getDocs().get(node).getPageRank() - pageRankOld.get(node));
        return err < ERROR_TOLERANCE * Ranker.getTotalDocCount();
    }
}