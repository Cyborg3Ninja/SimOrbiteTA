package ca.qc.bdeb.sim.simorbite;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

import static javafx.scene.paint.Color.*;

public class JavaFX extends Application {

    static final int WIDTH = 900;
    final int HEIGHT = 600;

    // Position du soleil (centre simulation)
    double centreX = getWidthSimulation() / 2.0;
    double centreY = HEIGHT / 2.0;

    private ArrayList<Point2D> traceT = new ArrayList<>();
    private ArrayList<Point2D> traceL = new ArrayList<>();

    private Point2D positionInitiale = null;
    private boolean orbiteComplete = false;


    private Satellite terre = new Satellite(0, 0, 10, 147099894, 149598023,
            5.972 * Math.pow(10, 24), 1.989 * Math.pow(10, 30),
            398600.4418,31558145);
    private Satellite lune = new Satellite(terre.getPosition().getX(),terre.getPosition().getY(),5, 356400 * 50 , 406700 * 50,
            7.35 * Math.pow(10, 22), 5.972 * Math.pow(10, 24), 2360448, 2548800);

    Point2D ancre = new Point2D(centreX , centreY);
    Physique physiqueT = new Physique(terre, ancre, 0, 0);
    Physique physiqueL = new Physique(lune, terre.getPosition(), 0,0);

    //10 = 20/2
    private CorpsCentral soleil = new CorpsCentral(centreX - physiqueT.getC() - 10, centreY - 10, 20);

    private static double tempsSimulation = 0;
    private static double accelerationTemps = 2000000;

    VBox menu;
    Slider sliderTemps;
    Button boutonEffacer;

    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root  = new BorderPane();
        Pane simulation = new Pane();

        simulation.setStyle("-fx-background-color: #000000;");
        simulation.setPrefWidth(getWidthSimulation());

        menu = new VBox(15); // 15 est l'espacement entre les éléments
        menu.setStyle("-fx-background-color: #2c3e50;");
        menu.setPrefWidth(WIDTH/3);   // Largeur fixe pour le menu
        menu.setPadding(new javafx.geometry.Insets(20)); // Marges intérieures

        HBox aTemps = new HBox();
        Text textTemps = new Text("Accélération du temps");
        textTemps.setFill(WHITE);
        sliderTemps = new Slider(1,10, 1);
        aTemps.getChildren().addAll(textTemps, sliderTemps);
        sliderTemps.setShowTickLabels(true);

        boutonEffacer = new Button("Effacer orbite");
        boutonEffacer.setStyle("-fx-font-size: 14px; -fx-background-color: #e74c3c; -fx-text-fill: white;");
        boutonEffacer.setOnAction(e -> {


            traceT.clear();
            traceL.clear();
            orbiteComplete = false;
            positionInitiale = null;


        });


        menu.getChildren().addAll(aTemps, boutonEffacer);


        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);

        Canvas canvas = new Canvas(WIDTH-50, HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();



        scene.setOnKeyPressed(e -> {
            if (e.getCode().toString().equals("ESCAPE")) {
                Platform.exit();
            }
        });

        // Initialisations
        ArrierePlan arrierePlan = new ArrierePlan();



        // Position de référence pour la physiqueT
        //Point2D ancre = new Point2D(soleil.getX() + 10, soleil.getY() + 10);
        //Point2D ancre = new Point2D(centreX, centreY);

        //Physique de terre- soleil
        physiqueT = new Physique(terre, ancre, tempsSimulation, 0);
        physiqueL = new Physique(lune, terre.getPosition(), tempsSimulation, 0);

        AnimationTimer timer = new AnimationTimer() {

            private long dernierTemps = System.nanoTime();

            @Override
            public void handle(long temps) {

                double deltaTemps = (temps - dernierTemps) * 1e-9;
                dernierTemps = temps;

                // accélérer le temps pour l'animation
                tempsSimulation += deltaTemps *  accelerationTemps;

                update();
                draw(context);
            }
        };

        timer.start();

        simulation.getChildren().add(canvas);
        root.setCenter(simulation);
        root.setRight(menu);
        stage.setTitle("Simulation orbite");
        stage.setResizable(true);
        stage.setScene(scene);
        stage.show();

    }

    private void update() {
        Point2D positionT = physiqueT.position(tempsSimulation);

        terre.setX(positionT.getX() - terre.getTaille().getX()/2 + physiqueT.getC());
        terre.setY(positionT.getY() - terre.getTaille().getY()/2);

        Point2D positionL = physiqueL.position(tempsSimulation);
        physiqueL.setAncre(terre.getPosition());

        //pas de c car pas visible
        lune.setX(positionL.getX() + terre.getTaille().getX()/2 - lune.getTaille().getX()/2);
        lune.setY(positionL.getY() + terre.getTaille().getY()/2 - lune.getTaille().getY()/2);

        physiqueL.setAncre(terre.getPosition());

        lune.setX(positionL.getX() + terre.getTaille().getX()/2 - lune.getTaille().getX()/2);
        lune.setY(positionL.getY() + terre.getTaille().getY()/2 - lune.getTaille().getY()/2);

        if (positionInitiale == null) {
            positionInitiale = positionT;
        }

        if (!orbiteComplete) {
            traceT.add(positionT);

            if (positionT.distance(positionInitiale) < 2 && traceT.size() > 100) {
                orbiteComplete = true;
            }
        }

        Point2D posCentreeL = physiqueL.centrer(positionL);
        if (posCentreeL.getX() > 10 && posCentreeL.getY() > 10) { // Seuil arbitraire
            traceL.add(posCentreeL);
        }

        accelerationTemps = sliderTemps.getValue() * 2000000;

        if(traceT.size() > 5000){
            traceT.remove(0);
            traceL.remove(0);
        }

    }


    private void draw(GraphicsContext gc) {


        gc.clearRect(0, 0, WIDTH, HEIGHT);

        gc.setLineWidth(2);

        gc.setStroke(Color.rgb(255, 255, 255, 0.35));
        dessinerTrace(traceT, gc);

        gc.setLineWidth(1);

        gc.setStroke(Color.rgb(167, 0, 255, 0.5));
        dessinerTrace(traceL, gc);


        // Soleil
        gc.setFill(Color.YELLOW);
        soleil.draw(gc);


        // Terre
        gc.setFill(Color.BLUE);
        terre.draw(gc);

        // Lune
        gc.setFill(GRAY);
        lune.draw(gc);


    }


    public static double getTempsSimulation(){
        return tempsSimulation;
    }
    public static double getWidthSimulation(){
        return WIDTH - WIDTH/3;
    }

    public static void dessinerTrace(ArrayList<Point2D> trace, GraphicsContext gc){
        for (int i = 1; i < trace.size(); i++) {

            Point2D p1 = trace.get(i - 1);
            Point2D p2 = trace.get(i);

            if (p1.getX() == 0 && p1.getY() == 0) continue;

            gc.strokeLine(
                    p1.getX(), p1.getY(),
                    p2.getX(), p2.getY()
            );
        }
    }


    public static void main(String[] args) {
        launch();
    }
}
