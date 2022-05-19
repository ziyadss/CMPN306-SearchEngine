package com.cmpn306.indexer;

import com.cmpn306.database.Database;
import com.cmpn306.database.WordsTable;
import com.cmpn306.util.Filterer;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Indexer {
    private static final int SLEEP_TIME   = 10 * 1000;
    private static final int THREAD_COUNT = 1;

    private static final int LIMIT = 100;

    public static void main(String[] args) {
        for (int i = 0; i < THREAD_COUNT; i++) {
            initializeIndexer();
        }
    }

    public static void initializeIndexer() {
        new IndexerThread().start();
    }

    private record Document(String pageTitle, String docUrl, String content) { }

    public static class IndexerThread extends Thread {
        private static Document resultToDocument(ResultSet result) {
            try {
                return new Document(result.getString("pageTitle"),
                                    result.getString("docUrl"),
                                    result.getString("content"));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        private List<Document> fetchToIndex() throws SQLException {
            String query = String.format(
                    "SELECT docUrl, content, pageTitle, pageRank FROM documents WHERE indexTime < %d ORDER BY indexTime LIMIT %d",
                    System.currentTimeMillis(),
                    LIMIT);
            return Database.INSTANCE.query(query, IndexerThread::resultToDocument);
        }

        private void updateIndexTime(List<Document> documents) throws SQLException {
            if (documents.size() > 0) {
                String query = "UPDATE documents SET indexTime = " + System.currentTimeMillis() + " WHERE docUrl in ";

                String docs = documents.stream()
                                       .map(doc -> "'" + doc.docUrl + "'")
                                       .collect(Collectors.joining(", ", "(", ");"));

                Database.INSTANCE.update(query + docs);
            }
        }

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
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

                    Map<String, Long> wordFreq = new HashMap<>();
                    words.forEach(word -> wordFreq.merge(word, 1L, Long::sum));
                    System.out.println("Here");
                    // print all k,v pairs
                    wordFreq.forEach((word, freq) -> System.out.println(word + " -> " + freq));
                    if (wordFreq.size() > 0) {
                        try {
                            WordsTable.insertWords(wordFreq, document.docUrl());
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }

                    words = words.stream().distinct().toList();
                    try {
                        String query = "UPDATE documents SET wordCount = " + words.size() + " " + "WHERE docUrl = '" + document.docUrl() + "' ;";

                        Database.INSTANCE.update(query);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }

                }
                try {
                    updateIndexTime(documents);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException ignored) {
                    Thread.currentThread().interrupt();
                }
            }
        }

    }
}


