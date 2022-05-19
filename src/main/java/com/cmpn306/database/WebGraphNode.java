package com.cmpn306.database;

import java.util.Hashtable;

public class WebGraphNode {
    String                          docUrl;
    double                          pageRank;
    Hashtable<String, WebGraphNode> outGoingUrls;

    public WebGraphNode(String docUrl, double pageRank) {
        this.docUrl   = docUrl;
        this.pageRank = pageRank;
    }


    public String getDocUrl() {
        return docUrl;
    }

    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }

    public Hashtable<String, WebGraphNode> getOutGoingUrls() {
        return outGoingUrls;
    }

    public double getPageRank() {
        return pageRank;
    }

    public void setPageRank(double pageRank) {
        this.pageRank = pageRank;
    }
}
