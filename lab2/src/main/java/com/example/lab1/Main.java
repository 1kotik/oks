package com.example.lab1;

import com.example.lab1.initializer.SceneInitializer;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        SceneInitializer sceneInitializer = new SceneInitializer();
        sceneInitializer.initialize(stage);

    }

    public static void main(String[] args) {
        launch();
    }
}