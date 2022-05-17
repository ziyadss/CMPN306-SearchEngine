package com.cmpn306.ranker;

public class QueryPageResult {

    //Data members from the word_document table
    String word;
    String docUrl;
    int wordCount;
    float tf;
    float idf;
    float tfIdf;

    //Data members from the documents table
    int docWordCount;
    int content;
    int title;

    float relevanceScore;

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

    public float getTf() {
        return tf;
    }

    public void setTf(float tf) {
        this.tf = tf;
    }

    public float getIdf() {
        return idf;
    }

    public void setIdf(float idf) {
        this.idf = idf;
    }

    public float getTfIdf() {
        return tfIdf;
    }

    public void setTfIdf(float tfIdf) {
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

    public float getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(float relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    public QueryPageResult(){

    }

    public String toJson(){
        return "";
    }

    public void calculateTf(){
        setTf(wordCount/docWordCount); //TODO: add query to update value in table
    }
    public void calculateIdf(int totalDocCount,int refDocCount){
        setIdf((float) Math.log(totalDocCount/refDocCount));
    }
    public void calculateTfIdf(){
        setTfIdf(tf*idf);
    }

    public void calculateRelevance(int totalDocCount, int refDocCount){
        calculateTf();
        calculateIdf(totalDocCount,refDocCount);
        calculateTfIdf();
        setRelevanceScore((float) (tfIdf + 0*1.0)); //TODO: Add other scores multiplied by factors to give final relevance score
    }
}
