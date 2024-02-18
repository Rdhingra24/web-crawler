package com.company.lib.project;

import lombok.Data;

import java.util.Set;

/**
 * Model to store the base URL and the child URLs
 */
@Data
public class Link {
    private String baseUrl;
    Set<String> childUrl;
    public Link(String baseUrl, Set<String> childUrl){
        this.baseUrl = baseUrl;
        this.childUrl = childUrl;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(" Visited URL: ").append(baseUrl).append("\nContains:\n");

        int index = 1;
        for (String url : childUrl) {
            builder.append("  ").append(index++).append(". ").append(url).append("\n");
        }

        return builder.toString();
    }
}
