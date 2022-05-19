package com.cmpn306.queryprocessor;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@WebServlet(name = "QuerySuggestor", urlPatterns = {"/suggest", "/suggest/"})
public class QuerySuggestor extends HttpServlet {

    private static Stream<String> suggestions(String query) {
        // TODO: search using the query
        Stream<String> results = Stream.of("suggestion1", "suggestion2");
        return results;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String query = request.getParameter("q");

        Stream<String> results = suggestions(query);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET");
        response.setStatus(HttpServletResponse.SC_OK);

        try (PrintWriter out = response.getWriter()) {
            String elements = results.collect(Collectors.joining(","));
            String json     = "{\"suggestions\":[%s]}";
            out.printf(json, elements);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
