package com.cmpn306.queryprocessor;

import com.cmpn306.ranker.Ranker;
import com.cmpn306.util.Stemmer;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class QueryProcessor {
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

    public static List<QueryResult> process(String query) {
        System.out.println("Processing query: " + query);
        QueryItem tokens = tokenize(query);
        System.out.println("Phrases: " + tokens.phrases());
        System.out.println("Excluded: " + tokens.excluded());
        System.out.println("Included: " + tokens.included());
        System.out.println("Words: " + tokens.words());

        List<QueryResult> results = new ArrayList<>();

        List<String> tokensList = Stream.of(tokens.included(), tokens.phrases(), tokens.words())
                                        .flatMap(List::stream)
                                        .distinct()
                                        .toList();

        //Ranker.rank(tokensList, results); TODO: fix function call

        return results;
    }

    private record QueryItem(List<String> phrases, List<String> excluded, List<String> included, List<String> words) { }

    public record QueryResult(String title, String url, String snippet) {
        String toJSON() {
            return String.format("{\"title\":\"%s\",\"url\":\"%s\",\"snippet\":\"%s\"}", title, url, snippet);
        }
    }
}