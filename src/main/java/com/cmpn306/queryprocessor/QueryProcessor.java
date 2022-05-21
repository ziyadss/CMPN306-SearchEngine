package com.cmpn306.queryprocessor;

import com.cmpn306.database.Database;
import com.cmpn306.ranker.QueryPageResult;
import com.cmpn306.ranker.Ranker;
import com.cmpn306.util.Stemmer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

import static java.util.regex.Matcher.quoteReplacement;

@WebServlet(name = "QueryProcessor", urlPatterns = {"/search", "/search/"})
public class QueryProcessor extends HttpServlet {
    private static final int     PAGE_ITEM_COUNT = 10;
    private static final Pattern PHRASE          = Pattern.compile("\"([^\"]+?)\"");
    private static final Pattern EXCLUDED        = Pattern.compile("-(\\w+)");
    private static final Pattern INCLUDED        = Pattern.compile("\\+(\\w+)");
    private static final Pattern WORD            = Pattern.compile("(\\w+)");

    private static List<String> tokensList;

    private static String processMatch(MatchResult match) {
        return match.group(1).replaceAll("[^\\w\\s]+\\s*|\\s+", " ").trim();
    }

    private static QueryItem tokenize(String query) {
        Matcher phraseMatcher = PHRASE.matcher(query);
        List<String> phrases = phraseMatcher.results()
                                            .map(QueryProcessor::processMatch)
                                            .filter(s -> !s.isBlank())
                                            .toList();
        query = phraseMatcher.replaceAll("");

        Matcher excludedMatcher = EXCLUDED.matcher(query);
        List<String> excluded = excludedMatcher.results()
                                               .map(QueryProcessor::processMatch)
                                               .filter(s -> !s.isBlank())
                                               .toList();
        query = excludedMatcher.replaceAll("");

        Matcher includedMatcher = INCLUDED.matcher(query);
        List<String> included = includedMatcher.results()
                                               .map(QueryProcessor::processMatch)
                                               .filter(s -> !s.isBlank())
                                               .toList();
        query = includedMatcher.replaceAll("");

        Matcher wordMatcher = WORD.matcher(query);
        List<String> words = wordMatcher.results().map(QueryProcessor::processMatch).filter(s -> !s.isBlank()).toList();

        tokensList = Stream.of(words, phrases, included).flatMap(Collection::stream).distinct().toList();

        return new QueryItem(phrases.stream().distinct().toList(),
                             excluded.stream().distinct().map(Stemmer::stem).toList(),
                             included.stream().distinct().map(Stemmer::stem).toList(),
                             words.stream().distinct().map(Stemmer::stem).toList());
    }

