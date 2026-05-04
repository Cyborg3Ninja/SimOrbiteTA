package ca.qc.bdeb.sim.simorbite;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;

public abstract class Vecteur {

        /** Position de l'objet (Centre). */
        protected Point2D position;
        /** Vélocité de l'objet. */
        protected Point2D velocite;
        /** Accélération de l'objet. */
        protected Point2D acceleration;
        /** Taille de l'objet : largeur et hauteur. */
        protected Point2D taille;
        /** Image utilisée pour l'affichage. */


        public Vecteur(double x, double y, double rayon) {
            this.position = new Point2D(x, y );
            this.taille = new Point2D(rayon, rayon);
            this.velocite = Point2D.ZERO;
            this.acceleration = Point2D.ZERO;
        }

        /** Dessine l'objet. */
        public abstract void draw(GraphicsContext gc);


        // ----- Position -----
        public double getX() {return position.getX();}
        public void setX(double x) {position = new Point2D(x, position.getY());}
        public double getY() {return position.getY();}
        public void setY(double y) {position = new Point2D(position.getX(), y);}


        // ----- Taille -----
        public Point2D getTaille() {return taille;}


        // ----- Position  -----
        public Point2D getPosition() {return position;}


        // ----- Largeur et hauteur -----
        public double getLargeur() {return taille.getX();}
        public double getHauteur() {return taille.getY();}


    }


