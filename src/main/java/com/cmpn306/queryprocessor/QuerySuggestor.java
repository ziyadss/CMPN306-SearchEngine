package com.cmpn306.queryprocessor;

import com.cmpn306.database.Database;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@WebServlet(name = "QuerySuggestor", urlPatterns = {"/suggest", "/suggest/"})
public class QuerySuggestor extends HttpServlet {

    private static List<String> suggestions(String q) throws SQLException {
        String query = "SELECT word FROM words WHERE word LIKE '" + q + "%'";
        return Database.query(query, QuerySuggestor::getWord);

    }

    private static String getWord(ResultSet rs) {
        try {
            return rs.getString("word");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String query = request.getParameter("q");

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET");
        response.setStatus(HttpServletResponse.SC_OK);

        try (PrintWriter out = response.getWriter()) {
            List<String> results  = suggestions(query);
            String       elements = String.join("',", results);
            String       json     = "{\"suggestions\":['%s']}";
            out.printf(json, elements);
        } catch (IOException | SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
