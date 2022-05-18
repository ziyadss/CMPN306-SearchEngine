package com.cmpn306.indexer;

import com.cmpn306.database.Document;
import com.cmpn306.database.DocumentsTable;
import com.cmpn306.util.Filterer;

import java.sql.SQLException;
import java.util.*;

public class Indexer implements Runnable {
    private int sleepTime;

    private int iterationSize;

    //TO DO: acquire documentsTable
    private DocumentsTable documentsTable;

    //    private KeywordsTable keywordsTable;

    @Override public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }

            int wordCount = 0;
            //TO DO: clean this

            // Fetch non indexed docs
            List<Document> documents;
            try {
                documents = fetchToIndex();
            } catch (SQLException e) {
                e.printStackTrace();
                continue;
            }
            // for every document
            for (Document document: documents) {

                List<String> words = Collections.singletonList(Filterer.rawText(document.getContent()));

                try {
                    documentsTable.updateCountByURL(document.getDocUrl(), words.size());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Map<String, Long> wordFreq = new HashMap<>();
                words.forEach(word -> wordFreq.merge(word, 1L, Long::sum));

            }
        }
    }

    private List<Document> fetchToIndex() throws SQLException {
        return documentsTable.getIndexable(iterationSize);
    }

}
