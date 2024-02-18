package com.company.lib.print;

import com.company.lib.project.Link;
import com.company.lib.project.PrintJobRequest;
import com.company.lib.project.QueueManager;
import lombok.extern.slf4j.Slf4j;


/**
 * Printer class to print the results
 */
@Slf4j
public class Printer implements Runnable {

    private QueueManager printingQueue;

    public Printer(QueueManager queue){
        this.printingQueue = queue;
    }

    @Override
    public void run() {
        try {
            PrintJobRequest printJobRequest = printingQueue.getElementToPrint();
            log.info("picked print request [{}] from queue to print",printJobRequest.getUuid(),printJobRequest);

            log.info(printJobRequest.getLink().toString());
            log.info("picked print request [{}] completed.",printJobRequest.getUuid(),printJobRequest);

        } catch (InterruptedException e) {
            log.error("Exception while reading message from queue [{}]",e);
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