    public static Response process(String query, int page, boolean lucky) throws SQLException {
        QueryItem tokens = tokenize(query);

        //formulate queries from token list, and then send them to the
        HashMap<String, List<QueryPageResult>> resultsMap = new HashMap<>();
        HashMap<QueryPageResult, Boolean>      excludeMap = new HashMap<>();
        HashMap<QueryPageResult, Boolean>      includeMap = new HashMap<>();
        //for every token, search up the index and return the query results

        for (String token: tokens.excluded()) {

            List<QueryPageResult> currentQueryResult = Database.query(
                    "SELECT documents.pageRank, word_document.word, word_document.wordCount as wwc, documents.wordCount as dwc, documents.content, documents.pageTitle, documents.docUrl FROM word_document, documents WHERE word_document.word = '" + token + "' AND word_document.docUrl = documents.docUrl and indexTime > 0;",
                    QueryProcessor::resultToQueryPageResult);

            for (QueryPageResult queryPageResult: currentQueryResult)
                excludeMap.put(queryPageResult, true);
        }

        for (String token: tokens.included()) {

            List<QueryPageResult> currentQueryResult = Database.query(
                    "SELECT documents.pageRank, word_document.word, word_document.wordCount as wwc, documents.wordCount as dwc, documents.content, documents.pageTitle, documents.docUrl FROM word_document, documents WHERE word_document.word = '" + token + "' AND word_document.docUrl = documents.docUrl and indexTime > 0;",
                    QueryProcessor::resultToQueryPageResult);

            for (QueryPageResult queryPageResult: currentQueryResult)
                includeMap.put(queryPageResult, true);
        }

        //need to join documents --> word_document
        for (String token: tokens.words()) {
            List<QueryPageResult> currentQueryResult = Database.query(
                    "SELECT documents.pageRank, word_document.word, word_document.wordCount as wwc, documents.wordCount as dwc, documents.content, documents.pageTitle, documents.docUrl FROM word_document, documents WHERE word_document.word = '" + token + "' AND word_document.docUrl = documents.docUrl and indexTime > 0;",
                    QueryProcessor::resultToQueryPageResult);

            currentQueryResult.removeIf(excludeMap::containsKey);
            resultsMap.put(token, currentQueryResult);
        }

        for (String token: tokens.phrases()) {

            List<QueryPageResult> currentQueryResult = Database.query(
                    "SELECT documents.pageRank, word_document.word, word_document.wordCount as wwc, documents.wordCount as dwc, documents.content, documents.pageTitle, documents.docUrl FROM word_document, documents WHERE documents.content LIKE '%" + token + "%' AND word_document.docUrl = documents.docUrl and indexTime > 0;",
                    QueryProcessor::resultToQueryPageResult);

            currentQueryResult.removeIf(excludeMap::containsKey);
            if (!includeMap.isEmpty())
                currentQueryResult.removeIf(queryPageResult -> !includeMap.containsKey(queryPageResult));
            resultsMap.put(token, currentQueryResult);
        }

        Ranker.rank(resultsMap);

        List<QueryResult> queryResults = resultsMap.values()
                                                   .stream()
                                                   .flatMap(List::stream)
                                                   .sorted(Comparator.comparing(QueryPageResult::getRelevanceScore)
                                                                     .reversed())
                                                   .map(QueryResult::new)
                                                   .toList();

        int               total;
        List<QueryResult> results;
        int               needed = lucky ? 1 : PAGE_ITEM_COUNT;
        if (queryResults.size() < needed) {
            total   = queryResults.size();
            results = queryResults;
        }
        else if (lucky) {
            total   = 1;
            results = Collections.singletonList(queryResults.get(0));
        }
        else {
            total   = queryResults.size();
            results = queryResults.subList((page - 1) * PAGE_ITEM_COUNT, Math.min(page * PAGE_ITEM_COUNT, total));
        }

        return new Response(total, results, tokensList);

    }

    private static QueryPageResult resultToQueryPageResult(ResultSet result) {
        try {
            return new QueryPageResult(result.getString("word"),
                                       result.getString("docUrl"),
                                       result.getInt("wwc"),
                                       result.getInt("dwc"),
                                       result.getString("content"),
                                       result.getString("pageTitle"),
                                       result.getDouble("pageRank"));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String  query  = request.getParameter("q");
        String  page_s = request.getParameter("page");
        boolean lucky  = request.getParameter("lucky") != null;

        int page = page_s == null ? 1 : Integer.parseInt(page_s.trim().replaceAll("/$", ""));

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET");
        response.setStatus(HttpServletResponse.SC_OK);

        try (PrintWriter out = response.getWriter()) {
            Response res      = process(query, page, lucky);
            String   elements = res.results().stream().map(QueryResult::toJson).collect(Collectors.joining(","));
            String tokens = res.tokens()
                               .stream()
                               .map(s -> "\"" + s.replaceAll("\"", "\\\"") + "\"")
                               .collect(Collectors.joining(","));
            String json = "{\"total\":%d,\"results\":[%s], \"tokens\":[%s]}";
            out.printf(json, res.total(), elements, tokens);
            Database.update("INSERT OR IGNORE INTO query (text) VALUES ('" + query.replaceAll("\"",
                                                                                              quoteReplacement("\"")) + "')");
        } catch (IOException | SQLException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    record Response(int total, List<QueryResult> results, List<String> tokens) { }

    private record QueryItem(List<String> phrases, List<String> excluded, List<String> included, List<String> words) { }

    public record QueryResult(String title, String url, String snippet) {
        QueryResult(QueryPageResult queryPageResult) {
            this(queryPageResult.getTitle(),
                 queryPageResult.getDocUrl(),
                 queryPageResult.getSnippet().replaceAll("\"", "\\\""));
        }

        String toJson() {
            return String.format("{\"title\":\"%s\",\"url\":\"%s\",\"snippet\":\"%s\"}", title, url, snippet);
        }
    }
}
