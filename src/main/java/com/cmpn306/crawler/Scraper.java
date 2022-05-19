package com.cmpn306.crawler;

///IMPORTS//

import com.cmpn306.database.Database;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class Scraper {

    public List<String> Links = new LinkedList<>();
    public Document     htmlDocx;

    private static String getDomain(String URL1) {
        String originUrl = null;
        try {
            URL url = new URL(URL1);
            originUrl = url.getProtocol() + "://" + url.getHost();

        } catch (MalformedURLException ex) {
            //ex.printStackTrace();
            System.out.println(
                    "Malformed Exception.. Either no legal protocol could be found in a specification string or the string could not be parsed.");
        }

        return originUrl;
    }

    public void Scrape(
            String URL, Map<String, Vector<String>> nonAllowedSitesList, Map<String, Vector<String>> allowedSitesList
                      ) {
        try {
            boolean notAllowed       = false;
            boolean Allowed          = false;
            String  my_meta_KeyWords = " ";
            int     of_Importance    = 0;

            //if non-allowed list doesn't contain this domain + allowed doesn't also contain
            // then for sure it is a new domain to scraper so send to robot parser to decide
            if (!nonAllowedSitesList.containsKey(getDomain(URL)) && !allowedSitesList.containsKey(getDomain(URL)))
                robotRead(URL, nonAllowedSitesList, allowedSitesList);

            //if it is in non-allowed list--> we search for similar domains among non-allowed list
            // // and set not_allowed = true as a flag
            if (nonAllowedSitesList.containsKey(getDomain(URL))) {
                for (int i = 0; i < nonAllowedSitesList.get(getDomain(URL)).size(); i++) {

                    if (URL.startsWith(nonAllowedSitesList.get(getDomain(URL)).get(i)))
                        notAllowed = true;
                }
            }
            if (notAllowed)
            //then we check if it is in the allowed sites list, so we make sure if it is accepted similar domain
            {
                if (allowedSitesList.containsKey(getDomain(URL))) {
                    for (int i = 0; i < allowedSitesList.get(getDomain(URL)).size(); i++) {
                        if (URL.startsWith(allowedSitesList.get(getDomain(URL)).get(i)))
                            Allowed = true;
                    }
                }
                else //if not in the allowed list already then don't download and return
                    return;
            }
            if (notAllowed && !Allowed)
                return; //return don't continue to download

            Document htmlDocx = Jsoup.connect(URL).timeout(600000).ignoreContentType(true).ignoreHttpErrors(true).get();
            this.htmlDocx = Jsoup.parse(htmlDocx.toString());
            for (Element metaTag: this.htmlDocx.getElementsByTag("meta")) {
                if (metaTag.attr("name").equalsIgnoreCase("keywords")) {
                    my_meta_KeyWords = metaTag.attr("content").toLowerCase();
                    break;
                }
            }
            if (my_meta_KeyWords.toLowerCase().contains("news") || my_meta_KeyWords.toLowerCase()
                                                                                   .contains("movies") || my_meta_KeyWords.toLowerCase()
                                                                                                                          .contains(
                                                                                                                                  "tv") || my_meta_KeyWords.toLowerCase()
                                                                                                                                                           .contains(
                                                                                                                                                                   "radio") || my_meta_KeyWords.toLowerCase()
                                                                                                                                                                                               .contains(
                                                                                                                                                                                                       "music") || my_meta_KeyWords.toLowerCase()
                                                                                                                                                                                                                                   .contains(
                                                                                                                                                                                                                                           "sports") || my_meta_KeyWords.toLowerCase()
                                                                                                                                                                                                                                                                        .contains(
                                                                                                                                                                                                                                                                                "series") || my_meta_KeyWords.toLowerCase()
                                                                                                                                                                                                                                                                                                             .contains(
                                                                                                                                                                                                                                                                                                                     "programming") || my_meta_KeyWords.toLowerCase()
                                                                                                                                                                                                                                                                                                                                                       .contains(
                                                                                                                                                                                                                                                                                                                                                               "egypt") || my_meta_KeyWords.toLowerCase()
                                                                                                                                                                                                                                                                                                                                                                                           .contains(
                                                                                                                                                                                                                                                                                                                                                                                                   "ahly") || my_meta_KeyWords.toLowerCase()
                                                                                                                                                                                                                                                                                                                                                                                                                              .contains(
                                                                                                                                                                                                                                                                                                                                                                                                                                      "books") || my_meta_KeyWords.toLowerCase()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                  .contains(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                          "box office") || my_meta_KeyWords.toLowerCase()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                           .contains(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   "cinema") || my_meta_KeyWords.toLowerCase()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                .contains(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                        "breaking") || my_meta_KeyWords.toLowerCase()
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       .contains(
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               "java"))
                of_Importance = 1;
            Elements hyperLinks = htmlDocx.select("a[href]");
            for (Element link: hyperLinks) {
                this.Links.add(link.absUrl("href"));
                if (Links.size() > 30) {
                    break;
                }
            }
            String query = String.format(
                    "INSERT OR REPLACE INTO documents (pageTitle, docUrl, content, crawlTime, important) VALUES ('%s', '%s', '%s', %d, %d);",
                    htmlDocx.title().replaceAll("'", "''"),
                    URL,
                    htmlDocx.text().replaceAll("'", "''"),
                    System.currentTimeMillis(),
                    of_Importance);
            Database.update(query);

            if (getLinks().size() > 0) {
                query = "INSERT OR IGNORE INTO documents (pageTitle, docUrl, content) VALUES ";
                String to_add = getLinks().stream()
                                          .map(link -> String.format("(' ', '%s', ' ')", link))
                                          .collect(Collectors.joining(", "));
                Database.update(query + to_add + ";");

                query  = "INSERT OR REPLACE INTO web_graph (srcDocUrl, dstDocUrl) VALUES ";
                to_add = getLinks().stream()
                                   .map(link -> String.format("('%s', '%s')", URL, link))
                                   .collect(Collectors.joining(", "));
                Database.update(query + to_add + ";");
            }

        } catch (IOException ioe) {
            System.out.println("Error in the HTTP request " + ioe + URL);
        } catch (PatternSyntaxException ex) {
            System.out.println("Regex error " + ex);
        } catch (IllegalArgumentException ignored) {

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getLinks() {
        return this.Links;
    }

    private void robotRead(
            String url, Map<String, Vector<String>> forbiddenList, Map<String, Vector<String>> allowedList
                          ) throws IOException {
        boolean        start             = false;
        Vector<String> robotForAllAgents = new Vector<>();
        Vector<String> notAllowed        = new Vector<>();
        Vector<String> Allowed           = new Vector<>();
        String         Domain            = getDomain(url);
        Document       htmlDocument;
        try {
            htmlDocument = Jsoup.connect(Domain + "/robots.txt").get();
        } catch (IOException ioe) {
            // no robot so forbidden paths vector is empty
            forbiddenList.put(Domain, notAllowed);
            return;
        }
        String[] words = htmlDocument.text().split(" ");

        for (int i = 0; i < words.length - 2; i++) {
            //System.out.println(words[i]);
            if (words[i].equals("User-agent:") && (words[i + 1].equals("*"))) {
                start = true;
                i += 2;
            }
            if (start) {
                if (words[i].equals("User-agent:") && !(words[i + 1].equals("*")) || words[i].equals("#"))
                    break;
                robotForAllAgents.add(words[i]);
            }
        }

        for (int i = 0; i < robotForAllAgents.size() - 1; i++) {
            //System.out.println(robotForAll.get(i));
            if (robotForAllAgents.get(i).equals("Disallow:"))
                notAllowed.add(Domain + robotForAllAgents.get(i + 1));
            if (robotForAllAgents.get(i).equals("Allow:"))
                Allowed.add(Domain + robotForAllAgents.get(i + 1));
        }
        forbiddenList.put(Domain, notAllowed);
        allowedList.put(Domain, Allowed);

    }

    public void Rescrape(String url) {

        try {

            //Document htmlDocument = Jsoup.connect(url).get();
            Document htmlDocx   = Jsoup.connect(url)
                                       .timeout(600000)
                                       .ignoreContentType(true)
                                       .ignoreHttpErrors(true)
                                       .get();
            Elements hyperLinks = htmlDocx.select("a[href]");
            for (Element link: hyperLinks) {
                this.Links.add(link.absUrl("href"));
                if (Links.size() > 30) {
                    break;
                }
            }

            String query = "UPDATE documents SET indexTime = 0 WHERE url = '" + url + "'";
            Database.update(query);

            query = "DELETE FROM web_graph WHERE dstDocUrl = '" + url + "'";
            Database.update(query);

        } catch (IOException ioe) {
            System.out.println("Error in the HTTP request " + ioe + url);
        } catch (PatternSyntaxException e) {
            System.out.println("Regex error " + e);
        } catch (IllegalArgumentException ignored) {

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
