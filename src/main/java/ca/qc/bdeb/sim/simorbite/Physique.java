package ca.qc.bdeb.sim.simorbite;

import javafx.geometry.Point2D;

import static java.lang.Math.*;

public class Physique {

    Point2D ancre = Point2D.ZERO;
    Satellite satellite;
    double temps;
    double tempsP; //temps au passage du periastre
    Physique(Satellite satellite, Point2D ancre, double temps, double tempsP) {
        this.satellite = satellite;
        this.ancre = ancre;
        this.temps = temps;
        this.tempsP = tempsP;
    }

    //CONSTANTES
    final double DEGREEPRECISION = 0.005;
    double angleP = 0; //angle du periastre
    double deltaT = temps - tempsP; //a revoir



    //Demie grand axe de la terre autour du soleil
    /*final double RAYONPERIASTRE = 147099894;
    final double RAYONAPOASTRE = 149598023;
    final double MTERRE = 5.972 * Math.pow(10, 24);
    final double MSOLEIL = 1.989 * Math.pow(10, 30);
    final double FGTERRESOLEIL = 3.6 * Math.pow(10, 22);
    final double GM = 398600.4418; //Parametre gravitionnelle standard Terre
    final double PERIODETHEORIQUETERRE = 31558145;*/

    //Excentricite
    public double getE(){
        return (satellite.getRayonApoastre() - satellite.getRayonPeriastre()) / (satellite.getRayonApoastre() + satellite.getRayonPeriastre());
    }
    public double getDGA(){
        return (satellite.getRayonApoastre() + satellite.getRayonPeriastre())/2;
    }

    public double getDPA() {
        return getDGA() * Math.sqrt(1 - Math.pow(getE(), 2));
    }

    public double getParametreEllipse() {
        return getDGA() * (1-getE() * getE());
    }

    public double getMoyenneMouvement() {
        return 2 * PI / satellite.getPeriodeTheorique();
    }

    public double getAnomalieMoyenne() {
        return getMoyenneMouvement() * deltaT;
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

    public Point2D getPosition(){
        return ancre.add(getVecteurRayon());
    }

    //Distance entre centre et le foyer
    public double getC(){
        return getDGA() / Constantes.ECHELLE * getE();
    }



    /*double anomalieMoyenne = moyenneMouvement * (deltaT);
    double anomalieExcentrique = calculApproximationAnomalieExcentrique(anomalieMoyenne /(1-e));
    double anomalieVraie = 2*Math.atan(Math.sqrt((1+e)/(1-e)*tan(anomalieExcentrique/2)));
    Point2D vecteurRayon = new Point2D(DGA*(cos(anomalieExcentrique - e)), DGA*(Math.sqrt(1-e*e)) * sin(anomalieExcentrique));
    */


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


    public Point2D position(double tempsActuel) {

        double deltaT = tempsActuel - tempsP;

        double anomalieMoyenne = getMoyenneMouvement() * deltaT;

        double E = calculApproximationAnomalieExcentrique(anomalieMoyenne);

        double x = getDGA() * (Math.cos(E) - getE());
        double y = -getDGA() * (Math.sqrt(1 - getE() * getE()) * Math.sin(E));

        Point2D vecteurRayon = new Point2D(x, y);

        return ancre.add(vecteurRayon.multiply(1.0 / Constantes.ECHELLE)); //appliqué l'échelle avant d'add l'ancre
    }

    public double calculForceGravitionnelle(){
        return 6.67*Math.pow(10, -11) * satellite.getMasseSatellite() * satellite.getMasseCorpsCentral() / (calculDistance()*calculDistance());
    }

    public Point2D centrer(Point2D point2D){
        return new Point2D(satellite.getX() + satellite.getLargeur()/2, satellite.getY() + satellite.getLargeur()/2);
    }

    public static double norme(Point2D p){
    return Math.sqrt(p.getX() * p.getX() + p.getY()*p.getY());
    }

    public double calculDistance (){
        return getParametreEllipse()/(1+getE()*cos(getAnomalieVraie() - angleP));
    }

    public double forceTerreSoleil(double distance){
        return -calculForceGravitionnelle() * satellite.getMasseCorpsCentral() * satellite.getMasseSatellite() / (distance * distance);
    }
    
    public double calculVitesseA(){
        return Math.sqrt(satellite.getGm()*(1-getE()/ getDGA() *(1+getE())));
    }

    public double calculVitesseP(){
        return Math.sqrt(satellite.getGm()*(1+getE())/ getDGA() *(1-getE()));
    }

    public double calculPeriode(){
        return 2*PI*Math.sqrt(getDGA()*getDGA()*getDGA()/ satellite.getGm());
    }

    public double normeRayon(){
        return Math.sqrt(getVecteurRayon().getX()*getVecteurRayon().getX() + getVecteurRayon().getY()*getVecteurRayon().getX());
    }

    public void setAncre(Point2D point2D){
        ancre = point2D;
    }


}

