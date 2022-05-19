package com.cmpn306.crawler;

//////////// SET the project Compile output to specified location in instructions ////////////////
//IMPORTING UTILITIES, INPUT&OUTPUT FOR READING/WRITING ON FILES
//IMPORTING IO EXCEPTIONS OBJECTS AND FILE NOT FOUND EXCEPTION OBJECTS

import com.cmpn306.database.Database;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

//CLASS CRAWLER FOR CRAWLER ON WEB PAGES FROM A SEED LIST DEFINED IN SEED LIST TXT FILE
// IT IMPLEMENTS RUNNABLE FOR MULTI-THREADING NO. OF CRAWLERS

public class Crawler implements Runnable {

    public static        int numOfCrawlers = 8; //NUM OF CRAWLERS TO CRAWL DEFINED HERE
    private static final int maxNumPages   = 1000; //MAX NUM OF PAGES TO CRAWL
    Map<String, Vector<String>> nonAllowedSitesList; // MAPPING OF NON ALLOWED SITES TO CRAWL/ TO DOWNLOAD HTML DOCX FROM
    Map<String, Vector<String>> allowedSitesList; // MAPPING OF ALLOWED SITES TO CRAWL/ TO DOWNLOAD HTML DOCX FROM
    long                        ID; //EVERY URL IS GIVEN A UNIQUE ID FOR ENTRIES IN DATABASE

    //    Default 4 hours
    Date    toRecrawlTime;
    //A DATE TYPE VARIABLE FOR WHICH WE ARE GOING TO CHECK IF TIME FOR RECRAWL.. 4 HRS
    int     pass;
    boolean toRecrawl; // VARIABLE TO ALLOW RECRAWL OR ONE CRAWL IS ENOUGH, SET TRUE TO RECRAWL, FALSE TO CRAWL
    private final Set<String>  visitedSites; // VISITED WEBSITES ARE DEFINED IN A SET WITH BOOLEAN 1-0
    private       List<String> queueSites; // LIST OF QUEUE OF SITES waiting TO BE VISITED/CRAWLED

    //DEFAULT CONSTRUCTOR
    public Crawler(
            Set<String> visitedSites,
            List<String> queueSites,
            Map<String, Vector<String>> nonAllowedSitesList,
            Map<String, Vector<String>> allowedSitesList,
            long ID
                  ) {
        this.visitedSites        = visitedSites;
        this.queueSites          = queueSites;
        this.nonAllowedSitesList = nonAllowedSitesList;
        this.allowedSitesList    = allowedSitesList;
        this.ID                  = ID;
        toRecrawlTime            = new Date(); //INITIALIZING THE FIRST CRAWL TIME
        pass                     = 0;
        toRecrawl                = false; //SET TRUE IF YOU WANT INFINITE RECRAWLS
    }

