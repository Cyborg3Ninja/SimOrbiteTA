package ca.qc.bdeb.sim.simorbite;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;

import static java.lang.Math.*;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.sin;

public class Satellite extends Astre {
    double rayonPeriastre;
    double rayonApoastre;
    double masseSatellite;
    double masseCorpsCentral;
    double Gm;
    double periodeTheorique;
    Astre corpsCentrale;
    double temps;
    double tempsP; //temps au passage du periastre
    Color couleur;
    Point2D ancre;
    ArrayList<Point2D> trace = new ArrayList<>();

    //CONSTANTES
    final double DEGREEPRECISION = 0.005;
    double angleP = 0; //angle du periastre


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
    }

    public ArrayList<Point2D> getTrace(){
        return trace;
    }

    public void setTemps(double temps){
        this.temps = temps;
    }

    public Point2D getAncre() {
        return corpsCentrale.getPosition();
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

    //Excentricite
    public double getE(){
        return (getRayonApoastre() - getRayonPeriastre()) / (getRayonApoastre() + getRayonPeriastre());
    }
    public double getDGA(){
        return (getRayonApoastre() + getRayonPeriastre())/2;
    }

    public double getDPA() {
        return getDGA() * Math.sqrt(1 - Math.pow(getE(), 2));
    }

    public double getParametreEllipse() {
        return getDGA() * (1-getE() * getE());
    }

    public double getMoyenneMouvement() {
        return 2 * PI / getPeriodeTheorique();
    }

    public double getAnomalieMoyenne() {
        return getMoyenneMouvement() * (temps - tempsP);
    }

    public double getAnomalieExcentrique() {
        return calculApproximationAnomalieExcentrique(getAnomalieMoyenne() /(1-getE()));
    }

    public double getAnomalieVraie() {
        return 2*Math.atan(Math.sqrt((1+getE())/(1-getE())*tan(getAnomalieExcentrique()/2)));
    }

    public Point2D getVecteurRayon(){
        return new Point2D(getDGA()*(cos(getAnomalieExcentrique() - getE())), getDGA()*(Math.sqrt(1-getE()*getE())) * sin(getAnomalieExcentrique()));
    }

    //Distance entre centre et le foyer
    public double getC(){
        return getDGA() / Constantes.ECHELLE * getE();
    }


    //double distance = calculDistance();
    //double force = forceTerreSoleil(distance);
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

    /*public Point2D position(double c) {

        double deltaT = temps - tempsP;

        double anomalieMoyenne = (getMoyenneMouvement() * deltaT) % (2*Math.PI);
        if (anomalieMoyenne < 0) anomalieMoyenne += 2 * Math.PI;

        double E = calculApproximationAnomalieExcentrique(anomalieMoyenne);

        double x = getDGA() * (Math.cos(E) - getE());
        double y = -getDGA() * (Math.sqrt(1 - getE() * getE()) * Math.sin(E));

        Point2D vecteurRayon = new Point2D(x, y);
        Point2D pointEspace = ancre.add(vecteurRayon.multiply(1.0 / Constantes.ECHELLE));

        setX(pointEspace.getX() - taille.getX()/2 + c + corpsCentrale.getLargeur()/2);
        setY(pointEspace.getY() - taille.getY()/2 + corpsCentrale.getLargeur()/2);

        return pointEspace; //appliqué l'échelle avant d'add getAncre()
    }*/

    public Point2D position(double c) {
        double deltaT = temps - tempsP;

        double anomalieMoyenne = (getMoyenneMouvement() * deltaT) % (2 * Math.PI);
        if (anomalieMoyenne < 0) anomalieMoyenne += 2 * Math.PI;

        double E = calculApproximationAnomalieExcentrique(anomalieMoyenne);

        // Calcul de la position relative au foyer (le corps central)
        // On multiplie par 1/ECHELLE pour passer des mètres aux pixels
        double xRelatif = (getDGA() * (Math.cos(E) - getE())) / Constantes.ECHELLE;
        double yRelatif = (-getDGA() * (Math.sqrt(1 - getE() * getE()) * Math.sin(E))) / Constantes.ECHELLE;

        // Le centre du corps autour duquel on tourne
        double centreAncreX = ancre.getX() + corpsCentrale.getLargeur() / 2;
        double centreAncreY = ancre.getY() + corpsCentrale.getLargeur() / 2;

        // Position finale : Centre de l'ancre + position relative - moitié de la taille du satellite pour le centrer
        setX(centreAncreX + xRelatif - taille.getX() / 2);
        setY(centreAncreY + yRelatif - taille.getY() / 2);

        return new Point2D(getX(), getY());
    }

    public double calculForceGravitionnelle(){
        return 6.67*Math.pow(10, -11) * getMasseSatellite() * getMasseCorpsCentral() / (calculDistance()*calculDistance());
    }

    public Point2D centrer(Point2D point2D){
        return new Point2D(getX() + getLargeur()/2, getY() + getLargeur()/2);
    }

    public static double norme(Point2D p){
        return Math.sqrt(p.getX() * p.getX() + p.getY()*p.getY());
    }

    public double calculDistance (){
        return getParametreEllipse()/(1+getE()*cos(getAnomalieVraie() - angleP));
    }

    public double forceTerreSoleil(double distance){
        return -calculForceGravitionnelle() * getMasseCorpsCentral() * getMasseSatellite() / (distance * distance);
    }

    public double calculVitesseA(){
        return Math.sqrt(getGm()*(1-getE()/ getDGA() *(1+getE())));
    }

    public double calculVitesseP(){
        return Math.sqrt(getGm()*(1+getE())/ getDGA() *(1-getE()));
    }

    public double calculPeriode(){
        return 2*PI*Math.sqrt(getDGA()*getDGA()*getDGA()/ getGm());
    }

    public double normeRayon(){
        return Math.sqrt(getVecteurRayon().getX()*getVecteurRayon().getX() + getVecteurRayon().getY()*getVecteurRayon().getX());
    }

}



