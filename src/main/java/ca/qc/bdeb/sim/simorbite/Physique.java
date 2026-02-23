package ca.qc.bdeb.sim.simorbite;

import javafx.geometry.Point2D;

public class Physique {
    Point2D ancre = Point2D.ZERO;
    Point2D apoastre = Point2D.ZERO;
    Point2D periastre = Point2D.ZERO;

    Physique(Point2D ancre, Point2D apoastre, Point2D periastre) {
        this.ancre = ancre;
        this.apoastre = apoastre;
        this.periastre = periastre;
    }

    double RayonApo = norme(ancre.subtract(apoastre));
    double RayonPeri = norme(ancre.subtract(periastre));

    double dgA = (RayonApo + RayonPeri)/2;

    double excentE = (RayonApo - RayonPeri)/(RayonApo + RayonPeri);
    double excentC = dgA * excentE;


    public static double norme(Point2D p){
    return Math.sqrt(p.getX() * p.getX() + p.getY()*p.getY());
    }

}

