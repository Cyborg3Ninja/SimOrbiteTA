package ca.qc.bdeb.sim.simorbite;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.Random;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.converter.NumberStringConverter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Map.entry;
import static javafx.scene.paint.Color.*;
//gay
public class JavaFX extends Application {


    static final int WIDTH = 900;
    final int HEIGHT = 600;

    // Position du soleil (centre simulation)
    double centreX = getWidthSimulation() / 2.0;
    double centreY = HEIGHT / 2.0;

    private Point2D positionInitiale = null;
    private boolean orbiteComplete = false;

    ArrayList<TextField> textFields = new ArrayList<>();
    ArrayList<Slider> inputsList = new ArrayList<>();

    private final LinkedHashMap<String, String> PARAM = new LinkedHashMap<>() {{
        put("Rayon", "3;10");
        put("Periastre", "11000000;200000000");
        put("Apoastre", "11000000;200000000");
        put("Periode theorique","864000;6.312e9"); //min = 10 jours, max = 200 ans
        /*"Masse du Satellite","1;1",
            "Masse du Corps Centrale","1;1",
            "GM","1;1",
            ,
            "Corps Centrale","1;1"*/
    }};




    ArrayList<Satellite> satellitesList = new ArrayList<>();
    private CorpsCentral soleil = new CorpsCentral(centreX - Constantes.DISTANCEFOYERSOLEIL - 10, centreY - 10, 20, 1.989 * Math.pow(10, 30), "Soleil");

    private Satellite terre = new Satellite(0, 0, 10, "Terre" , 147099894, 149598023,
            5.972 * Math.pow(10, 24),
            398600.4418, 31558145, soleil, 0, 0, BLUE);

    private Satellite lune = new Satellite(terre.getX(), terre.getY(), 5, "Lune", 356400 *50 , 406700 *50,
            7.35 * Math.pow(10, 22), 2360448, 2548800,
            terre, 0, 0, GRAY);




    private static double tempsSimulation = 0;
    private static double accelerationTemps = 2000000;

    VBox menu;
    Slider sliderTemps;
    Slider sliderTrace;
    CheckBox traceInfini;
    Button boutonEffacer;
    ComboBox<Satellite> comboSupprimer;

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
        sliderTrace = new Slider(0, 1000, 50);
        aTrace.getChildren().addAll(textTrace, sliderTrace);
        sliderTrace.setShowTickLabels(true);
        traceInfini = new CheckBox("Trace Infini");
        traceInfini.setTextFill(WHITE);

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
            inputsList.clear();  // Vide les anciennes références
            textFields.clear();
            // Fenêtre principale (VBox)
            VBox mainLayout = new VBox(20); // Spacing de 20px entre les lignes
            mainLayout.setPadding(new javafx.geometry.Insets(25)); // Marges intérieures
            mainLayout.setAlignment(javafx.geometry.Pos.CENTER);
            mainLayout.setStyle("-Inner-background-color: #2c3e50; -fx-background-color: #f4f4f4;");

            Text title = new Text("Configuration");
            title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-fill: #34495e;");
            mainLayout.getChildren().add(title);

            HBox nomRange = new HBox(10);
            Text nom = new Text("Nom ");
            nom.setWrappingWidth(100);
            TextField tfNom = new TextField();

            Text erreurNom = new Text();
            erreurNom.setFill(Color.RED);
            mainLayout.getChildren().add(erreurNom);


            nomRange.getChildren().addAll(nom, tfNom);
            mainLayout.getChildren().add(nomRange);
            for (String p : PARAM.keySet()) {
                HBox row = new HBox(10);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);

                Text label = new Text(p);
                label.setWrappingWidth(100); // Largeur fixe pour aligner les colonnes

                String[] tab = PARAM.get(p).split(";", 2);
                double inf = Double.parseDouble(tab[0]);
                double sup = Double.parseDouble(tab[1]);

                // Slider : on lui donne la priorité pour prendre l'espace
                Slider slider = new Slider(inf, sup, inf);
                // On calcule une unité de graduation raisonnable (environ 10 marques max)
                double range = sup - inf;
                slider.setMajorTickUnit(range);
                slider.setMinorTickCount(0); // On enlève les petites barres inutiles
                slider.setShowTickMarks(true);
                slider.setShowTickLabels(true);
                HBox.setHgrow(slider, Priority.ALWAYS);
                inputsList.add(slider);

                // TextField : on le garde petit
                TextField tf = new TextField();
                tf.setPrefWidth(100);

                tf.textProperty().bindBidirectional(slider.valueProperty(), new javafx.util.converter.NumberStringConverter());

                textFields.add(tf);

