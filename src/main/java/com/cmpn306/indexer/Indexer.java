package com.cmpn306.indexer;

import com.cmpn306.database.Document;
import java.util.List;

public class Indexer implements Runnable {
    private int sleepTime;

    private int iterationSize;

//    private DocumentsTable documentsTable;

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
//            List<Document> documents = fetchToIndex();





        }
    }

//    private List<Document> fetchToIndex() {
//        return documentsTable.getDocuments(iterationSize);
//    }

}
