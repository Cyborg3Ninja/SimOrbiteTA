package ca.qc.bdeb.sim.simorbite;

import javafx.geometry.Point2D;

import static java.lang.Math.*;

public class Physique {

    Point2D ancre = Point2D.ZERO;
    double temps;
    double tempsP; //temps au passage du periastre

    Physique(Point2D ancre, double temps, double tempsP) {
        this.ancre = ancre;
        this.temps = temps;
        this.tempsP = tempsP;
    }

    //CONSTANTES
    final double DEGREEPRECISION = 0.005;
    final double GM = 398600.4418; //Parametre gravitionnelle standard
    final double PERIODETHEORIQUE = 31558145;

    //Demie grand axe de la terre autour du soleil
    static final double RAYONPERIASTRE = 147099894;
    static final double RAYONAPOASTRE = 149598023;
    final double MTERRE = 5.972 * Math.pow(10, 24);
    final double MSOLEIL = 1.989 * Math.pow(10, 30);
    final double GTERRESOLEIL = 3.6 * Math.pow(10, 22);


    static double e = (RAYONAPOASTRE - RAYONPERIASTRE) / (RAYONAPOASTRE + RAYONPERIASTRE); //Excentricite
    static double DGA = (RAYONAPOASTRE + RAYONPERIASTRE) / 2; //Demi Grand Axe
    static double DPA = DGA * Math.sqrt(1 - Math.pow(e, 2)); // Demi Petit Axe
    double parametreEllipse = DGA * (1-e * e);
    double angleP = 0; //angle du periastre

    //double distance = calculDistance();
    //double force = forceTerreSoleil(distance);

    double moyenneMouvement = 2 * PI / PERIODETHEORIQUE;
    double anomalieMoyenne = moyenneMouvement * (calculDeltaT(temps));
    double anomalieExcentrique = calculApproximationAnomalieExcentrique(anomalieMoyenne);
    double anomalieVraie = 2*Math.atan(Math.sqrt((1+e)/(1-e)*tan(anomalieExcentrique/2)));

    Point2D vecteurRayon = new Point2D(DGA*(cos(anomalieExcentrique - e)), DGA*(Math.sqrt(1-e*e)) * sin(anomalieExcentrique));


    public double calculApproximationAnomalieExcentrique(double M) {

        double u = M;
        double uAncien;

        do {
            uAncien = u;
            u = e * Math.sin(u) + M;
        } while (Math.abs(u - uAncien) > DEGREEPRECISION);

        return u;
    }


    public Point2D position(double tempsActuel) {

        double deltaT = tempsActuel - tempsP;

        //anomalie moyenne
        double M = moyenneMouvement * deltaT;

        double E = calculApproximationAnomalieExcentrique(M);

        double x = DGA * (Math.cos(E) - e); //horizontale
        double y = -DPA * Math.sin(E);      // vertical

        Point2D vecteurRayon = new Point2D(x, y);

        return ancre.add(vecteurRayon.multiply(1.0 / Constantes.ECHELLE)); //appliqué l'échelle avant d'ajouter l'ancre
    }

    public double calculDeltaT(double temps ){
        return temps - tempsP;
    }

    public static double norme(Point2D p){
    return Math.sqrt(p.getX() * p.getX() + p.getY()*p.getY());
    }

    public double calculDistance (){
        return parametreEllipse/(1+e*cos(anomalieVraie - angleP));
    }

    public double forceTerreSoleil(double distance){
        return -GTERRESOLEIL * MSOLEIL * MTERRE / (distance * distance);
    }
    
    public double calculVitesseA(){
        return Math.sqrt(GM*(1-e)/ DGA *(1+e));
    }

    public double calculVitesseP(){
        return Math.sqrt(GM*(1+e)/ DGA *(1-e));
    }

    public double calculPeriode(){
        return 2*PI*Math.sqrt(DGA*DGA*DGA/GM);
    }

    public double normeRayon(){
        return Math.sqrt(vecteurRayon.getX()*vecteurRayon.getX() + vecteurRayon.getY()*vecteurRayon.getY());
    }



}

