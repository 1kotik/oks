package com.example.lab1.initializer;

import com.example.lab1.Main;
import com.example.lab1.controller.Controller;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;

import javafx.stage.Stage;

import java.io.IOException;

public class SceneInitializer {

    public void initialize(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
        stage.setScene(new Scene(fxmlLoader.load()));
        Controller controller = fxmlLoader.getController();
        stage.setOnCloseRequest(controller.onClose());
        stage.setTitle("lab3");
        stage.setResizable(false);
        stage.show();
    }
}
