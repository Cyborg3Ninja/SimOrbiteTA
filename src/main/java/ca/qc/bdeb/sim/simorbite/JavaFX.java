package ca.qc.bdeb.sim.simorbite;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.util.Map.entry;
import static javafx.scene.paint.Color.*;

public class JavaFX extends Application {

    static final int WIDTH = 900;
    final int HEIGHT = 600;

    // Position du soleil (centre simulation)
    double centreX = getWidthSimulation() / 2.0;
    double centreY = HEIGHT / 2.0;

    private Point2D positionInitiale = null;
    private boolean orbiteComplete = false;

    //
    Map<String, String> map = Map.of(
            "Rayon", "1;10",
            "Periastre", "value2",
            "Apoastre", "value3",
            "Masse du Satellite","1;1",
            "Masse du Corps Centrale","1;1",
            "GM","1;1",
            "Periode theorique","1;1",
            "Corps Centrale","1;1"
    );

    private final HashMap<String, String> PARAM = new HashMap<>(map);



    ArrayList<Satellite> satellitesList = new ArrayList<>();
    private CorpsCentral soleil = new CorpsCentral(centreX - Constantes.DISTANCEFOYERSOLEIL - 10, centreY - 10, 20);

    private Satellite terre = new Satellite(0, 0, 10, 147099894, 149598023,
            5.972 * Math.pow(10, 24), 1.989 * Math.pow(10, 30),
            398600.4418, 31558145, soleil, 0, 0, BLUE);

    private Satellite lune = new Satellite(terre.getX(), terre.getY(), 5, 356400 *50 , 406700 *50,
            7.35 * Math.pow(10, 22), 5.972 * Math.pow(10, 24), 2360448, 2548800,
            terre, 0, 0, GRAY);




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

        satellitesList.add(terre);
        satellitesList.add(lune);

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


        Button boutonAjouter = new Button("Ajouter planète");
        boutonAjouter.setStyle("-fx-font-size: 14px; -fx-background-color: #3498db; -fx-text-fill: white;");


        boutonAjouter.setOnAction(e -> {
            // Fenêtre principale (VBox)
            VBox mainLayout = new VBox(20); // Spacing de 20px entre les lignes
            mainLayout.setPadding(new javafx.geometry.Insets(25)); // Marges intérieures
            mainLayout.setAlignment(javafx.geometry.Pos.CENTER);
            mainLayout.setStyle("-Inner-background-color: #2c3e50; -fx-background-color: #f4f4f4;");

            ArrayList<Slider> inputsList = new ArrayList<>();

            Text title = new Text("Configuration");
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #34495e;");
            mainLayout.getChildren().add(title);

            for (String p : PARAM.keySet()) {
                HBox row = new HBox(10);
                Text label = new Text(p);
                label.setWrappingWidth(120);

                Slider slider = new Slider(1,1,1);

                // On garde la référence du TextField
                inputsList.add(slider);

                row.getChildren().addAll(label, slider);
                mainLayout.getChildren().add(row);
            }

            // Bouton de validation
            Button fini = new Button("Enregistrer le Satellite");
            fini.setPrefWidth(200);
            fini.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

            fini.setOnAction(event -> {
                try {
                    ArrayList<Double> donneesPhysiques = new ArrayList<>();
                    for(Slider s : inputsList) {
                        // Remplacer les virgules par des points pour éviter les erreurs Java
                        donneesPhysiques.add(s.getValue());
                    }
                    Satellite nouveau = new Satellite(
                            0, 0,           // x, y initiaux (seront écrasés par position())
                            donneesPhysiques.get(0),       // rayon du cercle à l'écran
                            donneesPhysiques.get(1),       // rayonPeriastre
                            donneesPhysiques.get(2),       // rayonApoastre
                            donneesPhysiques.get(3),       // masseSatellite
                            donneesPhysiques.get(4),       // masseCorpsCentral
                            donneesPhysiques.get(5),       // Gm
                            donneesPhysiques.get(6).longValue(), // periodeTheorique (converti en long si nécessaire)
                            soleil,         // corpsCentral (par défaut le soleil ici) AJOUTER SELECTOR
                            0, 0,           // temps, tempsP
                            Color.RED       // couleur par défaut ( ajouter un ColorPicker !)
                    );


                } catch (NumberFormatException err) {
                    System.out.println("Erreur : Veuillez entrer des chiffres valides !");
                }
            });

            // Effet hover simple
            fini.setOnMouseEntered(event -> fini.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;"));
            fini.setOnMouseExited(event -> fini.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;"));

            mainLayout.getChildren().addAll(fini);

            Scene secondScene = new Scene(mainLayout, 350, 450); // Légèrement plus large pour le confort

            Stage newWindow = new Stage();

            fini.setOnAction(event ->
                    newWindow.close());

            newWindow.setTitle("Nouveau Satellite");
            newWindow.setScene(secondScene);
            newWindow.show();
        });



        menu.getChildren().addAll(aTemps, aTrace, boutonEffacer, boutonAjouter);


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

        for (Satellite s : satellitesList) {
            s.updateAncre();
            s.position(s.getC());



            /*if (position.getX() > getWidthSimulation()) {
                position = new Point2D(getWidthSimulation() , position.getY());
            }
            if (position.getY() > HEIGHT) {
                position = new Point2D(position.getX(), HEIGHT);
            }*/

            Point2D pointTrace = new Point2D(s.getX() + s.getTaille().getX() / 2, s.getY() + s.getTaille().getY() / 2);
            s.getTrace().add(pointTrace);

            while (s.getTrace().size() > sliderTrace.getValue()) {
                s.getTrace().removeFirst();
            }
        }

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

    public HBox addHbox(Text text){
        HBox hbox = new HBox();
        TextField textField = new TextField();
        hbox.getChildren().addAll(text, textField);
        return hbox;
    }


    public static void main(String[] args) {
        launch();
    }
}
