package com.kodilla.royal.game.of.ur;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameRunner extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = new Board();
        primaryStage.setTitle("The Royal Game Of Ur by Paweł Bandura");
        primaryStage.setScene(new Scene(root, 670, 420));
        //primaryStage.setResizable(false);
        primaryStage.show();
    }
}
