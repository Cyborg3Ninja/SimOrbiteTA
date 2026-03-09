package ca.qc.bdeb.sim.simorbite;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        JavaFX app = new JavaFX();
        app.start(stage);

    }

    public static void main(String[] args) {
        launch();
    }

}
