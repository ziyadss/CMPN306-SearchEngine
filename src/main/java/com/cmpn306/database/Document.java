package com.cmpn306.database;

import java.util.List;

public class Document {
    private String docUrl;
    private String content;
    private long wordCount;
    private long indexedTime;
    private long timeCurrent;
    private float pageRank;
    private String pageTitle;


    public Document(String docUrl, String content, long wordCount, long timeCurrent, float pageRank, String pageTitle) {
        this.docUrl = docUrl;
        this.content = content;
        this.wordCount = wordCount;
        this.timeCurrent = timeCurrent;
        this.pageRank = pageRank;
        this.pageTitle = pageTitle;
    }

    public Document setDocUrl(String docUrl) {
        this.docUrl = docUrl;
        return this;
    }

    public Document setContent(String content) {
        this.content = content;
        return this;
    }

    public Document setWordCount(long wordCount) {
        this.wordCount = wordCount;
        return this;
    }

    public Document setTimeCurrent(long timeCurrent) {
        this.timeCurrent = timeCurrent;
        return this;
    }

    public Document setPageRank(float pageRank) {
        this.pageRank = pageRank;
        return this;
    }

    public Document setPageTitle(String pageTitle) {
        this.pageTitle = pageTitle;
        return this;
    }

    public String getDocUrl() {
        return docUrl;
    }

    public String getContent() {
        return content;
    }

    public long getWordCount() {
        return wordCount;
    }

    public long getTimeCurrent() {
        return timeCurrent;
    }

    public float getPageRank() {
        return pageRank;
    }

    //TO DO: format page title and return it directly
    public String getPageTitle() {
        return pageTitle;
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

