package com.kodilla.royal.game.of.ur;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GameRunner extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Game game = new Game();
        primaryStage.setTitle("The Royal Game Of Ur by Paweł Bandura");
        primaryStage.setScene(new Scene(game.getBoardController(), 670, 420));
        //primaryStage.setResizable(false);
        primaryStage.show();
    }

}
