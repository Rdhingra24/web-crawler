package com.company.lib.project;

import lombok.Data;

@Data
public class CrawlRequest {
    private String uuid;
    private String url;
    public CrawlRequest(String url){
        this.url = url;
        this.uuid = java.util.UUID.randomUUID().toString();
    }

}
