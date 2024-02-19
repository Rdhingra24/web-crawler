package com.company.lib.project;

import crawlercommons.robots.BaseRobotRules;
import crawlercommons.robots.SimpleRobotRulesParser;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/** Helper class to validate and compare URLs */
@Slf4j
public class Helper {
    public static String USER_AGENT="Mozilla/5.0";

    public static boolean isValidUrl(String url) {
        try {
            getUrl(url);
            return true;
        } catch (MalformedURLException e) {
            log.warn("Invalid URL provided [{}]",url);
            return false;
        }
    }

    public static boolean sameDomain(String givenUrl, String base) {
        try {
            URL givenLink = getUrl(givenUrl);
            URL baseLink = getUrl(base);

            return givenLink.getHost().equalsIgnoreCase(baseLink.getHost())
//                    || givenLink.getHost().endsWith("."+baseLink.getHost())
                    ;

        } catch (MalformedURLException e) {
            return false;
        }
    }

    static URL getUrl(String url) throws MalformedURLException {
        return new URL(url);
    }


    //get robots.txt and read the content of the page
    String getRobotTxt(String url) throws MalformedURLException {
        String urlStr = url+"/robots.txt";
        StringBuilder content = new StringBuilder();
        URL urlObj = new URL(urlStr);

        try (BufferedReader in = new BufferedReader(new InputStreamReader(urlObj.openStream()))){;
            String line;
            while ((line = in.readLine()) != null) {
                content.append(line);
                content.append(System.lineSeparator());
            }
        } catch (IOException e) {
            log.error("Error in reading robots.txt file", e);
        }
        return content.toString();
    }

    public static boolean isUrlAllowed(String link){
        try {
            URL url = new URL(link);
            URL robotsTxtUrl = new URL(url.getProtocol(), url.getHost(), "/robots.txt");

            URLConnection connection = robotsTxtUrl.openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z"); // Read to the end of the input stream
            String content = scanner.next();
            scanner.close();

            SimpleRobotRulesParser parser = new SimpleRobotRulesParser();
            BaseRobotRules rules = parser.parseContent(robotsTxtUrl.toString(), content.getBytes(), "text/plain", Collections.singleton(USER_AGENT));

            return rules.isAllowed(link);
        } catch (IOException e) {
            log.error("Error fetching or parsing robots.txt: []{}" , e.getMessage());
            return true;
        }

    }

    public static Document getDocument(String url) throws IOException {
        return Jsoup.connect(url).get();
    }

    public static boolean linkReachable(String url) {
        try{
            log.info("is link reachable [{}]",url);
            if(url.endsWith(".pdf") ){
                return false;
            }
            Connection.Response response = Jsoup.connect(url)
                    .ignoreContentType(true)
                    .execute();
            String contentType = response.header("Content-Type");
            if (contentType != null && contentType.toLowerCase().contains("application/pdf")) {
                return false;
            }
            return response.statusCode() == 200;
        }catch (IOException exception){
            log.trace("url [{}] not reachable.",url);
            return false;
        }
    }
}




