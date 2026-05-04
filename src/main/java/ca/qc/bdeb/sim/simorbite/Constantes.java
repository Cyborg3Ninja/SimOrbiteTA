package ca.qc.bdeb.sim.simorbite;

public class Constantes {
    public static final double ECHELLE = 1000000; //1 000 000km = 1 pixel conversion

    public static final double DISTANCEFOYERSOLEIL = (double) (147099894 + 149598023) /2 /(Constantes.ECHELLE)* (147099894 - 149598023)/(147099894 + 149598023);
}
