package com.cmpn306.ranker;

public class Ranker {

    public PopularityRanker popularityRanker;
    public RelevanceRanker relevanceRanker;
    int totalDocCount;

    Ranker(){
        popularityRanker = new PopularityRanker();
        relevanceRanker = new RelevanceRanker();
    }

    void calculatePageRank(){
        popularityRanker.calculatePageRank();
    }
}
