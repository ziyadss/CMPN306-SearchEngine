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

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public String getDocUrl() {
        return docUrl;
    }

    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }

    public int getWordCount() {
        return wordCount;
    }

    public void setWordCount(int wordCount) {
        this.wordCount = wordCount;
    }

    public int getTf() {
        return tf;
    }

    public void setTf(int tf) {
        this.tf = tf;
    }

    public int getIdf() {
        return idf;
    }

    public void setIdf(int idf) {
        this.idf = idf;
    }

    public int getTfIdf() {
        return tfIdf;
    }

    public void setTfIdf(int tfIdf) {
        this.tfIdf = tfIdf;
    }

    public int getDocWordCount() {
        return docWordCount;
    }

    public void setDocWordCount(int docWordCount) {
        this.docWordCount = docWordCount;
    }

    public int getContent() {
        return content;
    }

    public void setContent(int content) {
        this.content = content;
    }

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }


    public QueryPageResult(){

    }

    public String toJson(){
        return "";
    }
}
