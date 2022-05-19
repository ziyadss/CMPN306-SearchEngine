package com.cmpn306.database;

public record Document(String docUrl, String content, String pageTitle, long wordCount, long indexedTime,
                       long timeCurrent, double pageRank) { }

