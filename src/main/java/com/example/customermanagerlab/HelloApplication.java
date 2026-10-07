package com.example.customermanagerlab;

// Keep the 'package ...;' line your project already has above this comment.

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        loader.setController(new HelloController());   // view <-> controller link
        Scene scene = new Scene(loader.load());
        stage.setTitle("Customer Manager");
        stage.setScene(scene);
        stage.setMinWidth(420);
        stage.setMinHeight(380);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}