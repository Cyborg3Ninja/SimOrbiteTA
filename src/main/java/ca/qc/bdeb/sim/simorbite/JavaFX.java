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

import java.util.ArrayList;
import java.util.LinkedHashMap;

import static javafx.scene.paint.Color.*;

public class JavaFX extends Application {

    // --- Configuration de base de la fenêtre ---
    static final int WIDTH = 900;
    static final int HEIGHT = 600;

    // Calcul du point central pour positionner le Soleil
    double centreX = getWidthSimulation() / 2.0;
    double centreY = HEIGHT / 2.0;

    // Listes pour stocker les composants interactifs de la fenêtre d'ajout
    ArrayList<TextField> textFields = new ArrayList<>();
    ArrayList<Slider> inputsList = new ArrayList<>();

    // Liste pour les étoiles décoratives en arrière-plan
    private ArrayList<Etoile> etoiles = new ArrayList<>();

    // Map définissant les plages de valeurs autorisées pour les nouveaux satellites (min;max)
    private final LinkedHashMap<String, String> PARAM = new LinkedHashMap<>() {{
        put("Rayon", "3;10");
        put("Periastre", "11000000;200000000");
        put("Apoastre", "11000000;200000000");
        put("Periode theorique","1000000;100000000");
    }};

    // --- Initialisation des objets de la simulation ---
    ArrayList<Satellite> satellitesList = new ArrayList<>();

    // Création du corps central fixe
    private CorpsCentral soleil = new CorpsCentral(centreX - Constantes.DISTANCEFOYERSOLEIL - 10, centreY - 10, 20, 1.989 * Math.pow(10, 30), "Soleil");

    // Création des astres par défaut (Terre et Lune)
    private Satellite terre = new Satellite(0, 0, 10, "Terre" , 147099894, 149598023,
            5.972 * Math.pow(10, 24),
            398600.4418, 31558145, soleil, 0, 0, BLUE);

    private Satellite lune = new Satellite(terre.getX(), terre.getY(), 5, "Lune", 356400 *50 , 406700 *50,
            7.35 * Math.pow(10, 22), 2360448, 2548800,
            terre, 0, 0, GRAY);

    // Variables de contrôle temporel
    private static double tempsSimulation = 0;
    private static double accelerationTemps = 2000000;

    // Références des composants UI pour le menu de droite
    VBox menu;
    Slider sliderTemps;
    Slider sliderTrace;
    CheckBox traceInfini;
    Button boutonEffacer;
    ComboBox<Satellite> comboSupprimer;

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        Pane simulation = new Pane();

        // Style visuel de l'espace (dégradé radial)
        simulation.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 100%, #0b0f1a, #000000);");
        simulation.setPrefWidth(getWidthSimulation());

        satellitesList.add(terre);
        satellitesList.add(lune);

        // --- Construction du panneau de contrôle ---
        menu = new VBox(15);
        menu.setStyle("-fx-background-color: linear-gradient(to bottom, #1b2735, #090a0f); -fx-border-color: #00c3ff; -fx-border-width: 0 0 0 2;");
        menu.setPrefWidth((double) WIDTH / 3);
        menu.setPadding(new javafx.geometry.Insets(20));

        // Slider pour gérer la vitesse de la simulation
        HBox aTemps = new HBox();
        Text textTemps = new Text("Accélération du temps");
        textTemps.setFill(WHITE);
        sliderTemps = new Slider(1, 10, 1);
        aTemps.getChildren().addAll(textTemps, sliderTemps);
        sliderTemps.setShowTickLabels(true);

        // Slider pour gérer la longueur visuelle de l'orbite (trace)
        HBox aTrace = new HBox();
        Text textTrace = new Text("Longeur trace");
        textTrace.setFill(WHITE);
        sliderTrace = new Slider(0, 1000, 50);
        aTrace.getChildren().addAll(textTrace, sliderTrace);
        sliderTrace.setShowTickLabels(true);

