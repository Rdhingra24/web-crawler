package com.company.lib.project;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CrawlerTest {
    private QueueManager queueManager;
    private Crawler crawler;
    String url =null;
    String uuid;

    @BeforeEach
    public void setup() {
        queueManager = new QueueManager() ;
        url = "http://test.com";
        CrawlRequest request = new CrawlRequest(url);
        uuid = request.getUuid();
        queueManager.addToCrawlQueue(request);
        crawler = new Crawler(queueManager);
    }

    @Test
    void testCrawl() throws IOException {
        File file = TestUtils.getFile("crawlerTest/index.html");
        Document content = Jsoup.parse(file, "UTF-8","http://test.com");

        try(MockedStatic<Helper> mockedStatic = mockStatic(Helper.class)){
            mockedStatic.when(() -> Helper.isValidUrl(url)).thenReturn(true);
            mockedStatic.when(()->Helper.getDocument(url)).thenReturn(content);
            mockedStatic.when(()->Helper.linkReachable(url)).thenReturn(true);
            mockedStatic.when(()->Helper.isUrlAllowed(url)).thenReturn(true);
            mockedStatic.when(()-> Helper.sameDomain(anyString(),anyString())).thenReturn(true);

            PrintJobRequest result = crawler.call();

            // print request is not null
            assertNotNull(result);

            //print request has same trace ID as that of crawl request
            assertEquals(uuid, result.getUuid());

            //print job has two child URLs as scanned from index
            assertEquals(2, result.getLink().getChildUrl().size());
        }
    }
}