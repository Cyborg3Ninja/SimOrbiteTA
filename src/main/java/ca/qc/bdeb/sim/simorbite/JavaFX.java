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

    /*private ArrayList<Point2D> traceT = new ArrayList<>();
    private ArrayList<Point2D> traceL = new ArrayList<>();*/

    private Point2D positionInitiale = null;
    private boolean orbiteComplete = false;


    ArrayList<Satellite> satellitesList = new ArrayList<>();
    private CorpsCentral soleil = new CorpsCentral(centreX - Constantes.DISTANCEFOYERSOLEIL - 10, centreY - 10, 20);
    ;

    private Satellite terre = new Satellite(0, 0, 10, 147099894, 149598023,
            5.972 * Math.pow(10, 24), 1.989 * Math.pow(10, 30),
            398600.4418, 31558145, soleil.getPosition(), 0, 0, BLUE);

    private Satellite lune = new Satellite(terre.getX(), terre.getY(), 5, 356400 , 406700,
            7.35 * Math.pow(10, 22), 5.972 * Math.pow(10, 24), 2360448, 2548800, terre.getPosition(), 0, 0, GRAY);


    //physiqueT.getC() = getDGA() / Constantes.ECHELLE * getE()
    //10 = 20/2

    /*Point2D ancre = new Point2D(centreX, centreY);
    Physique physiqueT = new Physique(terre, ancre, 0, 0);;
    Physique physiqueL = new Physique(lune, terre.getPosition(), 0, 0);*/


    private static double tempsSimulation = 0;
    private static double accelerationTemps = 2000000;

    VBox menu;
    Slider sliderTemps;
    Slider sliderTrace;
    Button boutonEffacer;

    @Override
    public void start(Stage stage) throws IOException {
        BorderPane root = new BorderPane();
        Pane simulation = new Pane();

        simulation.setStyle("-fx-background-color: #000000;");
        simulation.setPrefWidth(getWidthSimulation());

        menu = new VBox(15); // 15 est l'espacement entre les éléments
        menu.setStyle("-fx-background-color: #2c3e50;");
        menu.setPrefWidth(WIDTH / 3);   // Largeur fixe pour le menu
        menu.setPadding(new javafx.geometry.Insets(20)); // Marges intérieures

        HBox aTemps = new HBox();
        Text textTemps = new Text("Accélération du temps");
        textTemps.setFill(WHITE);
        sliderTemps = new Slider(1, 10, 1);
        aTemps.getChildren().addAll(textTemps, sliderTemps);
        sliderTemps.setShowTickLabels(true);

        HBox aTrace = new HBox();
        Text textTrace = new Text("Longeur trace");
        textTrace.setFill(WHITE);
        sliderTrace = new Slider(1, 500, 50);
        aTrace.getChildren().addAll(textTrace, sliderTrace);
        sliderTrace.setShowTickLabels(true);

        boutonEffacer = new Button("Effacer orbite");
        boutonEffacer.setStyle("-fx-font-size: 14px; -fx-background-color: #e74c3c; -fx-text-fill: white;");
        boutonEffacer.setOnAction(e -> {
            for (Satellite s :
                    satellitesList) {
                s.getTrace().clear();
            }
            orbiteComplete = false;
            positionInitiale = null;
        });

        menu.getChildren().addAll(aTemps, aTrace, boutonEffacer);


        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);

        Canvas canvas = new Canvas(WIDTH - 50, HEIGHT);
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
        /*for (Satellite s : satellitesList) {
            physiquesList.add(new Physique(s, s.getCorpsCentrale().getPosition(), tempsSimulation,0));
        }*/

        satellitesList.add(terre);
        satellitesList.add(lune);



        AnimationTimer timer = new AnimationTimer() {

            private long dernierTemps = System.nanoTime();

            @Override
            public void handle(long temps) {

                double deltaTemps = (temps - dernierTemps) * 1e-9;
                dernierTemps = temps;

                // accélérer le temps pour l'animation
                tempsSimulation += deltaTemps * accelerationTemps;

                for (Satellite s : satellitesList) {
                    s.setTemps(tempsSimulation);
                }

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

        /*Point2D positionT = terre.position(tempsSimulation);

        terre.setX(positionT.getX() - terre.getTaille().getX() / 2 + terre.getC());
        terre.setY(positionT.getY() - terre.getTaille().getY() / 2);*/

        for (Satellite s : satellitesList) {

           // Point2D position = s.position();
            if (s == terre) {
                s.setPosition(s.position(s.getC()));
            } else {
                s.position(0);
                System.out.println(s.position);
                System.out.println(s.positionCorpsCentrale);
            }

                System.out.println(s.position);

            /*if (position.getX() > getWidthSimulation()) {
                position = new Point2D(getWidthSimulation() , position.getY());
            }
            if (position.getY() > HEIGHT) {
                position = new Point2D(position.getX(), HEIGHT);
            }*/
           /* if(s.getCorpsCentrale() == soleil) {
                s.setAncre(s.getCorpsCentrale().getPosition());
                s.setX(position.getX() - s.getTaille().getX() / 2+ s.getC());
                s.setY(position.getY() - s.getTaille().getY() / 2);

            } *//*else if (s.getCorpsCentrale() == terre){
                s.setAncre(s.getCorpsCentrale().getPosition().multiply(1.0/Constantes.ECHELLE));
                s.setX(position.getX() + s.getCorpsCentrale().getTaille().getX() /2- s.getTaille().getX() / 2);
                s.setY(position.getY() + s.getCorpsCentrale().getTaille().getY() /2 - s.getTaille().getY() / 2);
                s.setAncre(s.corpsCentrale.getPosition());
            //pas de c car pas visible
            }*/

            Point2D pointTrace = new Point2D(s.getX() + s.getTaille().getX() / 2, s.getY() + s.getTaille().getY() / 2);
            s.getTrace().add(pointTrace);

            while (s.getTrace().size() > sliderTrace.getValue()) {
                s.getTrace().removeFirst();
            }
        }
        /*Point2D positionL = lune.position(tempsSimulation);
        lune.setAncre(terre.getPosition());*/

        //pas de c car pas visible
       /* lune.setX(positionL.getX() + terre.getTaille().getX() / 2 - lune.getTaille().getX() / 2);
        lune.setY(positionL.getY() + terre.getTaille().getY() / 2 - lune.getTaille().getY() / 2);
        lune.setAncre(terre.getPosition());*/


        /*if (!orbiteComplete) {
            traceT.add(positionT);

            if (positionT.distance(positionInitiale) < 2 && traceT.size() > 100) {
                orbiteComplete = true;
            }
        }

        Point2D posCentreeL = lune.centrer(positionL);
        if (posCentreeL.getX() > 10 && posCentreeL.getY() > 10) { // Seuil arbitraire
            traceL.add(posCentreeL);
        }*/

        accelerationTemps = sliderTemps.getValue() * 2000000;

    }

    private void draw(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH, HEIGHT);

        // 1. Dessiner les traces d'abord (sous les planètes)
        gc.setLineWidth(1);
        for (Satellite s : satellitesList) {
            gc.setStroke(s.couleur.deriveColor(0, 1, 1, 0.5)); // Trace semi-transparente
            dessinerTrace(s.getTrace(), gc);
        }

        // 2. Dessiner le Soleil
        gc.setFill(Color.YELLOW);
        soleil.draw(gc);

        // 3. Dessiner les satellites (par-dessus)
        for (Satellite s : satellitesList) {
            gc.setFill(s.couleur);
            s.draw(gc);
        }
    }
    /*private void draw(GraphicsContext gc) {


        gc.clearRect(0, 0, WIDTH, HEIGHT);

        gc.setLineWidth(2);

        gc.setStroke(Color.rgb(255, 255, 255, 0.35));

        for (Satellite s :
                satellitesList) {
            gc.setFill(s.couleur);
            s.draw(gc);
            dessinerTrace(s.getTrace(), gc);

        }*/
        /*dessinerTrace(traceT, gc);

        gc.setLineWidth(1);

        gc.setStroke(Color.rgb(167, 0, 255, 0.5));
        dessinerTrace(traceL, gc);*/


    // Soleil
        /*gc.setFill(Color.YELLOW);
        soleil.draw(gc);

        gc.rect(100,100,100,100);*/
        /*
        // Terre
        gc.setFill(Color.BLUE);
        terre.draw(gc);

        // Lune
        gc.setFill(GRAY);
        lune.draw(gc);*/


    public static double getTempsSimulation() {
        return tempsSimulation;
    }

    public static double getWidthSimulation() {
        return WIDTH - WIDTH / 3;
    }

    public static void dessinerTrace(ArrayList<Point2D> trace, GraphicsContext gc) {
        for (int i = 1; i < trace.size(); i++) {

            Point2D p1 = trace.get(i - 1);
            Point2D p2 = trace.get(i);

            if (p1.getX() == 0 && p1.getY() == 0) {
                continue;
            }

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
