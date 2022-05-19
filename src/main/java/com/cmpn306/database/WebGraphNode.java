package com.cmpn306.database;

import java.util.Hashtable;

public class WebGraphNode {
    String                      docUrl;
    float                       pageRank;
    Hashtable<String, WebGraph> outGoingUrls;

    public WebGraphNode(String docUrl, float pageRank) {
        this.docUrl   = docUrl;
        this.pageRank = pageRank;
    }

    public String getDocUrl() {
        return docUrl;
    }

    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }

}
