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
import java.util.ArrayList;

public class JavaFX extends Application {

    final int WIDTH = 900;
    final int HEIGHT = 700;

    private Etoile soleil;
    private Planete terre;
    private ArrayList<Point2D> trace = new ArrayList<>();

    private Physique physique;

    private double tempsSimulation = 0;

    @Override
    public void start(Stage stage) throws IOException {

        Pane root = new Pane();
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);

        Canvas canvas = new Canvas(WIDTH, HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();


        scene.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ESCAPE")) {
                Platform.exit();
            }
        });

        // Initialisations
        ArrierePlan arrierePlan = new ArrierePlan();

        // Position du soleil (centre écran)
        double centreX = WIDTH / 2.0;
        double centreY = HEIGHT / 2.0;

        soleil = new Etoile(centreX - 10, centreY - 10, 20);

        // Position de référence pour la physique
        Point2D ancre = new Point2D(centreX, centreY);

        physique = new Physique(ancre, 0, 0);

        terre = new Planete(0, 0, 10);

        AnimationTimer timer = new AnimationTimer() {

            private long dernierTemps = System.nanoTime();

            @Override
            public void handle(long temps) {

                double deltaTemps = (temps - dernierTemps) * 1e-9;
                dernierTemps = temps;

                // accélérer le temps pour l'animation
                tempsSimulation += deltaTemps *  2000000;

                update();
                draw(context);
            }
        };

        timer.start();

        root.getChildren().add(canvas);

        stage.setTitle("Simulation orbite");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();

    }

    private void update() {

        Point2D position = physique.position(tempsSimulation);

        terre.setX(position.getX());
        terre.setY(position.getY());

        trace.add(position);

        terre.setX(position.getX());
        terre.setY(position.getY());

        /*double centreX = WIDTH / 2.0;
        double centreY = HEIGHT / 2.0;

        // test ellipse simple (temporaire)
        double a = 200; // demi grand axe
        double b = 180; // demi petit axe

        double x = centreX + a * Math.cos(tempsSimulation);
        double y = centreY + b * Math.sin(tempsSimulation);*/

    }

    private void draw(GraphicsContext gc) {

        gc.clearRect(0, 0, WIDTH, HEIGHT);

        gc.setFill(Color.YELLOW);
        soleil.draw(gc);

        gc.setStroke(Color.GRAY);
        gc.setLineWidth(1);

        for (int i = 1; i < trace.size(); i++) {

            Point2D p1 = trace.get(i - 1);
            Point2D p2 = trace.get(i);

            gc.strokeLine(
                    p1.getX(), p1.getY(),
                    p2.getX(), p2.getY()
            );
        }

        gc.setFill(Color.BLUE);
        terre.draw(gc);

    }


    public static void main(String[] args) {
        launch();
    }
}
