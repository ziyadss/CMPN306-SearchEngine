package com.cmpn306.database;

import java.util.Hashtable;

public class WebGraphNode {
    String                          docUrl;
    double                          pageRank;
    Hashtable<String, WebGraphNode> outGoingUrls;
    Hashtable<String, WebGraphNode> incomingUrls;

    public WebGraphNode(String docUrl, double pageRank) {
        this.docUrl   = docUrl;
        this.pageRank = pageRank;
        outGoingUrls = new Hashtable<String,WebGraphNode>();
        incomingUrls = new Hashtable<String,WebGraphNode>();
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

    public Hashtable<String, WebGraphNode> getIncomingUrls() {
        return incomingUrls;
    }

    public double getPageRank() {
        return pageRank;
    }

    public void setPageRank(double pageRank) {
        this.pageRank = pageRank;
    }
}
