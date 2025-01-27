package com.freepass.conference.dto;

public class FeedbackRequest {

    private String content;

    public FeedbackRequest(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
