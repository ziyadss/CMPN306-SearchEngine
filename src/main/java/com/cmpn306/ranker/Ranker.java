package com.cmpn306.ranker;

import java.util.HashMap;
import java.util.List;

public class Ranker {

    private final PopularityRanker popularityRanker = new PopularityRanker();
    private final RelevanceRanker  relevanceRanker  = new RelevanceRanker();
    int totalDocCount;

    public Ranker() {

    }

    public int getTotalDocCount() {
        return totalDocCount;
    }

    public void setTotalDocCount(int totalDocCount) {
        this.totalDocCount = totalDocCount;
        relevanceRanker.setTotalDocCount(this.totalDocCount);
    }

    public void rank(HashMap<String, List<QueryPageResult>> resultsMap) {
        //NOTE: HashMap are faster however it is not synchronized, if threading is used HashTable is preferred
        relevanceRanker.rank(resultsMap);
    }

    void calculatePageRank() {
        popularityRanker.calculatePageRank();
    }
}
