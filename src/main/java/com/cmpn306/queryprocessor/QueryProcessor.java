package com.cmpn306.queryprocessor;

import com.cmpn306.ranker.QueryPageResult;
import com.cmpn306.util.Stemmer;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
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
        Matcher phraseMatcher = PHRASE.matcher(query);
        List<String> phrases = phraseMatcher.results()
                                            .map(QueryProcessor::processMatch)
                                            .filter(s -> !s.isBlank())
                                            .distinct()
                                            .toList();
        query = phraseMatcher.replaceAll("");

        Matcher excludedMatcher = EXCLUDED.matcher(query);
        List<String> excluded = excludedMatcher.results()
                                               .map(QueryProcessor::processMatch)
                                               .map(Stemmer::stem)
                                               .filter(s -> !s.isBlank())
                                               .distinct()
                                               .toList();
        query = excludedMatcher.replaceAll("");

        Matcher includedMatcher = INCLUDED.matcher(query);
        List<String> included = includedMatcher.results()
                                               .map(QueryProcessor::processMatch)
                                               .map(Stemmer::stem)
                                               .filter(s -> !s.isBlank())
                                               .distinct()
                                               .toList();
        query = includedMatcher.replaceAll("");

        Matcher wordMatcher = WORD.matcher(query);
        List<String> words = wordMatcher.results()
                                        .map(QueryProcessor::processMatch)
                                        .map(Stemmer::stem)
                                        .filter(s -> !s.isBlank())
                                        .distinct()
                                        .toList();

        return new QueryItem(phrases, excluded, included, words);
    }

    public static Stream<QueryResult> process(String query, int page, boolean lucky) {
        QueryItem tokens = tokenize(query);

        List<String> tokensList = Stream.of(tokens.included(), tokens.phrases(), tokens.words())
                                        .flatMap(List::stream)
                                        .distinct()
                                        .toList();

        // TODO: search using the tokensList and the page number and lucky

        QueryResult qr1 = new QueryResult("title1", "url1", "snippet1");
        QueryResult qr2 = new QueryResult("title2", "url2", "snippet2");

        Stream<QueryResult>                    results    = Stream.of(qr1, qr2);
        HashMap<String, List<QueryPageResult>> resultsMap = null;
        //        Ranker.rank(resultsMap);

        return results;
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        String  query  = request.getParameter("q");
        String  page_s = request.getParameter("page");
        boolean lucky  = request.getParameter("lucky") != null;

        int page = page_s == null ? 1 : Integer.parseInt(page_s.trim().replaceAll("/$", ""));

        Stream<QueryResult> results = process(query, page, lucky);

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET");
        response.setStatus(HttpServletResponse.SC_OK);

        try (PrintWriter out = response.getWriter()) {
            String elements = results.map(QueryResult::toJson).collect(Collectors.joining(","));
            String json     = "{\"total\":%d,\"results\":[%s]}";
            out.printf(json, 0, elements);
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
}