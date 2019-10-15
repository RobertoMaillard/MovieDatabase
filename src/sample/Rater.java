package sample;


import java.util.HashMap;
import java.util.Map;

public class Rater {

    private String raterId;
    private Map<String, Rating> ratingMap;

    public Rater(String raterId) {
        this.raterId = raterId;
        this.ratingMap = new HashMap<>();
    }

    public String getRaterId() {
        return raterId;
    }

    public Map<String, Rating> getRatingList() {
        return ratingMap;
    }

    public void addRating(String movieId, Rating rating) {
        ratingMap.put(movieId, rating);
    }
}
