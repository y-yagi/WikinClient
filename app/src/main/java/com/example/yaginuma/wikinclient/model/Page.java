package com.example.yaginuma.wikinclient.model;

import java.io.Serializable;
import java.net.URL;

/**
 * Created by yaginuma on 14/07/08.
 */
public class Page implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String title;
    private String body;
    private String extractedBody;
    private String url;

    public Page(int id, String title, String body, String extractedBody, String url) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.extractedBody = extractedBody;
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {

        this.body = body;
    }

    public String getExtractedBody() {
        return extractedBody;
    }

    public void setExtractedBody(String extractedBody) {
        this.extractedBody = extractedBody;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
