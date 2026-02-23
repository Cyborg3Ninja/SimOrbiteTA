package ca.qc.bdeb.sim.simorbite;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.IOException;


public class JavaFX extends Application {

    final int WIDTH = 1000;
    final int HEIGHT = 1000;
    @Override
    public void start(Stage stage) throws IOException {


        Pane root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);
        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();

        // Initialisations
        ArrierePlan arrierePlan = new ArrierePlan();




        scene.setOnKeyPressed(event -> {


            switch (event.getCode()) {


                /*//case d = debug,
                case F -> montrerVecteur = !montrerVecteur; // Correction de l'erreur de toggle
                case L -> lancerTransition();
                case K -> Jeux.nbJournaux = 0;
                case Q -> Jeux.nbJournaux += 10;
                case I -> Jeux.particulesTest();
                case ESCAPE -> Platform.exit();*/
            }
        });

        scene.setOnKeyReleased(event -> {

        });

        AnimationTimer timer = new AnimationTimer() {

            private long dernierTemps = System.nanoTime();

            @Override
            public void handle(long temps) {

                double deltaTemps = (temps - dernierTemps) * 1e-9;
                dernierTemps = temps;
                //tempsActuelEnSecondes = temps * 1e-9; // Temps actuel en secondes (double)


            }
        };

        timer.start();
        root.getChildren().add(canvas);
        stage.setTitle("Camelot à vélo");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }




    public static void main(String[] args) {
        launch();
    }
}
