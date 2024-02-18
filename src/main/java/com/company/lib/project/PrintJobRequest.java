package com.company.lib.project;

import lombok.Data;

@Data
public class PrintJobRequest {
    private String uuid;
    private Link link;
    public PrintJobRequest(Link link, String uuid){
        this.link = link;
        this.uuid = uuid;
    }
}
