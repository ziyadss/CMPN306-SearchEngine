package com.cmpn306.database;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WordsTable {
    public static void insertWords(Map<String, Long> wordFreq, String docUrl) throws SQLException {
        List<String> words = new ArrayList<>(wordFreq.keySet());
        String       query = "INSERT OR IGNORE INTO words VALUES ";

        String wordsString = words.stream().collect(Collectors.joining("'), ('", "('", "');"));

        Database.INSTANCE.update(query + wordsString);

        query = "REPLACE INTO word_document(docUrl, word, wordCount) VALUES ";
        String entries = words.stream()
                              .map(word -> "('" + docUrl + "', '" + word + "', " + wordFreq.get(word)+ ")" )
                              .collect(Collectors.joining(", ", "", ";"));

        Database.INSTANCE.update(query + entries);
    }
}
