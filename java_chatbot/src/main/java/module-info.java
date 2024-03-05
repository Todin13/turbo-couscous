module ia.institut {
    requires javafx.controls;
    requires javafx.fxml;

    opens ia.institut to javafx.fxml;
    exports ia.institut;
}
