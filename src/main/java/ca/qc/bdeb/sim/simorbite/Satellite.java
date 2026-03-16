package ca.qc.bdeb.sim.simorbite;

public class Satellite extends Astre {
    double rayonPeriastre;
    double rayonApoastre;
    double masseSatellite;
    double masseCorpsCentral;
    double Gm;
    double periodeTheorique;

    public Satellite(double x, double y, double rayon, double rayonPeriastre,
                     double rayonApoastre, double masseSatellite, double masseCorpsCentral,
                     double Gm, double periodeTheorique) {
        super(x, y, 0, rayon);
        this.rayonPeriastre = rayonPeriastre;
        this.rayonApoastre = rayonApoastre;
        this.masseSatellite = masseSatellite;
        this.masseCorpsCentral = masseCorpsCentral;
        this.Gm = Gm;
        this.periodeTheorique = periodeTheorique;

    }

    public double getRayonPeriastre() {
        return rayonPeriastre;
    }

    public double getRayonApoastre() {
        return rayonApoastre;
    }

    public double getMasseSatellite() {
        return masseSatellite;
    }

    public double getMasseCorpsCentral() {
        return masseCorpsCentral;
    }

    public double getGm() {
        return Gm;
    }

    public double getPeriodeTheorique() {
        return periodeTheorique;
    }
    /*
    final double RAYONPERIASTRE = 147099894;
    final double RAYONAPOASTRE = 149598023;
    final double MTERRE = 5.972 * Math.pow(10, 24);
    final double MSOLEIL = 1.989 * Math.pow(10, 30);
    final double FGTERRESOLEIL = 3.6 * Math.pow(10, 22);
    final double Gm = 398600.4418; //Parametre gravitionnelle standard Terre
    final double PERIODETHEORIQUETERRE = 31558145;*/
}
