package ca.qc.bdeb.sim.simorbite;

import javafx.scene.canvas.GraphicsContext;

public class Astre extends Vecteur {

    protected double masse;
    protected double rayon;

    public Astre(double x, double y, double largeur, double hauteur, double masse, double rayon) {
        super(x, y, largeur, hauteur);
        this.masse = masse;
        this.rayon = rayon;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.fillOval(getX(), getY(), getLargeur(), getHauteur());
    }

    public double masseToRayon(double masse, int constante){
    //9.807 = G * Mt / Rt^2 terre

        return rayon;
    }
}
