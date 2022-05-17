package com.cmpn306.ranker;

import com.cmpn306.queryprocessor.QueryProcessor.QueryResult;

import java.util.HashMap;
import java.util.List;

public class Ranker {

    private final PopularityRanker popularityRanker = new PopularityRanker();
    private final RelevanceRanker  relevanceRanker  = new RelevanceRanker();
    int totalDocCount;

    public Ranker(){

    }

    public void rank(HashMap<String,List<QueryPageResult>> resultsMap) {
        //NOTE: HashMap are faster however if they are not synchronized, if threading is used HashTable is preferred
        relevanceRanker.rank(resultsMap);
    }

    void calculatePageRank() {
        popularityRanker.calculatePageRank();
    }
}
