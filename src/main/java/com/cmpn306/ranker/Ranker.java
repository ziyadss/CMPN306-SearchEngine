package com.cmpn306.ranker;

import com.cmpn306.database.Database;
import com.cmpn306.queryprocessor.API;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

public class Ranker {

    private static final PopularityRanker popularityRanker = new PopularityRanker();
    private static final RelevanceRanker  relevanceRanker  = new RelevanceRanker();
    static               int              totalDocCount;

    public static void main(String[] args) throws SQLException {
        String        sql   = "SELECT COUNT(*) AS count FROM documents;";
        List<Integer> count = Database.query(sql, API::queryCount);
        Ranker.setTotalDocCount(count.get(0));
        Ranker.calculatePageRank();
    }

    public static int getTotalDocCount() {
        return totalDocCount;
    }

    static public void setTotalDocCount(int totalDocCount) {
        Ranker.totalDocCount = totalDocCount;
    }

    public static void rank(HashMap<String, List<QueryPageResult>> resultsMap) {
        //NOTE: HashMap are faster however it is not synchronized, if threading is used HashTable is preferred
        relevanceRanker.rank(resultsMap);
    }

    public static void calculatePageRank() {
        try {
            popularityRanker.pageRank();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
