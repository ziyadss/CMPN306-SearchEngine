package com.cmpn306.queryprocessor;

import com.cmpn306.database.Database;
import com.cmpn306.ranker.Ranker;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class API {
    public static void main(String[] args) throws SQLException {
        String        sql   = "SELECT COUNT(*) AS count FROM documents;";
        List<Integer> count = Database.query(sql, API::queryCount);
        Ranker.setTotalDocCount(count.get(0));

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

    public static Integer queryCount(ResultSet rs) {
        try {
            return rs.getInt("count");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}