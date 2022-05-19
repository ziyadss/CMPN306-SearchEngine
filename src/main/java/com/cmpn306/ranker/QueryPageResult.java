package com.cmpn306.ranker;

public class QueryPageResult {

    //Data members from the word_document table
    String word;
    String docUrl;
    int    wordCount;
    double tf;
    double idf;
    double tfIdf;

    //Data members from the documents table
    int docWordCount;
    int content;
    int title;

    public QueryPageResult(String word, String docUrl, int wordCount, int docWordCount, int content, int title) {
        this.word         = word;
        this.docUrl       = docUrl;
        this.wordCount    = wordCount;
        this.docWordCount = docWordCount;
        this.content      = content;
        this.title        = title;
    }

    double relevanceScore;

    public QueryPageResult() {

    }

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

    public double getTf() {
        return tf;
    }

    public void setTf(double tf) {
        this.tf = tf;
    }

    public double getIdf() {
        return idf;
    }

    public void setIdf(double idf) {
        this.idf = idf;
    }

    public double getTfIdf() {
        return tfIdf;
    }

    public void setTfIdf(double tfIdf) {
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

    public double getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    public String toJson() {
        return "";
    }

    public void calculateTf() {
        setTf((double) wordCount / docWordCount); //TODO: add query to update value in table
    }

    public void calculateIdf(int totalDocCount, int refDocCount) {
        setIdf(Math.log((double) totalDocCount / refDocCount));
    }

    public void calculateTfIdf() {
        setTfIdf(tf * idf);
    }

    public void calculateRelevance(int totalDocCount, int refDocCount) {
        calculateTf();
        calculateIdf(totalDocCount, refDocCount);
        calculateTfIdf();
        setRelevanceScore(tfIdf + 0 * 1.0); //TODO: Add other scores multiplied by factors to give final relevance score
    }
}
