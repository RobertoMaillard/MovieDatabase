package sample;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void init() {
        // CONNECTS TO THE DATABASE
        if(!Datasource.getInstance().open()) {
            System.out.println("FATAL ERROR: Couldn't connect to database");
            Platform.exit();
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("movieDBWindow.fxml"));
        Parent root = fxmlLoader.load();

        // LOADS THE ListView ObservableList BEFORE SETTING THE SCENE
        MovieDBController controller = fxmlLoader.getController();
        controller.listMoviesAndRatings();

        // SETS THE SCENE
        primaryStage.setTitle("Movie Database");
        primaryStage.setScene(new Scene(root, 1280, 720));

        // SET STAGE BOUNDARIES TO VISIBLE BOUNDS OF THE MAIN SCREEN
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());

        primaryStage.show();

    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Datasource.getInstance().close();
    }
}
