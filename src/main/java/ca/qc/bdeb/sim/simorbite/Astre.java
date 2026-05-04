package ca.qc.bdeb.sim.simorbite;

import javafx.scene.canvas.GraphicsContext;

public class Astre extends Vecteur {

    protected double masse;
    protected String nom;

    public Astre(double x, double y, double masse, double rayon, String nom) {
        super(x, y, rayon);
        this.nom = nom;
        this.masse = masse;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.fillOval(getX(), getY(), getLargeur(), getHauteur());
    }

    public String getNom(){
        return nom;
    }


    @Override
    public String toString() {
        return this.nom;
    }

}
