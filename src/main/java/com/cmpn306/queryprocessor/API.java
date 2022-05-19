package com.cmpn306.queryprocessor;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

public class API {
    public static void main(String[] args) {
        Tomcat tomcat = new Tomcat();

        Context context = tomcat.addContext("", new File("src/main/api/").getAbsolutePath());
        Tomcat.addServlet(context, "QueryProcessor", new QueryProcessor());
        context.addServletMappingDecoded("/search", "QueryProcessor");
        context.addServletMappingDecoded("/search/", "QueryProcessor");

        Tomcat.addServlet(context, "QuerySuggestor", new QuerySuggestor());
        context.addServletMappingDecoded("/suggest", "QuerySuggestor");
        context.addServletMappingDecoded("/suggest/", "QuerySuggestor");

        tomcat.getConnector();

        try { tomcat.start(); } catch (LifecycleException ignored) { }

        tomcat.getServer().await();
    }
}