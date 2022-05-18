package com.cmpn306.ranker;

import java.util.*;

public class RelevanceRanker {

    int totalDocCount;

    public RelevanceRanker() {

    }

    public int getTotalDocCount() {
        return totalDocCount;
    }

    public void setTotalDocCount(int totalDocCount) {
        this.totalDocCount = totalDocCount;
    }

    public void rank(HashMap<String, List<QueryPageResult>> resultsMap) {
        Iterator itMap = resultsMap.entrySet().iterator();
        HashMap<String, QueryPageResult> docs= new HashMap<String,QueryPageResult>();
        while(itMap.hasNext()){
            Map.Entry pair = (Map.Entry)itMap.next();
            List<QueryPageResult> docList = (List<QueryPageResult>)pair.getValue();
            int refDocCount = docList.size();
            Iterator itList = docList.iterator();
            while(itList.hasNext()){
                QueryPageResult page = (QueryPageResult) itList.next();
                page.calculateRelevance(totalDocCount,refDocCount);
                if(!docs.containsKey(page.getDocUrl())){
                    docs.put(page.getDocUrl(),page);
                }else{
                    float relScoreAdd = docs.get(page.getDocUrl()).getRelevanceScore();
                    docs.get(page.getDocUrl()).setRelevanceScore(relScoreAdd+page.getRelevanceScore());
                    itList.remove();
                }
            }
        }
        sortByRelevance(resultsMap);
    }

    public void sortByRelevance(HashMap<String, List<QueryPageResult>> resultsMap){
        Iterator itMap = resultsMap.entrySet().iterator();
        HashMap<String, QueryPageResult> docs= new HashMap<String,QueryPageResult>();
        while(itMap.hasNext()) {
            Map.Entry             pair    = (Map.Entry) itMap.next();
            List<QueryPageResult> docList = (List<QueryPageResult>) pair.getValue();
            docList.sort(new Comparator<QueryPageResult>() {
                @Override public int compare(QueryPageResult o1, QueryPageResult o2) {
                    if(o1.getRelevanceScore()>o2.getRelevanceScore())
                        return 1;
                    else if (o1.getRelevanceScore()<o2.getRelevanceScore())
                        return -1;
                    else return 0;
                }
            });
        }
    }
}