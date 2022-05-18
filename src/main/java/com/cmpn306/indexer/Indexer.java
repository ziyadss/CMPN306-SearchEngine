package com.cmpn306.indexer;

import com.cmpn306.database.Document;
import com.cmpn306.database.DocumentsTable;
import com.cmpn306.util.Filterer;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Indexer implements Runnable {
    private int sleepTime;

    private int iterationSize;

    //TO DO: acquire documentsTable
    private DocumentsTable documentsTable;

//    private KeywordsTable keywordsTable;

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(sleepTime);
            }
            catch (InterruptedException ignored) {
                Thread.currentThread().interrupt();
            }

            int wordCount = 0;
            //TO DO: clean this

            // Fetch non indexed docs
            List<Document> documents = null;
            try {
                documents = fetchToIndex();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            // for every document
            for (Document document: documents) {

                List<String> words = Filterer.rawText(document.getContent());

                Map<String, Integer> wordFreq = new HashMap<String, Integer>();

                try {
                    documentsTable.updateCountByURL(document.getDocUrl(), words.size());
                } catch (SQLException e) {
                    e.printStackTrace();
                }


                for (String word: words) {
                    Integer count = wordFreq.get(word);
                    if (count == null)
                        wordFreq.put(word, 1);
                    else
                        wordFreq.put(word, count + 1);
                }




            }





        }
    }

    private List<Document> fetchToIndex() throws SQLException {
        return documentsTable.getIndexable(iterationSize);
    }


}
