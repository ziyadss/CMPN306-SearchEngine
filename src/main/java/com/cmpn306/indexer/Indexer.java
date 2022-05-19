package com.cmpn306.indexer;

import com.cmpn306.database.Document;
import com.cmpn306.database.DocumentsTable;
import com.cmpn306.database.WordsTable;
import com.cmpn306.util.Filterer;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Indexer implements Runnable {
    private int sleepTime;

    private int iterationSize;

    //TO DO: acquire documentsTable
    private DocumentsTable documentsTable;

    private WordsTable wordsTable;

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }

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
                List<String> words = Filterer.getKeyWords(document.content());

                try {
                    documentsTable.updateCountByURL(document.docUrl(), words.size());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                Map<String, Long> wordFreq = new HashMap<>();
                words.forEach(word -> wordFreq.merge(word, 1L, Long::sum));
                //update word count for the URL doc
                try {
                    documentsTable.updateCountByURL(document.docUrl(), words.size());
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                if (wordFreq.size() > 0) {
                    try {
                        wordsTable.insertWords(wordFreq, document.docUrl());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

            }
            try {
                updateIndexTime(documents);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private List<Document> fetchToIndex() throws SQLException {
        return documentsTable.getIndexable(iterationSize);
    }

    private void updateIndexTime(List<Document> documents) throws SQLException {
        if (documents.size() > 0) {
            documentsTable.updateIndexTime(documents);
        }
    }

    private static class Index {
        private Indexer indexer;

        public void initializeIndexer() {
            new Thread(indexer, "indexer").start();
        }
    }
}
