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

import static javafx.scene.paint.Color.WHITE;
import static javafx.scene.paint.Color.YELLOW;

public class JavaFX extends Application {

    final int WIDTH = 900;
    final int HEIGHT = 600;

    // Position du soleil (centre écran)
    double centreX = WIDTH / 2.0;
    double centreY = HEIGHT / 2.0;


    //Distance entre centre et le foyer
    double c = Physique.DGA / Constantes.ECHELLE * Physique.e;
    private Etoile soleil = new Etoile(centreX - c - 10, centreY - 10, 20);
    private Planete terre = new Planete(0, 0, 10);

    Point2D ancre = new Point2D(centreX , centreY);
    Physique physique = new Physique(ancre, 0, 0);

    private static double tempsSimulation = 0;


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



        // Position de référence pour la physique
        //Point2D ancre = new Point2D(soleil.getX() + 10, soleil.getY() + 10);
        //Point2D ancre = new Point2D(centreX, centreY);

        physique = new Physique(ancre, tempsSimulation, 0);

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

        terre.setX(position.getX() - terre.getTaille().getX()/2 + c);
        terre.setY(position.getY() - terre.getTaille().getY()/2);

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

        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);

        double a = Physique.DGA / Constantes.ECHELLE;
        double b = Physique.DPA / Constantes.ECHELLE;

        gc.strokeOval(
                centreX - a, // décalage horizontal pour placer le foyer au Soleil
                centreY - b,     // verticale centrée
                a * 2,
                b * 2
        );


        gc.setFill(YELLOW);
        soleil.draw(gc);

        gc.setFill(Color.BLUE);
        terre.draw(gc);

    }

    public static double getTempsSimulation(){
        return tempsSimulation;
    }

    public static void main(String[] args) {
        launch();
    }
}
