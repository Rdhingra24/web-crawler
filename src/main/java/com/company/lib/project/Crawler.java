package com.company.lib.project;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import static com.company.lib.project.Helper.*;

/**
 * Responsible for reading the queue for a given URL, extracting child links,
 * and submitting them for further crawling.
 */
@Slf4j
public class Crawler implements Callable<PrintJobRequest> {
    public static final String MINUS_ONE = "minus one";
    private final QueueManager processingQueue;
    public Crawler(QueueManager queue){
        this.processingQueue = queue;
    }

    @Override
    public PrintJobRequest call() {
        try{
            if(!processingQueue.hasLinksToCrawl()){
               return  new PrintJobRequest(null,null);
            }
            CrawlRequest crawlRequest = processingQueue.getCrawlRequest();
            String targetUrl = crawlRequest.getUrl();
            long startTime = System.currentTimeMillis();

            log.info("picked request [{}] from queue to crawl",crawlRequest.getUuid(),targetUrl);
            Link link = crawl(targetUrl);

            long duration = System.currentTimeMillis() - startTime;
            log.info("picked request [{}] completed. took [{}] ms",crawlRequest.getUuid(), duration);

            return new PrintJobRequest(link,crawlRequest.getUuid());
        } catch (IOException e) {
            log.error("Exception while reading message from queue [{}]",e);
            throw new RuntimeException(e);
        }
    }

    public Link crawl(String url) throws IOException {
        if(!Helper.isValidUrl(url)){
            log.info(MINUS_ONE);

            log.error("Invalid URL provided [{}]",url);
            throw new IOException("Invalid URL provided");
        }
        Set<String> childUrl =  new HashSet<>();
        if(linkReachable(url)){
            try{
                Document document = getDocument(url);
                Elements linksOnPage = document.select("a[href]");
                for (Element element : linksOnPage) {
                    String link = element.attr("abs:href");
                    childUrl.add(link);
                }
                log.debug("base url [{}] has [{}] child-links",url, childUrl.size());
                Link link = new Link(url,childUrl);
                submitCrawlJobToQueue(childUrl,url);
                return link;
            }catch (Exception exception){
                log.info("minus one");
                log.error("Exception getting this document, though link is reachable [{}]",url);
                return null;
            }
        }
        log.info("minus one");
        return null;
    }

    private void submitCrawlJobToQueue(Set<String> links, String baseUrl) {
        links.stream()
                .filter(link -> sameDomain(link, baseUrl) && isUrlAllowed(link))
                .forEach(link -> {
                    log.debug("Adding child link to queue: [{}]", link);
                    processingQueue.addToCrawlQueue(new CrawlRequest(link));
                });
    }
}
