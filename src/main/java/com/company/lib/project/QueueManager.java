package com.company.lib.project;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Queue Manager that manages the queue for crawling and printing.
 * Keeps an eye on visited links to avoid duplicate processing.
 */
@Slf4j
@Data
public class QueueManager {
    Queue<CrawlRequest> crawlQueue;
    Queue<PrintJobRequest> printQueue ;
    /**
     * Set to keep track of visited links to avoid duplicate processing.
     */
    Set<String> visitedLinks = Collections.newSetFromMap(new ConcurrentHashMap<>());
    Map<String, Integer> visitedLinksMap = new HashMap<>();
    Map<String, Integer> visitedLinksMap1 = new HashMap<>();

    int printCounter = 0;
    int counter = 0;



    public QueueManager(){
        this.crawlQueue =  new ConcurrentLinkedQueue<>();
        this.printQueue = new ConcurrentLinkedQueue<>();
    }

    public void addToCrawlQueue(CrawlRequest request){
        String url = request.getUrl();
        log.info("Crawl request with ID [{}] received for [{}]",request.getUuid(),url);
        if(visitedLinks.add(url) ){
            crawlQueue.add(request);
            log.info("Size of the crawl queue [{}]",crawlQueue.size());
            log.info("crawling counter: [{}]",counter++);
        }else{
            log.info("Processing queue :: skipping visited url [{}] ",url);
            visitedLinksMap.put(url, visitedLinksMap.getOrDefault(url,0)+1);
            visitedLinksMap1.put(url, 1);

            log.info("skipped URLS, [{}]" , visitedLinksMap1.values().stream().mapToInt(Integer::intValue).sum());
        }
    }
    //504 324 739

    public void submitPrintJobToQueue(PrintJobRequest printJobRequest){
        Link requestLink = printJobRequest.getLink();
        if(Objects.nonNull(requestLink)){
            log.debug("Adding request [{}] to print queue for [{}]",printJobRequest.getUuid(),requestLink.getBaseUrl());
            log.info("print counter: [{}]",printCounter++);
            printQueue.add(printJobRequest);
            }else {
                log.trace("Attempted to add null to queue." );
            }
    }
    public synchronized PrintJobRequest getElementToPrint() throws InterruptedException {
        return this.printQueue.poll();
    }

    public synchronized CrawlRequest getCrawlRequest(){
        log.info("Existing elements: [{}]", crawlQueue.peek());
        log.info("Polled link [{}] from queue, size before polling: [{}]", crawlQueue.peek(), crawlQueue.size());

        CrawlRequest crawlRequest = this.crawlQueue.poll();

        log.info("Remaining elements: [{}], size after polling: [{}]", crawlQueue.peek(), crawlQueue.size());

        return crawlRequest;


    }

    public boolean hasJobsToPrint(){
        return !this.printQueue.isEmpty();
    }

    public boolean hasLinksToCrawl(){
        return !this.crawlQueue.isEmpty();
    }

}
