package com.example.cinego;

public class Movie {
    private String id;
    private String title;
    private String posterUrl;
    private double rating;
    private int ratingCount;
    private String genre;
    private String synopsis;
    private String actors;
    private String trailerUrl;

    public Movie() {
    }

    public Movie(String id, String title, String posterUrl, double rating, int ratingCount, String genre, String synopsis, String actors) {
        this.id = id;
        this.title = title;
        this.posterUrl = posterUrl;
        this.rating = rating;
        this.ratingCount = ratingCount;
        this.genre = genre;
        this.synopsis = synopsis;
        this.actors = actors;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public void setPosterUrl(String posterUrl) {
        this.posterUrl = posterUrl;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getRatingCount() {
        return ratingCount;
    }

    public void setRatingCount(int ratingCount) {
        this.ratingCount = ratingCount;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getSynopsis() {
        return synopsis;
    }

    public void setSynopsis(String synopsis) {
        this.synopsis = synopsis;
    }

    public String getActors() {
        return actors;
    }

    public void setActors(String actors) {
        this.actors = actors;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }
}