        traceInfini = new CheckBox("Trace Infini");
        traceInfini.setTextFill(WHITE);

        // Bouton pour vider les listes de points des traces
        boutonEffacer = new Button("Effacer orbite");
        boutonEffacer.setStyle("-fx-background-color: linear-gradient(to right, #f7971e, #ffd200); -fx-text-fill: black; -fx-background-radius: 8;");
        boutonEffacer.setOnAction(e -> {
            for (Satellite s : satellitesList) s.getTrace().clear();
        });

        Button boutonAjouter = getBoutonAjouter();

        // ComboBox pour sélectionner une planète à supprimer
        comboSupprimer = new ComboBox<>();
        comboSupprimer.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Satellite item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); }
                else { setText(item.getNom()); setTextFill(Color.WHITE); }
            }
        });
        comboSupprimer.setPrefWidth(200);
        refreshComboSupprimer(comboSupprimer);

        Button boutonSupprimer = getBoutonSupprimer();
        HBox supprimer = new HBox();
        supprimer.getChildren().addAll(comboSupprimer, boutonSupprimer);

        menu.getChildren().addAll(aTemps, aTrace, traceInfini, boutonEffacer, boutonAjouter, supprimer);

        // --- Préparation du Canvas graphique ---
        Canvas canvas = new Canvas(WIDTH - 50, HEIGHT);
        GraphicsContext context = canvas.getGraphicsContext2D();

        // Génération des étoiles aléatoires
        Random rand = new Random();
        for (int i = 0; i < 150; i++) {
            etoiles.add(new Etoile(rand.nextDouble() * getWidthSimulation(), rand.nextDouble() * HEIGHT));
        }

        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.BLACK);
        scene.setOnKeyPressed(e -> { if (e.getCode().toString().equals("ESCAPE")) Platform.exit(); });

        // --- Boucle de rafraîchissement (60 FPS approx) ---
        AnimationTimer timer = new AnimationTimer() {
            private long dernierTemps = System.nanoTime();

            @Override
            public void handle(long temps) {
                double deltaTemps = (temps - dernierTemps) * 1e-9;
                dernierTemps = temps;

                tempsSimulation += deltaTemps * accelerationTemps;
                for (Satellite s : satellitesList) s.setTemps(tempsSimulation);

                update(); // Calculer physique
                draw(context); // Rendre graphique
            }
        };

        timer.start();

        simulation.getChildren().add(canvas);
        root.setCenter(simulation);
        root.setRight(menu);
        stage.setTitle("Simulation orbite");
        mettreIcone(stage);
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Crée le bouton de suppression et définit son action.
     */
    private Button getBoutonSupprimer() {
        Button boutonSupprimer = new Button("Supprimer planète");
        boutonSupprimer.setStyle("-fx-background-color: linear-gradient(to right, #ff416c, #ff4b2b); -fx-text-fill: white; -fx-background-radius: 8;");
        boutonSupprimer.setOnAction(e -> {
            Satellite selection = comboSupprimer.getValue();
            if (selection != null) {
                supprimerAvecSatellites(selection);
                comboSupprimer.getItems().remove(selection);
            }
        });
        return boutonSupprimer;
    }

    /**
     * Construit la fenêtre secondaire (Stage) pour la saisie des données d'un nouveau satellite.
     */
    private Button getBoutonAjouter() {
        Button boutonAjouter = new Button("Ajouter planète");
        boutonAjouter.setStyle("-fx-background-color: linear-gradient(to right, #00c6ff, #0072ff); -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 8;");

        boutonAjouter.setOnAction(e -> {
            inputsList.clear();
            textFields.clear();
            VBox mainLayout = new VBox(20);
            mainLayout.setPadding(new javafx.geometry.Insets(25));
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

            // Génération dynamique des Sliders basés sur la Map de paramètres
            for (String p : PARAM.keySet()) {
                HBox row = new HBox(10);
                row.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
                Text label = new Text(p);
                label.setWrappingWidth(100);

                String[] tab = PARAM.get(p).split(";", 2);
                double inf = Double.parseDouble(tab[0]);
                double sup = Double.parseDouble(tab[1]);

                Slider slider = new Slider(inf, sup, inf);
                slider.setMajorTickUnit((sup-inf)/2);
                slider.setMinorTickCount(0);
                slider.setShowTickLabels(true);
                HBox.setHgrow(slider, Priority.ALWAYS);
                inputsList.add(slider);

                TextField tf = new TextField();
                tf.setPrefWidth(100);
                // Liaison bidirectionnelle pour que Slider et TextField soient synchronisés
                tf.textProperty().bindBidirectional(slider.valueProperty(), new javafx.util.converter.NumberStringConverter());
                textFields.add(tf);

                row.getChildren().addAll(label, slider, tf);
                mainLayout.getChildren().add(row);
            }

            ComboBox<Astre> comboCorps = new ComboBox<>();
            comboCorps.getItems().add(soleil);
            comboCorps.getItems().addAll(satellitesList);
            comboCorps.getSelectionModel().selectFirst();
            comboCorps.setPrefWidth(200);

            HBox rowCorps = new HBox(10);
            rowCorps.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            Text labelCorps = new Text("Orbiter autour de :");
            labelCorps.setWrappingWidth(120);
            rowCorps.getChildren().addAll(labelCorps, comboCorps);
            mainLayout.getChildren().add(rowCorps);

            Text messageErreur = new Text();
            messageErreur.setFill(Color.RED);
            mainLayout.getChildren().add(messageErreur);

            Stage newWindow = new Stage();
            Button fini = new Button("Enregistrer le Satellite");
            fini.setPrefWidth(200);
            fini.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");

            fini.setOnAction((event) -> {
                boolean valide = true;
                if (tfNom.getText().trim().isEmpty()) { valide = false; tfNom.setStyle("-fx-border-color: red;"); }
                for (Satellite s : this.satellitesList) {
                    if (s.getNom().equalsIgnoreCase(tfNom.getText().trim())) { valide = false; break; }
                }

                if (!valide) return;

                try {
                    ArrayList<Double> donneesPhysiques = new ArrayList<>();
                    for (Slider s : this.inputsList) donneesPhysiques.add(s.getValue());
                    Color couleurClaire = Color.hsb(Math.random() * 360, 0.8, 0.9);
                    Astre cible = comboCorps.getValue();

                    Satellite nouveau = new Satellite(0, 0, donneesPhysiques.get(0), tfNom.getText(), donneesPhysiques.get(1), donneesPhysiques.get(2), 0.0, 398600.44, donneesPhysiques.get(3), cible, 0, 0, couleurClaire);
                    this.satellitesList.add(nouveau);
                    refreshComboSupprimer(comboSupprimer);
                    newWindow.close();
                } catch (Exception ex) { messageErreur.setText("Erreur."); }
            });

            mainLayout.getChildren().add(fini);
            newWindow.setScene(new Scene(mainLayout, 450, 550));
            newWindow.setTitle("Nouveau Satellite");
            mettreIcone(newWindow);
            newWindow.show();
        });
        return boutonAjouter;
    }

    /**
     * Supprime récursivement un astre et tout ce qui est en orbite autour de lui.
     */
    private void supprimerAvecSatellites(Astre cible) {
        ArrayList<Satellite> aSupprimer = new ArrayList<>();
        for (Satellite s : satellitesList) {
            if (s.corpsCentrale == cible) aSupprimer.add(s);
        }
        for (Satellite s : aSupprimer) supprimerAvecSatellites(s);
        satellitesList.remove(cible);
    }

    /**
     * Met à jour la position des satellites et enregistre les points de la trace.
     */
    private void update() {
        for (Satellite s : satellitesList) {
            s.updateAncre();
            s.position(1);
            Point2D pointTrace = new Point2D(s.getX() + s.getTaille().getX() / 2, s.getY() + s.getTaille().getY() / 2);
            s.getTrace().add(pointTrace);

            if(!traceInfini.isSelected()) {
                while (s.getTrace().size() > sliderTrace.getValue()) s.getTrace().removeFirst();
            }
        }
        accelerationTemps = sliderTemps.getValue() * 500000;
    }

    /**
     * Gère tout le rendu graphique du Canvas (Étoiles, Traces, Soleil, Planètes).
     */
    private void draw(GraphicsContext gc) {
        gc.clearRect(0, 0, WIDTH, HEIGHT);
        gc.setFill(Color.WHITE);
        for (Etoile e : etoiles) {
            e.update();
            gc.setGlobalAlpha(e.alpha);
            gc.fillOval(e.x, e.y, 1.5, 1.5);
        }
        gc.setGlobalAlpha(1.0);

        for (Satellite s : satellitesList) {
            gc.setStroke(s.couleur.deriveColor(0, 1, 1, 0.3));
            dessinerTrace(s.getTrace(), gc);
        }

        // Effets visuels du Soleil (Pulsations et Halo)
        double pulse = (Math.sin(tempsSimulation * 0.00001) + 1) / 2;
        double facteur = 0.9 + pulse * 0.3;
        gc.setFill(Color.rgb(255, 220, 100));
        gc.fillOval(soleil.getX(), soleil.getY(), soleil.getLargeur(), soleil.getHauteur());

        double cx = soleil.getX() + soleil.getLargeur() / 2;
        double cy = soleil.getY() + soleil.getHauteur() / 2;
        int rays = 10;
        double baseRadius = soleil.getLargeur() / 2 + 5;
        double innerRadius = baseRadius * facteur;
        double outerRadius = (baseRadius + 12) * facteur;

        double[] xPoints = new double[rays * 2];
        double[] yPoints = new double[rays * 2];

        for (int i = 0; i < rays * 2; i++) {
            double angle = i * Math.PI / rays;
            double radius = (i % 2 == 0) ? outerRadius : innerRadius;
            xPoints[i] = cx + Math.cos(angle) * radius;
            yPoints[i] = cy + Math.sin(angle) * radius;
        }

        gc.setFill(Color.rgb(255, 200, 50, 0.15 + pulse * 0.2));
        gc.fillOval(soleil.getX() - 12, soleil.getY() - 12, soleil.getLargeur() + 24, soleil.getHauteur() + 24);
        gc.setFill(Color.rgb(255, 200, 50, 0.25));
        gc.fillPolygon(xPoints, yPoints, rays * 2);

        for (Satellite s : satellitesList) {
            gc.setFill(s.couleur.deriveColor(0, 1, 1, 0.2));
            gc.fillOval(s.getX() - 3, s.getY() - 3, s.getLargeur() + 6, s.getHauteur() + 6);
            gc.setFill(s.couleur);
            s.draw(gc);
        }
    }

    public static double getWidthSimulation() { return (double) WIDTH - WIDTH / 3.0; }

    /**
     * Dessine les lignes reliant chaque point de la trajectoire mémorisée.
     */
    public static void dessinerTrace(ArrayList<Point2D> trace, GraphicsContext gc) {
        for (int i = 1; i < trace.size(); i++) {
            Point2D p1 = trace.get(i - 1);
            Point2D p2 = trace.get(i);
            if (p1.getX() == 0 && p1.getY() == 0) continue;
            gc.strokeLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        }
    }

    private void refreshComboSupprimer(ComboBox<Satellite> combo) {
        combo.getItems().clear();
        combo.getItems().addAll(this.satellitesList);
    }

    private void mettreIcone(Stage stage){
        try {
            stage.getIcons().add(new javafx.scene.image.Image(getClass().getResourceAsStream("/IconTerre.png")));
        } catch (Exception e) { System.out.println("Erreur icône."); }
    }
}