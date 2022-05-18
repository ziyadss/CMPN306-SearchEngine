package com.cmpn306.database;

public class Document {
    private String docUrl;
    private String content;
    private long   wordCount;
    private long   indexedTime;
    private long   timeCurrent;
    private float  pageRank;
    private String pageTitle;

    public Document(String docUrl, String content, long wordCount, long timeCurrent, float pageRank, String pageTitle) {
        this.docUrl      = docUrl;
        this.content     = content;
        this.wordCount   = wordCount;
        this.timeCurrent = timeCurrent;
        this.pageRank    = pageRank;
        this.pageTitle   = pageTitle;
    }

    public String getDocUrl() {
        return docUrl;
    }

    public Document setDocUrl(String docUrl) {
        this.docUrl = docUrl;
        return this;
    }

    public String getContent() {
        return content;
    }

    public Document setContent(String content) {
        this.content = content;
        return this;
    }

    public long getWordCount() {
        return wordCount;
    }

    public Document setWordCount(long wordCount) {
        this.wordCount = wordCount;
        return this;
    }

    public long getTimeCurrent() {
        return timeCurrent;
    }

    public Document setTimeCurrent(long timeCurrent) {
        this.timeCurrent = timeCurrent;
        return this;
    }

    public float getPageRank() {
        return pageRank;
    }

    public Document setPageRank(float pageRank) {
        this.pageRank = pageRank;
        return this;
    }

    //TO DO: format page title and return it directly
    public String getPageTitle() {
        return pageTitle;
    }

    public Document setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
        return this;
    }
    //TO DO: Add a snippet extractor ?

    public long getIndexedTime() {
        return indexedTime;
    }

    public Document setIndexedTime(long indexedTime) {
        this.indexedTime = indexedTime;
        return this;
    }

}

