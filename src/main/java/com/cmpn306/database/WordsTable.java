package com.cmpn306.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WordsTable {
    public void insertWords(Map<String, Long> wordFreq, String docUrl) throws SQLException {
        List<String>  words = new ArrayList<>(wordFreq.keySet());
        StringBuilder query = new StringBuilder("INSERT OR IGNORE INTO words VALUES (");

        for (int i = 0; i < words.size(); ++i) {
            query.append(words.get(i));

            if (i != words.size() - 1)
                query.append(", ");
        }
        query.append(");");

        Database.INSTANCE.update(query.toString());

        //        StringBuilder query2 = new StringBuilder("SELECT ROWID FROM words where word in (");
        //
        //        for (int i = 0; i < words.size(); ++i) {
        //            query2.append(words.get(i));
        //
        //            if (i != words.size() - 1)
        //                query2.append(", ");
        //        }
        //
        //        query2.append(";");
        //
        //        ResultSet rowIdSet = Database.INSTANCE.query(query2.toString());
        //
        //        List<Integer> rowIds = new ArrayList<Integer>();
        //
        //        while (rowIdSet.next()) {
        //            rowIds.add(rowIdSet.getInt(1));
        //        }

        StringBuilder query3 = new StringBuilder("REPLACE INTO words_document(docID, word, count) VALUES");

        for (int i = 0; i < words.size(); ++i) {
            query3.append("(")
                  .append(docUrl)
                  .append(", ")
                  .append(words.get(i))
                  .append(", ")
                  .append(wordFreq.get(words.get(i)))
                  .append(")");

            if (i != words.size() - 1) {
                query3.append(", ");
            }
        }
        query3.append(";");

        Database.INSTANCE.update(query3.toString());
    }
}
