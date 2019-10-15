package sample;

public class Rating {

    private String movieId;
    private int value;

    public Rating(String movieId, int value) {
        this.movieId = movieId;
        this.value = value;
    }

    public String getMovieId() {
        return movieId;
    }

    public int getValue() {
        return value;
    }
}
