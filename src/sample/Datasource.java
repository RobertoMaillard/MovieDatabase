package sample;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Datasource {

    private static final String DB_NAME = "movieDB.sqlite";

    private static final String CONNECTION_STRING =
            "jdbc:sqlite:/Users/Roberto/Dropbox/CodeFolder/IntelliJ/JavaFX-Projects/MovieDB/" + DB_NAME;

    private static final String SELECT_ALL_FROM_MOVIES = "SELECT * FROM movies";
    private static final String SELECT_ALL_FROM_RATINGS = "SELECT * FROM ratings";
    private PreparedStatement queryMovies;
    private PreparedStatement queryRatings;

    Map<String, List<Rating>> movieRatingMap;
    Map<String, Map<String, Rating>> raterRatingMap;

    private static Datasource instance = new Datasource();

    private Connection connection;

    static Datasource getInstance() {
        return instance;
    }


    private Datasource() {
    }

    public Map<String, List<Rating>> getMovieRatingMap() {
        return movieRatingMap;
    }

    public Map<String, Map<String, Rating>> getRaterRatingMap() {
        return raterRatingMap;
    }

    boolean open() {

        try {

            connection = DriverManager.getConnection(CONNECTION_STRING);
            queryMovies = connection.prepareStatement(SELECT_ALL_FROM_MOVIES);
            queryRatings = connection.prepareStatement(SELECT_ALL_FROM_RATINGS);

            return true;

        } catch (SQLException e) {
            System.out.println("Couldn't connect to the database: " + e.getMessage());
            e.printStackTrace();

            return false;
        }

    }

    public void close() {

        try {

            if(queryMovies != null)
                queryMovies.close();

            if(queryRatings != null)
                queryRatings.close();

        } catch (SQLException e) {
            System.out.println("Couldn't disconnect from the database");
        }

    }

    List<Movie> movieList() {

        try(Statement movieStatement = connection.createStatement();
            ResultSet resultSetMovies = movieStatement.executeQuery(SELECT_ALL_FROM_MOVIES);
            Statement ratingStatement = connection.createStatement();
            ResultSet resultSetRating = ratingStatement.executeQuery(SELECT_ALL_FROM_RATINGS)) {

            // LOOP THROUGH THE RATING QUERY ResultSet AND POPULATED THE HashMap WITH RATING OBJECTS
            movieRatingMap = new HashMap<>();
            raterRatingMap = new HashMap<>();
            while(resultSetRating.next()) {
                String raterId = resultSetRating.getString(1);
                String movieId = resultSetRating.getString(2);
                int ratingValue = resultSetRating.getInt(3);
                Rating rating = new Rating(movieId, ratingValue);

                List<Rating> movieRatingList;
                if(movieRatingMap.containsKey(movieId))
                    movieRatingList = movieRatingMap.get(movieId);
                else
                    movieRatingList = new ArrayList<>();

                movieRatingList.add(rating);
                movieRatingMap.put(movieId, movieRatingList);

                Map<String, Rating> ratingMap;
                if(raterRatingMap.containsKey(raterId)) {
                    ratingMap = raterRatingMap.get(raterId);
                    ratingMap.put(movieId,rating);
                }
                else {
                    ratingMap = new HashMap<>();
                    ratingMap.put(movieId,rating);
                }
                raterRatingMap.put(raterId, ratingMap);

            }

            // LOOP THROUGH THE MOVIE QUERY ResultSet AND POPULATED THE ArrayList WITH MOVIE OBJECTS
            List<Movie> movies = new ArrayList<>();
            while(resultSetMovies.next()) {
                Movie movie = new Movie();

                String movieId = resultSetMovies.getString(1);
                movie.setMovieId(movieId);
                movie.setTitle(resultSetMovies.getString(2));
                movie.setYear(resultSetMovies.getString(3));
                movie.setCountry(resultSetMovies.getString(4));
                movie.setGenre(resultSetMovies.getString(5));
                movie.setDirector(resultSetMovies.getString(6));
                movie.setMinutes(resultSetMovies.getInt(7));
                movie.setPoster(resultSetMovies.getString(8));
                movie.addRatingList(movieRatingMap.get(movieId));

                movies.add(movie);
            }
            return movies;

        } catch (SQLException e) {
            System.out.println("Get all movies and raters query failed: " + e.getMessage());
            return null;
        }

    }
}
