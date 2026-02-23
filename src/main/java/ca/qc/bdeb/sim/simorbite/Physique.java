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
    final double RAYONPERIASTRE = 147099894;
    final double RAYONAPOASTRE = 149598023;
    final double MTERRE = 5.972 * Math.pow(10, 24);
    final double MSOLEIL = 1.989 * Math.pow(10, 30);
    final double GTERRESOLEIL = 3.6 * Math.pow(10, 22);


    double e = (RAYONAPOASTRE - RAYONPERIASTRE) / (RAYONAPOASTRE + RAYONPERIASTRE); //Excentricite
    double DGA = (RAYONAPOASTRE + RAYONPERIASTRE) / 2; //Demi Grand Axe
    double DPA = DGA * Math.sqrt(1 - Math.pow(e, 2)); // Demi Petit Axe
    double parametreEllipse = DGA * (1-e * e);
    double angleP = 0; //angle du periastre

    double distance = calculDistance();
    double force = forceTerreSoleil(distance);

    double moyenneMouvement = 2 * PI / PERIODETHEORIQUE;
    double deltaT = temps - tempsP; //a revoir
    double anomalieMoyenne = moyenneMouvement * (deltaT);
    double anomalieExcentrique = calculApproximationAnomalieExcentrique();
    double anomalieVraie = 2*Math.atan(Math.sqrt((1+e)/(1-e)*tan(anomalieExcentrique/2)));

    public double calculApproximationAnomalieExcentrique(){
        double u = anomalieMoyenne /(1-e);
        double uAncien = 0; //Initialise

        do{
            uAncien = u;
            u = e*sin(u) + anomalieMoyenne;

        } while(Math.abs(u - uAncien) < DEGREEPRECISION);

        return u;
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



}

