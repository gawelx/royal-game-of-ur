package com.kodilla.royal.game.of.ur;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.Random;

public class BoardController {

    public final static byte HUMAN_PLAYER = 1;
    public final static byte COMPUTER_PLAYER = 2;

    private final static byte ROW_COUNT = 3;
    private final static byte COLUMN_COUNT = 8;
    private final static double DICE_ROLLING_DURATION = 4000.0;

    private final Field[][] fields;
    private byte currentDiceFrame;
    private Timeline diceRollingAnimation;
    private byte lastDiceResult;
    private Player currentPlayer;

    private final Player humanPlayer;
    private final Player computerPlayer;

    @FXML private Text turnInfo;
    @FXML private Text messageBox;
    @FXML private ImageView diceImage;
    @FXML private Text diceValue;

    public BoardController() throws Exception {
        fields = new Field[ROW_COUNT][COLUMN_COUNT];
        for (byte row = 0; row < ROW_COUNT; row++) {
            for (byte col = 0; col < COLUMN_COUNT; col++) {
                if (row % 2 == 1 || col > 3 || col < 2) {
                    fields[row][col] = new Field(row, col, (row % 2 == 0 || col == 4));
                }
            }
        }
        currentDiceFrame = 0;
        diceRollingAnimation = null;
        lastDiceResult = 0;

        Route route = new Route();
        currentPlayer = humanPlayer = new HumanPlayer(PieceColor.DARK,
                route.getRoute(HUMAN_PLAYER, Route.SIMPLE_ROUTE, fields));
        computerPlayer = new ComputerPlayer(PieceColor.LIGHT,
                route.getRoute(COMPUTER_PLAYER, Route.SIMPLE_ROUTE, fields));
    }

    @FXML protected void handleRollTheDiceAction(ActionEvent event) {
        messageBox.setText("");
        if (diceRollingAnimation == null) {
            diceRollingAnimation = prepareDiceRollingAnimation();
        }
        Random randomIntGenerator = new Random();
        lastDiceResult = (byte) randomIntGenerator.nextInt(4);
        diceRollingAnimation.play();
    }

    private Timeline prepareDiceRollingAnimation() {
        Timeline timeline = new Timeline();
        final KeyValue[] keyValues = new KeyValue[3];
        ClassLoader classLoader = getClass().getClassLoader();
        System.out.println(classLoader.getResource("img/dice1.png"));
        keyValues[0] = new KeyValue(diceImage.imageProperty(),
                new Image(classLoader.getResource("img/dice1.png").toString()));
        keyValues[1] = new KeyValue(diceImage.imageProperty(),
                new Image(classLoader.getResource("img/dice2.png").toString()));
        keyValues[2] = new KeyValue(diceImage.imageProperty(),
                new Image(classLoader.getResource("img/dice3.png").toString()));

        double step = 100.0;
        for (double i = step; i < DICE_ROLLING_DURATION; i += (step *= 1.1)) {
            timeline.getKeyFrames().add(new KeyFrame(Duration.millis(i),
                    keyValues[currentDiceFrame++ % 3]));
        }

        timeline.setOnFinished(event -> {
            diceValue.setText(Integer.toString(lastDiceResult));
            messageBox.setText(currentPlayer == humanPlayer ? "Pick the piece to move." : "");
        });

        return timeline;
    }

}