    public static void readSeedList(List<String> queue) {
        try {
            File    myfileObject    = new File("seed_list.txt"); //name of seed file in project folder
            Scanner myScannerReader = new Scanner(myfileObject); //scanner reader object
            while (myScannerReader.hasNextLine()) { //check if not last line/not end of file
                String urlData = myScannerReader.nextLine(); //extracting the line into a string
                queue.add(urlData);
            }
            myScannerReader.close(); //close
        } catch (FileNotFoundException ex) {
            System.out.println("An Error has Occurred During Reading URL Seed List...");
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) throws InterruptedException, SQLException {
        //record start time in Millis
        //for calculating and recording performance of Crawler Process
        Set<String>                 visitedSites;
        List<String>                queueSites;
        Map<String, Vector<String>> nonAllowedSitesList = new HashMap<>();
        Map<String, Vector<String>> allowedSitesList    = new HashMap<>();
        List<Thread>                listOfThreads       = new LinkedList<>();
        Thread                      th; //dummy for thread initiation and adding to list

        String query = "SELECT docUrl FROM documents WHERE crawlTime = 0;";
        queueSites   = Database.INSTANCE.query(query, Crawler::getURL);
        query        = "SELECT docUrl FROM documents WHERE crawlTime > 0;";
        visitedSites = new HashSet<>(Database.INSTANCE.query(query, Crawler::getURL));
        if (visitedSites.size() == 0 && queueSites.size() == 0) //if visited and queue is empty then this the first run
        // we need to generate urls from seed_list.txt file previous chosen
        {
            readSeedList(queueSites);
        }

        for (int i = 0; i < numOfCrawlers; i++) {
            th = new Thread(new Crawler(visitedSites, queueSites, nonAllowedSitesList, allowedSitesList, i));
            //index is the id
            listOfThreads.add(th);
            String threadName = Integer.toString(i); // id .equals name of thread
            th.setName(threadName);
            th.start();
        }
        for (int i = 0; i < numOfCrawlers; i++) {
            listOfThreads.get(i).join();
            // we wait for them to join for them to all end here to calculate the time as one pass/loop ends
        }
    }

    private static String getURL(ResultSet rs) {
        try {
            return rs.getString("docUrl");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void Recrawl() throws SQLException {
        String current_URL;

        while (true) {
            if (Thread.currentThread().getName().equals("0")) { //checking first thread of zero ID
                //                queueSites.clear(); //we are repeating, so flushing queue
                String query;
                if (pass == 0) {
                    // Visited ones only
                    query = "SELECT docUrl FROM documents WHERE crawlTime > 0;";
                    pass  = 1;
                }
                else {
                    query = "SELECT docUrl FROM documents WHERE important = 1;";
                    pass  = 0;
                }
                queueSites = Database.INSTANCE.query(query, Crawler::getURL);
            }
            while ((new Date().after(toRecrawlTime))) {
                //System.out.println("A New Crawl Process is Initiated1.. ");
            }
            //time flag condition for recrawl returns false if current time hasnot passed
            //else
            System.out.println("A New Crawl Process is Initiated.. at time: " + new Date());
            while (true) {  // recrawl every 4 hours... set by default in database in minutes or hours as you wish

                synchronized (queueSites) //here we lock on the resource queue for multi threading
                {
                    if (queueSites.isEmpty()) //checing if queue is empty at this moment
                    {
                        //if yes go out we finished and update the time of last recrawl
                        //                        DB.updateDate(time2);
                        break;
                    }
                }
                current_URL = null;
                synchronized (queueSites) { //we also lock on the resource queue but now for extraction of next url
                    if (!queueSites.isEmpty()) //checking at this moment isnot empty
                    {
                        current_URL = queueSites.remove(0); //extracting the first on queue and then shifting in list
                    }
                }
                if (current_URL != null) {   //if there exist a useful (a non null) url start scraping and robot parsing instructions
                    Scraper myScraper = new Scraper();
                    myScraper.Rescrape(current_URL);
                }
            }

        }
    }

    public void Crawl() {
        System.out.println("First Crawl Process is Initiated.. at time: " + new Date());
        String current_URL;

        while (true) {
            synchronized (visitedSites) //we lock on the resource visited sites at this moment for each thread
            {
                if (visitedSites.size() > maxNumPages) //limited by max num pages.. if true we are finished crawling pages
                    break;
            }
            current_URL = null;
            synchronized (queueSites) // if not we lock on the queue state we shall extract from it later urls to visit
            {
                if (!queueSites.isEmpty()) //check if empty at the moment
                {
                    synchronized (visitedSites) //lock on visited sites for checking
                    {
                        try {
                            String next_URL = queueSites.remove(0); //extract first one of queue FIFO and Shift
                            current_URL = visitedSites.contains(next_URL) ? null : next_URL;
                            // if it is already visited before set to null else next url is current url to be visited
                            //

                        } catch (Exception ignoredURL) {
                            // in case extraction was null or some error occured in parsing string

                        }
                        if (current_URL != null) { // ifnot null we now add it to visited list
                            visitedSites.add(current_URL);
                        }
                    }
                }
            }
            if (current_URL != null) { //if not null strart scraping
                Scraper myScraper = new Scraper();
                myScraper.Scrape(current_URL, nonAllowedSitesList, allowedSitesList);
                try {
                    queueSites.addAll(myScraper.getLinks());
                } catch (IndexOutOfBoundsException ignored) {
                }
            }
        }
        Date time = new Date();
        //        DB.updateDate(time); //at the end of crawl we update time of last crawl fo next crawling prompt
    }

    @Override
    public void run() {
        //record start time in Millis

        Crawl(); //main function of crawling

        if (toRecrawl)//toRecrawl)  //the boolean variable,, set to true if you want to recrawl
        {
            try {
                Recrawl(); //repeating crawling
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        }
    }

}