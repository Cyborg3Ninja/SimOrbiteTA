module ca.qc.bdeb.sim.simorbite {
    requires javafx.controls;
    requires javafx.fxml;


    opens ca.qc.bdeb.sim.simorbite to javafx.fxml;
    exports ca.qc.bdeb.sim.simorbite;
}