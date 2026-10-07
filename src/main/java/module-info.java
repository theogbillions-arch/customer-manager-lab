module com.example.customermanagerlab {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.customermanagerlab to javafx.fxml;
    exports com.example.customermanagerlab;
}