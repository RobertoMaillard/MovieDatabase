package sample;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MovieDBController {

    // top BorderPane
    @FXML
    Label topLabelMovieCount;
    @FXML
    Label topLabelRaterCount;
    @FXML
    Label topLabelRatingCount;

    // left BorderPane
    @FXML
    TextField searchField;
    @FXML
    ListView<String> genreListView;
    @FXML
    ListView<String> directorListView;
    @FXML
    ListView<String> yearListView;
    @FXML
    ListView<String> countryListView;

    // center BorderPane
    @FXML
    ImageView imageView;
    @FXML
    Label anchorRating;
    @FXML
    Label anchorTitle;
    @FXML
    Label anchorYear;
    @FXML
    Label anchorGenre;
    @FXML
    Label anchorDirector;
    @FXML
    Label anchorCountry;
    @FXML
    Label anchorLength;
    @FXML
    Label movieQuantityCount;

    @FXML
    TableView<Movie> movieTableView;

    private List<Movie> moviesList;
    private Map<String, Map<String, Rating>> raterRatingMap;

    void listMoviesAndRatings() {

        // RUNNING BACKGROUND WORK/TASK
        // QUERY SELECT * FROM movies
        // QUERY SELECT * FROM ratings
        Task<ObservableList<Movie>> task = new Task<>() {
            @Override
            protected ObservableList<Movie> call() {
                moviesList = Datasource.getInstance().movieList();
                moviesList.sort(Comparator.comparing(Movie::getTitle));

                return FXCollections.observableArrayList(moviesList);
            }
        };

        new Thread(task).start();

        task.setOnSucceeded(e -> {
            movieQuantityCount.setText("Movies: " + moviesList.size());
            movieTableView.setItems(task.getValue());
            movieTableView.getSelectionModel().select(moviesList.get(0));
            displayMovie(moviesList.get(0));
            populateListViews();
            updateMovieRaterRatingCount();
        });

    }

    private void updateMovieRaterRatingCount() {
        topLabelMovieCount.setText("Movies: "+ moviesList.size());

        raterRatingMap = Datasource.getInstance().getRaterRatingMap();
        topLabelRaterCount.setText("Raters: " + raterRatingMap.size());

        int ratings = 0;
        for(String s : raterRatingMap.keySet()) {
            ratings += raterRatingMap.get(s).size();
        }
        topLabelRatingCount.setText("Ratings: " + ratings);
    }

    private void populateListViews() {


        genreListView.setItems(FXCollections.observableList(listGenres()));
        genreListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        directorListView.setItems(FXCollections.observableList(listDirectors()));
        directorListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        yearListView.setItems(FXCollections.observableList(listYears()));
        yearListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        countryListView.setItems(FXCollections.observableList(listCountries()));
        countryListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

    }

    private List<String> listGenres() {
        return listItems(Movie::getGenre);
    }

    private List<String> listDirectors() {
        return listItems(Movie::getDirector);
    }

    private List<String> listYears() {
        return listItems(Movie::getYear);
    }

    private List<String> listCountries() {
        return listItems(Movie::getCountry);
    }

    private List<String> listItems(Function<Movie,String> itemFilter) {

        List<String> list = new ArrayList<>();

        for(Movie movie : moviesList) {
            String[] items = itemFilter.apply(movie).split(",");

            for (String s : items) {
                String item = s.trim();

                if(!list.contains(item))
                    list.add(item);

            }
        }

        Collections.sort(list);
        return list;
    }

    // ---
    @FXML
    private void filterGenre() {
        filterMovies(genreListView, Movie::getGenre);
    }

    @FXML
    private void filterDirector() {
        filterMovies(directorListView, Movie::getDirector);
    }

    @FXML
    private void filterYear() {
        filterMovies(yearListView, Movie::getYear);
    }

    @FXML
    private void filterCountry() {
        filterMovies(countryListView, Movie::getCountry);
    }

    private void filterMovies(ListView listView, Function<Movie,String> filter) {

        String selectedItem = (String) listView.getSelectionModel().getSelectedItem();

        Task<ObservableList<Movie>> task = new Task<>() {

            @Override
            protected ObservableList<Movie> call() {

                List<Movie> list = moviesList.stream()
                        .filter(movie -> filter.apply(movie).contains(selectedItem))
                        .collect(Collectors.toList());
                return FXCollections.observableArrayList(list);
            }
        };

        new Thread(task).start();
        task.setOnSucceeded(e -> {
            movieTableView.setItems(task.getValue());
            movieQuantityCount.setText("Movies: " + task.getValue().size());
        });
    }

    // ---
    public void onKeyTypedSearchFieldFilter() {

        String keyTypedString = searchField.getText().toLowerCase();

        if(keyTypedString.isEmpty()) {
            resetFilter();
        }
        else {

            Task<ObservableList<Movie>> task = new Task<>() {

                @Override
                protected ObservableList<Movie> call() {

                    List<Movie> list = moviesList.stream()
                                    .filter(movie -> movie.getTitle().toLowerCase().contains(keyTypedString))
                                    .collect(Collectors.toList());

                    return FXCollections.observableArrayList(list);
                }
            };

            new Thread(task).start();
            task.setOnSucceeded(e -> {
                movieTableView.setItems(task.getValue());
                movieQuantityCount.setText("Movies: " + task.getValue().size());
            });

        }
    }

    // ---
    @FXML
    public void resetFilter() {
        movieTableView.setItems(FXCollections.observableArrayList(moviesList));
        movieQuantityCount.setText("Movies: " + moviesList.size());
    }

    // ---
    @FXML
    private void displaySelectedItem() {

        Movie movie = movieTableView.getSelectionModel().getSelectedItem();
        displayMovie(movie);
        System.out.println(movie.getTitle());

    }

    private void displayMovie(Movie movie) {

            try {
                String url = movie.getPoster();
                System.out.println(url);
                Image image = new Image(url,200,0,false,false);
                imageView.setImage(image);

            } catch (Exception e) {
                System.out.println("Couldn't load image from url: " + e.getMessage());
            }

            anchorTitle.setText(movie.getTitle());
            anchorYear.setText("Year: \t" + movie.getYear());
            anchorGenre.setText("Genre: \t" + movie.getGenre());
            anchorDirector.setText("Director: \t" + movie.getDirector());
            anchorCountry.setText("Country: \t" + movie.getCountry());

            int length = movie.getMinutes();
            anchorLength.setText("Duration: " + (length/60) + "h " +(length%60) + "m");

            double overallRating = movie.getOverallRating();
            int raters = movie.getRaters();
            anchorRating.setText("Rating: \t" + overallRating + " / " + raters);

    }

}

