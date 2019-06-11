package com.kodilla.royal.game.of.ur;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class GameRunner extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        Parent root = FXMLLoader.load(classLoader.getResource("FXML/board.fxml"));
        primaryStage.setTitle("The Royal Game Of Ur by Paweł Bandura");
        primaryStage.setScene(new Scene(root, 670, 420));
        primaryStage.show();
    }
}
