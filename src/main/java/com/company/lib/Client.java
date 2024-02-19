package com.company.lib;

import com.company.lib.print.Printer;
import com.company.lib.project.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * Main class to start the web crawler and print the results
 */
@Slf4j
public class Client {

    public static void main(String[] args) throws InterruptedException {
        validateInput(args);

        long start = System.currentTimeMillis();
        QueueManager processingQueue = new QueueManager(10000);

        String inputUrl = args[0];
        System.out.println("Input URL : "+inputUrl);

        CrawlRequest request = new CrawlRequest(inputUrl);
        processingQueue.addToCrawlQueue(request);

        ExecutorService crawlersPool = Executors.newFixedThreadPool(100);
        ExecutorService printerPool = Executors.newFixedThreadPool(100);

      while(processingQueue.hasLinksToCrawl() || processingQueue.hasJobsToPrint()){
            Future<PrintJobRequest> future = crawlersPool.submit(new Crawler(processingQueue));
            try {
                PrintJobRequest elements = future.get();
                processingQueue.submitPrintJobToQueue(elements);
            } catch (InterruptedException | ExecutionException e) {
                log.error("Exception while reading message from queue [{}]",e);
                e.printStackTrace();
            }
            printerPool.submit(new Printer(processingQueue));
        }

        crawlersPool.awaitTermination(5, TimeUnit.SECONDS);
       printerPool.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("Process complete. Total time taken "+(System.currentTimeMillis()-start)/1000+" seconds");
        System.exit(0);
    }

    private static void validateInput(String[] args) {
        if(args.length == 0){
            System.out.println("Please provide a URL to start crawling");
            System.exit(1);
        }
    }
}
