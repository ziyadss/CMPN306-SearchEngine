package com.cmpn306.queryprocessor;

import com.cmpn306.database.Database;
import com.cmpn306.ranker.QueryPageResult;
import com.cmpn306.util.Stemmer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.management.Query;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@WebServlet(name = "QueryProcessor", urlPatterns = {"/search", "/search/"})
public class QueryProcessor extends HttpServlet {
    private static final int     PAGE_ITEM_COUNT = 10;
    private static final Pattern PHRASE          = Pattern.compile("\"([^\"]+?)\"");
    private static final Pattern EXCLUDED        = Pattern.compile("-(\\w+)");
    private static final Pattern INCLUDED        = Pattern.compile("\\+(\\w+)");
    private static final Pattern WORD            = Pattern.compile("(\\w+)");

    private static String processMatch(MatchResult match) {
        return match.group(1).replaceAll("[^\\w\\s]+\\s*|\\s+", " ").trim();
    }

    private static QueryItem tokenize(String query) {
        Matcher phraseMatcher = PHRASE.matcher(query); List<String> phrases = phraseMatcher.results()
                                                                                           .map(QueryProcessor::processMatch)
                                                                                           .filter(s -> !s.isBlank())
                                                                                           .distinct()
                                                                                           .toList();
        query = phraseMatcher.replaceAll("");

        Matcher excludedMatcher = EXCLUDED.matcher(query); List<String> excluded = excludedMatcher.results()
                                                                                                  .map(QueryProcessor::processMatch)
                                                                                                  .map(Stemmer::stem)
                                                                                                  .filter(s -> !s.isBlank())
                                                                                                  .distinct()
                                                                                                  .toList();
        query = excludedMatcher.replaceAll("");

        Matcher includedMatcher = INCLUDED.matcher(query); List<String> included = includedMatcher.results()
                                                                                                  .map(QueryProcessor::processMatch)
                                                                                                  .map(Stemmer::stem)
                                                                                                  .filter(s -> !s.isBlank())
                                                                                                  .distinct()
                                                                                                  .toList();
        query = includedMatcher.replaceAll("");

        Matcher wordMatcher = WORD.matcher(query); List<String> words = wordMatcher.results()
                                                                                   .map(QueryProcessor::processMatch)
                                                                                   .map(Stemmer::stem)
                                                                                   .filter(s -> !s.isBlank())
                                                                                   .distinct()
                                                                                   .toList();

        return new QueryItem(phrases, excluded, included, words);
    }

    public static List<QueryResult> process(String query, int page, boolean lucky) throws SQLException {
        QueryItem tokens = tokenize(query);

        List<String> tokensList = Stream.of(tokens.included(), tokens.phrases(), tokens.words())
                                        .flatMap(List::stream)
                                        .distinct()
                                        .toList();

        // TODO: search using the tokensList and the page number and lucky
        //formulate queries from token list, and then send them to the
        List<QueryResult>                      queryResults = new ArrayList<QueryResult>();
        HashMap<String, List<QueryPageResult>> resultsMap   = null;
        HashMap<QueryPageResult, Boolean>      excludeMap   = new HashMap<QueryPageResult, Boolean>();
        HashMap<QueryPageResult, Boolean>      includeMap   = new HashMap<QueryPageResult, Boolean>();
        //for every token, search up the index and return the query results

        for (String token: tokens.excluded()) {

            List<QueryPageResult> currentQueryResult = Database.query(
                    "SELECT documents.word, word_document.wordCount, documents.wordCount, documents.content, documents.pageTitle, documents.pageTitle FROM word_document, documents WHERE word_document.word = '" + token + "' AND word_document.docUrl = documents.docUrl",
                    QueryProcessor::resultToQueryPageResult);

            for (QueryPageResult queryPageResult: currentQueryResult)
                excludeMap.put(queryPageResult, true);
        }

        for (String token: tokens.included()) {

            List<QueryPageResult> currentQueryResult = Database.query(
                    "SELECT documents.word, word_document.wordCount, documents.wordCount, documents.content, documents.pageTitle, documents.pageTitle FROM word_document, documents WHERE word_document.word = '" + token + "' AND word_document.docUrl = documents.docUrl",
                    QueryProcessor::resultToQueryPageResult);

            for (QueryPageResult queryPageResult: currentQueryResult)
                includeMap.put(queryPageResult, true);
        }

        //need to join documents --> word_document
        for (String token: tokens.words()) {
            List<QueryPageResult> currentQueryResult = Database.query(
                    "SELECT documents.word, word_document.wordCount, documents.wordCount, documents.content, documents.pageTitle, documents.pageTitle FROM word_document, documents WHERE word_document.word = '" + token + "' AND word_document.docUrl = documents.docUrl",
                    QueryProcessor::resultToQueryPageResult);

            currentQueryResult.removeIf(excludeMap::containsKey); assert resultsMap != null;
            resultsMap.put(token, currentQueryResult);
        }

        for (String token: tokens.phrases()) {

            List<QueryPageResult> currentQueryResult = Database.query(
                    "SELECT documents.word, word_document.wordCount, documents.wordCount, documents.content, documents.pageTitle, documents.pageTitle FROM word_document, documents WHERE word_document.content LIKE '%'" + token + "%' AND word_document.docUrl = documents.docUrl",
                    QueryProcessor::resultToQueryPageResult);

            currentQueryResult.removeIf(excludeMap::containsKey); if (!includeMap.isEmpty())
                currentQueryResult.removeIf(queryPageResult -> !includeMap.containsKey(queryPageResult));
            assert resultsMap != null; resultsMap.put(token, currentQueryResult);
        }

        List<QueryResult> results = new ArrayList<QueryResult>();

        assert resultsMap != null; for (Map.Entry<String, List<QueryPageResult>> entry: resultsMap.entrySet()) {
            for (QueryPageResult queryPageResult: entry.getValue()) {
                results.add(new QueryResult(queryPageResult.getTitle(),
                                            queryPageResult.getDocUrl(),
                                            queryPageResult.getSnippet(entry.getKey())));
            }
        }

        if (lucky) return Collections.singletonList(results.get(0));

        return results;


    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String  query = request.getParameter("q"); String page_s = request.getParameter("page");
        boolean lucky = request.getParameter("lucky") != null;

        int page = page_s == null ? 1 : Integer.parseInt(page_s.trim().replaceAll("/$", ""));

        List<QueryResult> results = null;
        try {
            results = process(query, page, lucky);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        response.setContentType("application/json"); response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET"); response.setStatus(HttpServletResponse.SC_OK);

        try (PrintWriter out = response.getWriter()) {
            String elements = results.map(QueryResult::toJson).collect(Collectors.joining(","));
            String json     = "{\"total\":%d,\"results\":[%s]}"; out.printf(json, 0, elements);
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private record QueryItem(List<String> phrases, List<String> excluded, List<String> included, List<String> words) { }

    public record QueryResult(String title, String url, String snippet) {
        String toJson() {
            return String.format("{\"title\":\"%s\",\"url\":\"%s\",\"snippet\":\"%s\"}", title, url, snippet);
        }
    }

    private static QueryPageResult resultToQueryPageResult(ResultSet result) {
        try {
            return new QueryPageResult(result.getString("word_document.word"),
                                       result.getString("word_document.docUrl"),
                                       result.getInt("word_document.wordCount"),
                                       result.getInt("documents.wordCount"),
                                       result.getString("documents.content"),
                                       result.getString("documents.pageTitle"),
                                       result.getDouble("documents.pageRank"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