                row.getChildren().addAll(label, slider, tf);
                mainLayout.getChildren().add(row);
            }

            // 1. Créer le ComboBox
            ComboBox<Astre> comboCorps = new ComboBox<>();

            // 2. Ajouter le Soleil et tous les satellites existants
            comboCorps.getItems().add(soleil);
            comboCorps.getItems().addAll(satellitesList);

            // 3. Sélectionner le soleil par défaut et styliser
            comboCorps.getSelectionModel().selectFirst();
            comboCorps.setPrefWidth(200);

            // 4. Ajouter au layout avec un label
            HBox rowCorps = new HBox(10);
            rowCorps.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            Text labelCorps = new Text("Orbiter autour de :");
            labelCorps.setWrappingWidth(120);
            rowCorps.getChildren().addAll(labelCorps, comboCorps);

            mainLayout.getChildren().add(rowCorps); // Ajoute-le avant ou après tes sliders

            Text messageErreur = new Text();
            messageErreur.setFill(Color.RED);
            mainLayout.getChildren().add(messageErreur);

            Stage newWindow = new Stage();

            // Bouton de validation
            Button fini = new Button("Enregistrer le Satellite");
            fini.setPrefWidth(200);
            fini.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold; -fx-cursor: hand;");

            fini.setOnAction((event) -> {

                boolean valide = true;
                messageErreur.setText("");
                erreurNom.setText("");

                // reset styles
                tfNom.setStyle("");

                // Vérifier champ nom vide
                if (tfNom.getText().trim().isEmpty()) {
                    tfNom.setStyle("-fx-border-color: red;");
                    messageErreur.setText("Veuillez remplir tous les champs.");
                    valide = false;
                }

                // Vérifier doublon de nom
                for (Satellite s : this.satellitesList) {
                    if (s.getNom().equalsIgnoreCase(tfNom.getText().trim())) {
                        erreurNom.setText("Deux satellites ne peuvent pas avoir le même nom.");
                        tfNom.setStyle("-fx-border-color: red;");
                        valide = false;
                        break;
                    }
                }

                if (!valide) return;

                try {
                    ArrayList<Double> donneesPhysiques = new ArrayList<>();

                    for (Slider s : this.inputsList) {
                        donneesPhysiques.add(s.getValue());
                    }

                    Color couleurClaire = Color.hsb(Math.random() * 360, 0.8, 0.9);
                    Astre cible = (Astre) comboCorps.getValue();

                    Satellite nouveau = new Satellite(
                            0, 0,
                            donneesPhysiques.get(0),
                            tfNom.getText(),
                            donneesPhysiques.get(1),
                            donneesPhysiques.get(2),
                            0.0,
                            398600.44,
                            donneesPhysiques.get(3),
                            cible,
                            0, 0,
                            couleurClaire
                    );

                    this.satellitesList.add(nouveau);
                    newWindow.close();

                } catch (Exception ex) {
                    messageErreur.setText("Erreur lors de la création.");
                }
            });



            // Effet hover simple
            fini.setOnMouseEntered(event -> fini.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold;"));
            fini.setOnMouseExited(event -> fini.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;"));

            mainLayout.getChildren().addAll(fini);



            Scene secondScene = new Scene(mainLayout, 450, 450); // Légèrement plus large pour le confort

            newWindow.setTitle("Nouveau Satellite");
            newWindow.setScene(secondScene);
            newWindow.show();
        });

        comboSupprimer = new ComboBox<>();


        comboSupprimer.setPrefWidth(200);
        comboSupprimer.setVisibleRowCount(10);
        refreshComboSupprimer(comboSupprimer);


        Button boutonSupprimer = new Button("Supprimer planète");
        boutonSupprimer.setStyle("-fx-font-size: 14px; -fx-background-color: #c0392b; -fx-text-fill: white;");


        boutonSupprimer.setOnAction(e -> {
            Satellite selection = comboSupprimer.getValue();

            if (selection != null) {
                satellitesList.remove(selection);
                refreshComboSupprimer(comboSupprimer);
            }
        });

        HBox supprimer = new HBox();
        supprimer.getChildren().addAll(comboSupprimer, boutonSupprimer);
            menu.getChildren().addAll(aTemps, aTrace, traceInfini, boutonEffacer, boutonAjouter, supprimer);


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
            s.position(01);



            /*if (position.getX() > getWidthSimulation()) {
                position = new Point2D(getWidthSimulation() , position.getY());
            }
            if (position.getY() > HEIGHT) {
                position = new Point2D(position.getX(), HEIGHT);
            }*/

            Point2D pointTrace = new Point2D(s.getX() + s.getTaille().getX() / 2, s.getY() + s.getTaille().getY() / 2);
            s.getTrace().add(pointTrace);

            if(!traceInfini.isSelected()) {
                while (s.getTrace().size() > sliderTrace.getValue()) {
                    s.getTrace().removeFirst();
                }
            }
        }

        /*for (int i = 0; i < textFields.size(); i++) {
            textFields.get(i).setText(String.valueOf(inputsList.get(i)));
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


    private void refreshComboSupprimer(ComboBox<Satellite> combo) {
        combo.getItems().clear();
        combo.getItems().addAll(this.satellitesList);


    }


    public static void main(String[] args) {
        launch();
    }
}
