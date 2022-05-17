package com.cmpn306.ranker;

import com.cmpn306.queryprocessor.QueryProcessor.QueryResult;

import java.util.List;

public class Ranker {

    private final PopularityRanker popularityRanker = new PopularityRanker();
    private final RelevanceRanker  relevanceRanker  = new RelevanceRanker();
    int totalDocCount;

    public Ranker(){

    }

    public static void rank(List<String> tokensList, List<QueryResult> results) {

    }

    void calculatePageRank() {
        popularityRanker.calculatePageRank();
    }
}
