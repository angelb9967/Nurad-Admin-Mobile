package com.example.nuradadmin.Models;

public class Model_Feedback {
    private String feedbackId, message, option, submissionDate, userId;
    private int rating;
    public Model_Feedback() {
    }

    public Model_Feedback(String feedbackId, String message, String option, int rating, String submissionDate, String userId) {
        this.feedbackId = feedbackId;
        this.message = message;
        this.option = option;
        this.rating = rating;
        this.submissionDate = submissionDate;
        this.userId = userId;
    }

    public String getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(String feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
