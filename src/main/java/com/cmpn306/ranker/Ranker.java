package com.cmpn306.ranker;
import com.cmpn306.queryprocessor.QueryProcessor;
import java.util.List;

public class Ranker {

    public PopularityRanker popularityRanker;
    public RelevanceRanker relevanceRanker;
    int totalDocCount;

    Ranker(){
        popularityRanker = new PopularityRanker();
        relevanceRanker = new RelevanceRanker();
    }

    void calculatePageRank() {
        popularityRanker.calculatePageRank();
    }
    public static void rank(List<String> tokensList, List<QueryProcessor.QueryResult> results) {


    }
}
