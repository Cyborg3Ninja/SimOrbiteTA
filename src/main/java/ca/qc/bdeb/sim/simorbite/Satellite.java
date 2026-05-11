package ca.qc.bdeb.sim.simorbite;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import java.util.ArrayList;

import static java.lang.Math.*;

public class Satellite extends Astre {
    // --- Attributs de l'orbite ---
    double rayonPeriastre;
    double rayonApoastre;
    double masseSatellite;
    double masseCorpsCentral;
    double Gm;
    double periodeTheorique;
    Astre corpsCentrale;
    double temps;
    double tempsP; // Temps au passage du périastre (T0)
    Color couleur;
    Point2D ancre; // Position du corps central (foyer)
    ArrayList<Point2D> trace = new ArrayList<>();

    // --- Constantes et paramètres de calcul ---
    final double DEGREEPRECISION = 0.005; // Précision pour la méthode itérative de Kepler
    double angleP = 0; // Angle du périastre (orientation de l'ellipse)


    public Satellite(double x, double y, double rayon, String nom, double rayonPeriastre,
                     double rayonApoastre, double masse,
                     double Gm, double periodeTheorique, Astre corpsCentrale, double temps, double tempsP, Color couleur) {
        super(x, y, masse, rayon, nom);
        this.rayonPeriastre = rayonPeriastre;
        this.rayonApoastre = rayonApoastre;
        this.Gm = Gm;
        this.periodeTheorique = periodeTheorique;
        this.corpsCentrale = corpsCentrale;
        this.temps = temps;
        this.tempsP = tempsP;
        this.couleur = couleur;
        this.ancre = corpsCentrale.getPosition();
        this.masseSatellite = masse;
    }

    public ArrayList<Point2D> getTrace(){
        return trace;
    }

    public void setTemps(double temps){
        this.temps = temps;
    }

    public double getRayonPeriastre() {
        return rayonPeriastre;
    }

    public double getRayonApoastre() {
        return rayonApoastre;
    }

    public double getMasseSatellite() {
        return masseSatellite;
    }

    public double getMasseCorpsCentral() {
        return masseCorpsCentral;
    }

    public double getGm() {
        return Gm;
    }

    public double getPeriodeTheorique() {
        return periodeTheorique;
    }

    /**
     * Calcule l'excentricité de l'orbite (e)
     */
    public double getE(){
        return (getRayonApoastre() - getRayonPeriastre()) / (getRayonApoastre() + getRayonPeriastre());
    }

    /**
     * Calcule le demi-grand axe (a)
     */
    public double getDGA(){
        return (getRayonApoastre() + getRayonPeriastre())/2;
    }

    /**
     * Calcule le demi-petit axe (b)
     */
    public double getDPA() {
        return getDGA() * Math.sqrt(1 - Math.pow(getE(), 2));
    }

    /**
     * Calcule le paramètre de l'ellipse (p)
     */
    public double getParametreEllipse() {
        return getDGA() * (1-getE() * getE());
    }

    /**
     * Calcule la vitesse angulaire moyenne (n)
     */
    public double getMoyenneMouvement() {
        return 2 * PI / getPeriodeTheorique();
    }

    /**
     * Calcule l'Anomalie Moyenne (M = n * deltaT)
     */
    public double getAnomalieMoyenne() {
        return getMoyenneMouvement() * (temps - tempsP);
    }

    /**
     * Calcule l'Anomalie Excentrique (E) via la résolution de l'équation de Kepler
     */
    public double getAnomalieExcentrique() {
        return calculApproximationAnomalieExcentrique(getAnomalieMoyenne() /(1-getE()));
    }

    /**
     * Calcule l'Anomalie Vraie (nu) à partir de l'anomalie excentrique
     */
    public double getAnomalieVraie() {
        return 2*Math.atan(Math.sqrt((1+getE())/(1-getE())*tan(getAnomalieExcentrique()/2)));
    }

    /**
     * Distance entre le centre de l'ellipse et le foyer (c)
     */
    public double getC(){
        return getDGA() / Constantes.ECHELLE * getE();
    }

    /**
     * Méthode itérative pour résoudre M = E - e*sin(E)
     */
    public double calculApproximationAnomalieExcentrique(double M) {
        double u = M/(1-getE());
        double uAncien;

        do {
            uAncien = u;
            u = getE() * Math.sin(u) + M;
        } while (Math.abs(u - uAncien) > DEGREEPRECISION);

        return u;
    }

    public void updateAncre(){
        ancre = corpsCentrale.getPosition();
    }

    /**
     * Calcule la position cartésienne (x, y) du satellite pour l'affichage JavaFX
     */
    public Point2D position(double c) {
        double deltaT = temps - tempsP;

        // Normalisation de l'anomalie moyenne
        double anomalieMoyenne = (getMoyenneMouvement() * deltaT) % (2 * Math.PI);
        if (anomalieMoyenne < 0) anomalieMoyenne += 2 * Math.PI;

        double E = calculApproximationAnomalieExcentrique(anomalieMoyenne);

        // Calcul des coordonnées relatives au foyer (mètres vers pixels via ECHELLE)
        double xRelatif = (getDGA() * (Math.cos(E) - getE())) / Constantes.ECHELLE;
        double yRelatif = (-getDGA() * (Math.sqrt(1 - getE() * getE()) * Math.sin(E))) / Constantes.ECHELLE;

        // Positionnement par rapport au centre du corps central
        double centreAncreX = ancre.getX() + corpsCentrale.getLargeur() / 2;
        double centreAncreY = ancre.getY() + corpsCentrale.getLargeur() / 2;

        // Mise à jour des coordonnées finales (centrage du satellite)
        setX(centreAncreX + xRelatif - taille.getX() / 2);
        setY(centreAncreY + yRelatif - taille.getY() / 2);

        return new Point2D(getX(), getY());
    }

    /**
     * Calcule la force gravitationnelle instantanée (F = G*m1*m2 / r^2)
     */
    public double calculForceGravitionnelle(){
        return 6.67e-11 * getMasseSatellite() * getMasseCorpsCentral() / (calculDistance()*calculDistance());
    }

    /**
     * Calcule la distance actuelle entre le satellite et le corps central
     */
    public double calculDistance (){
        return getParametreEllipse()/(1+getE()*cos(getAnomalieVraie() - angleP));
    }

    /**
     * Calcule la force gravitationnelle spécifique entre deux astres (ex: Terre-Soleil)
     */
    public double forceTerreSoleil(double distance){
        return -calculForceGravitionnelle() * getMasseCorpsCentral() * getMasseSatellite() / (distance * distance);
    }
}