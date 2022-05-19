package com.cmpn306.ranker;

public class QueryPageResult {

    private final double TFIDF_FACTOR    = 1.0;
    private final double PAGERANK_FACTOR = 1.0;
    private final double HEADING_FACTOR  = 1.0;
    private final double LINK_FACTOR     = 1.0;

    //Data members from the word_document table
    String word;
    String docUrl;
    int    wordCount;
    double tf;
    double idf;
    double tfIdf;
    double pageRank;

    //Data members from the documents table
    int    docWordCount;
    String content;
    String title;
    double relevanceScore;

    public QueryPageResult(
            String word, String docUrl, int wordCount, int docWordCount, String content, String title, double pageRank
                          ) {
        this.word         = word;
        this.docUrl       = docUrl;
        this.wordCount    = wordCount;
        this.docWordCount = docWordCount;
        this.content      = content;
        this.title        = title;
        this.pageRank     = pageRank;
    }

    public String getDocUrl() {
        return docUrl;
    }

    public void setTf(double tf) {
        this.tf = tf;
    }

    public void setIdf(double idf) {
        this.idf = idf;
    }

    public void setTfIdf(double tfIdf) {
        this.tfIdf = tfIdf;
    }

    public String getTitle() {
        return title;
    }

    public double getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    public void calculateTf() {
        setTf((double) wordCount / docWordCount);
    }

    public void calculateIdf(int totalDocCount, int refDocCount) {
        setIdf(Math.log((double) totalDocCount / refDocCount));
    }

    public void calculateTfIdf() {
        setTfIdf(tf * idf);
    }

    public void calculateRelevance(int refDocCount) {
        calculateTf();
        calculateIdf(Ranker.getTotalDocCount(), refDocCount);
        calculateTfIdf();
        double relevanceScoreCalc = tfIdf * TFIDF_FACTOR + pageRank * PAGERANK_FACTOR + isInHeading() * HEADING_FACTOR + isInLink() * LINK_FACTOR;
        setRelevanceScore(relevanceScoreCalc);
    }

    public String getSnippet() {
        int index = content.indexOf(word);
        //average word is 5 characters, return the first 10 words and subsequent 20 words
        return content.substring(Math.max(index - 50, 0), Math.min(index + 100, content.length()));
    }

    private double isInLink() {
        return docUrl.contains(word) ? 1 : 0;
    }

    private double isInHeading() {
        return title.contains(word) ? 1 : 0;
    }
}
