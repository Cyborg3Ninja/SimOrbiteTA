package ca.qc.bdeb.sim.simorbite;

import javafx.scene.canvas.GraphicsContext;

public class Astre extends Vecteur {

    protected double masse;
    public Astre(double x, double y, double masse, double rayon) {
        super(x, y, rayon);
        this.masse = masse;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.fillOval(getX(), getY(), getLargeur(), getHauteur());
    }

    public void masseToRayon(double masse, int constante){
    //9.807 = G * Mt / Rt^2 terre
//return rayon
    }


}
