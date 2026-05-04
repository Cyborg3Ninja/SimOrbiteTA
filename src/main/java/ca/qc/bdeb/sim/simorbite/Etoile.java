package ca.qc.bdeb.sim.simorbite;

import ca.qc.bdeb.sim.simorbite.JavaFX;

class Etoile {
    double x, y;
    double alpha; // transparence (0 → 1)
    double vitesse; // vitesse de scintillement
    boolean fadeIn;

    public Etoile(double x, double y) {
        this.x = x;
        this.y = y;
        this.alpha = Math.random();
        this.vitesse = 0.005 + Math.random() * 0.01;
        this.fadeIn = Math.random() > 0.5;
    }

    public void update() {
        if (fadeIn) {
            alpha += vitesse;
            if (alpha >= 1) fadeIn = false;
        } else {
            alpha -= vitesse;
            if (alpha <= 0) {
                fadeIn = true;
                // repositionner ailleurs pour "pop"
                x = Math.random() * JavaFX.WIDTH;
                y = Math.random() * JavaFX.HEIGHT;
            }
        }
    }
}
