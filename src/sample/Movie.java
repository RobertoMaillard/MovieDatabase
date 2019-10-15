package sample;

import java.util.ArrayList;
import java.util.List;

public class Movie {

    private String movieId;
    private String title;
    private String year;
    private String country;
    private String genre;
    private String director;
    private int minutes;
    private String poster;
    private int raters;
    private double overallRating;
    private List<Rating> ratingList;



    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getRaters() {
        return raters;
    }

    public List<Rating> getRatingList() {
        return ratingList;
    }

    public void addRatingList(List<Rating> ratingList) {

        if(this.ratingList == null)
            this.ratingList = ratingList;
        else {
            for(Rating rating : ratingList)
                this.ratingList.add(rating);
        }
        updateOverallRating();

    }

    public double getOverallRating() {

        if(ratingList == null)
            return -1;
        else {
            return overallRating;
        }

    }

    public void addRating(Rating rating) {

        if(ratingList == null)
            ratingList = new ArrayList<>();

        ratingList.add(rating);
        updateOverallRating();

    }

    private void updateOverallRating() {

        int sumOfRating = 0;
        for(Rating rating : ratingList) {
            sumOfRating += rating.getValue();
        }

        raters = ratingList.size();
        double overallRating = (double) sumOfRating / raters;
        overallRating = Math.round(overallRating * 10.0) / 10.0;
        this.overallRating = overallRating;

    }

}
