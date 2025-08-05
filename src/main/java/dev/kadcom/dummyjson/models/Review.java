package dev.kadcom.dummyjson.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class Review {
    private int rating;
    private String comment;
    private String date;
    private String reviewerName;
    private String reviewerEmail;
    
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public String getDate() { return date; }
    public String getReviewerName() { return reviewerName; }
    public String getReviewerEmail() { return reviewerEmail; }
    
    public Optional<LocalDateTime> getParsedDate() {
        try {
            return Optional.of(LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
    
    public boolean isPositive() {
        return rating >= 4;
    }
    
    public boolean isNegative() {
        return rating <= 2;
    }
    
    @Override
    public String toString() {
        return "Review{" +
                "rating=" + rating +
                ", comment='" + comment + '\'' +
                ", reviewerName='" + reviewerName + '\'' +
                '}';
    }
}