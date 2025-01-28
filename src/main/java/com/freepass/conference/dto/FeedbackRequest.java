package com.freepass.conference.dto;

import com.freepass.conference.enums.FeedbackRating;

public class FeedbackRequest {

    private String content;

    private FeedbackRating rating;

    public FeedbackRequest(String content, FeedbackRating rating) {
        this.content = content;
        this.rating = rating;
    }

    public String getContent() {
        return content;
    }

    public FeedbackRating getFeedbackRating() {
        return rating;
    }
}
