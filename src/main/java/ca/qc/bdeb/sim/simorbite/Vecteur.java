package ca.qc.bdeb.sim.simorbite;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Vecteur {

        /** Position de l'objet (coin haut-gauche). */
        protected Point2D position;
        /** Vélocité de l'objet. */
        protected Point2D velocite;
        /** Accélération de l'objet. */
        protected Point2D acceleration;
        /** Taille de l'objet : largeur et hauteur. */
        protected Point2D taille;
        /** Image utilisée pour l'affichage. */
        protected Image image;
        /**  Constructeur d'un objet du jeu. */


        public Vecteur(double x, double y, double largeur, double hauteur) {
            this.position = new Point2D(x, y);
            this.taille = new Point2D(largeur, hauteur);
            this.velocite = Point2D.ZERO;
            this.acceleration = Point2D.ZERO;
        }


        /** Mise à jour de la physique selon le temps. */
        protected void updatePhysique(double dt) {
            // v = v + a·dt
            velocite = velocite.add(acceleration.multiply(dt));
            // p = p + v·dt
            position = position.add(velocite.multiply(dt));


        }


        /** Mise à jour complète de l'objet. */
        public void update(double dt) {
            updatePhysique(dt);
        }


        /** Dessine l'objet. */
        public void draw(GraphicsContext gc) {
            //    gc.drawImage(image, position.getX(), position.getY(), taille.getX(), taille.getY());
        }


        // ----- Position -----
        public double getX() {return position.getX();}
        public void setX(double x) {position = new Point2D(x, position.getY());}
        public double getY() {return position.getY();}
        public void setY(double y) {position = new Point2D(position.getX(), y);}


        // ----- Vitesse -----
        public double getVx() {return velocite.getX();}
        public void setVx(double vx) {
            velocite = new Point2D(vx, velocite.getY());}
        public double getVy() {return velocite.getY();}
        public void setVy(double vy) {
            velocite = new Point2D(velocite.getX(), vy);}


        // ----- Accélération -----
        public double getAx() {return acceleration.getX();}
        public void setAx(double ax) {acceleration = new Point2D(ax, acceleration.getY());}
        public double getAy() {return acceleration.getY();}
        public void setAy(double ay) {acceleration = new Point2D(acceleration.getX(), ay);}


        // ----- Taille -----
        public Point2D getTaille() {return taille;}
        public void setTaille(Point2D taille) {this.taille = taille;}


        // ----- Vecteurs  -----
        public Point2D getPosition() {return position;}
        public void setPosition(Point2D position) {this.position = position;}
        public Point2D getVelocite() {return velocite;}
        public void setVelocite(Point2D velocite) {this.velocite = velocite;}
        public Point2D getAcceleration() {return acceleration;}
        public void setAcceleration(Point2D acceleration) {this.acceleration = acceleration;}


        // ----- Positionnement -----
        public void setGauche(double x) {position = new Point2D(x, position.getY());}
        public void setDroite(double x) {position = new Point2D(x - getLargeur(), position.getY());}
        public void setHaut(double y) {position = new Point2D(position.getX(), y);}
        public void setBas(double y) {position = new Point2D(position.getX(), y - getHauteur());}
        public void setCentreX(double x) {position = new Point2D(x - getLargeur() / 2, position.getY());}
        public void setCentreY(double y) {position = new Point2D(position.getX(), y - getHauteur() / 2);}


        public Point2D centre() {return position.add(taille.multiply(0.5));}


        public double haut() {return position.getY();}
        public double bas() {return position.getY() + getHauteur();}
        public double gauche() {return position.getX();}
        public double droite() {return position.getX() + getLargeur();}


        // ----- Largeur et hauteur -----
        public double getLargeur() {return taille.getX();}
        public double getHauteur() {return taille.getY();}
        public void setLargeur(double largeur) {taille = new Point2D(largeur, taille.getY());}
        public void setHauteur(double hauteur) {taille = new Point2D(taille.getX(), hauteur);}


    }


