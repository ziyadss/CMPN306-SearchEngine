package com.cmpn306.ranker;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class Ranker {

    private static final PopularityRanker popularityRanker = new PopularityRanker();
    private static final RelevanceRanker  relevanceRanker  = new RelevanceRanker();
    static               int              totalDocCount;

    public Ranker() {

    }

    public static int getTotalDocCount() {
        return totalDocCount;
    }

    public void setTotalDocCount(int totalDocCount) {
        Ranker.totalDocCount = totalDocCount;
    }

    public static void rank(HashMap<String, List<QueryPageResult>> resultsMap) {
        //NOTE: HashMap are faster however it is not synchronized, if threading is used HashTable is preferred
        relevanceRanker.rank(resultsMap);
    }

    void calculatePageRank() throws SQLException {
        popularityRanker.pageRank();
    }
}
