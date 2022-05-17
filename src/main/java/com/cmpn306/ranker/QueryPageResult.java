package com.cmpn306.ranker;

public class QueryPageResult {

    //Data members from the word_document table
    String word;
    String docUrl;
    int wordCount;
    int tf;
    int idf;
    int tfIdf;

    //Data members from the documents table
    int docWordCount;
    int content;
    int title;

    public String toJson(){
        return "";
    }
}
