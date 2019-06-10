package com.kodilla.royal.game.of.ur;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

public class GameRunner extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Board boardController = new Board();
        primaryStage.setTitle("The Royal Game Of Ur by Paweł Bandura");
        primaryStage.setScene(boardController.getScene());
        primaryStage.show();
    }
}
