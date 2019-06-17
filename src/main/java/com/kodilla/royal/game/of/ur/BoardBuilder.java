package com.kodilla.royal.game.of.ur;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

public class BoardBuilder {

    private final static double DICE_ROLLING_ANIMATION_DURATION = 4000d;
    private final static double DICE_ROLLING_ANIMATION_STEP = 100d;
    private final static double DICE_ROLLING_ANIMATION_STEP_MODIFIER = 1.1d;

    public static Node createTopBar() {
        HBox topBarPnl = new HBox();
        topBarPnl.setMinSize(670d, 40d);
        topBarPnl.setMaxSize(670d, 40d);
        topBarPnl.getStyleClass().add("border_down");

        Button newGameBtn = new Button("N");
        newGameBtn.setMinSize(40d, 40d);
        newGameBtn.setMaxSize(40d, 40d);
        newGameBtn.setAlignment(Pos.CENTER);

        Label titleLbl = new Label("The Royal Game Of Ur");
        titleLbl.setMinSize(590d, 40d);
        titleLbl.setMaxSize(590d, 40d);
        titleLbl.setAlignment(Pos.CENTER);

        Button helpBtn = new Button("?");
        helpBtn.setMinSize(40d, 40d);
        helpBtn.setMaxSize(40d, 40d);
        helpBtn.setAlignment(Pos.CENTER);

        topBarPnl.getChildren().addAll(newGameBtn, titleLbl, helpBtn);
        return topBarPnl;
    }

    public static Node createHumanPlayerPanel(final Player humanPlayer) {
        VBox humanPlayerPnl = new VBox();
        humanPlayerPnl.setMinSize(500d, 90d);
        humanPlayerPnl.setMaxSize(500d, 90d);
        humanPlayerPnl.getStyleClass().add("border_down");

        Label titleLbl = new Label("Human Player");
        titleLbl.setMinSize(500d, 35d);
        titleLbl.setMaxSize(500d, 35d);
        titleLbl.setAlignment(Pos.CENTER);

        humanPlayerPnl.getChildren().addAll(
                titleLbl,
                createPiecesPanel(humanPlayer)
        );
        return humanPlayerPnl;
    }

    public static Node createComputerPlayerPanel(final Player computerPlayer) {
        VBox computerPlayerPnl = new VBox();
        computerPlayerPnl.setMinSize(500d, 90d);
        computerPlayerPnl.setMaxSize(500d, 90d);
        computerPlayerPnl.getStyleClass().add("border_down");

        Label titleLbl = new Label("Computer Player");
        titleLbl.setMinSize(500d, 35d);
        titleLbl.setMaxSize(500d, 35d);
        titleLbl.setAlignment(Pos.CENTER);

        computerPlayerPnl.getChildren().addAll(
                createPiecesPanel(computerPlayer),
                titleLbl
        );
        return computerPlayerPnl;
    }

    private static Node createPiecesPanel(final Player player) {
        HBox piecesPnl = new HBox();
        piecesPnl.setMinSize(500d, 55d);
        piecesPnl.setMaxSize(500d, 55d);

        Label finishedPiecesLbl = new Label("Finished pieces:");
        finishedPiecesLbl.setMinSize(90d, 55d);
        finishedPiecesLbl.setMaxSize(90d, 55d);
        finishedPiecesLbl.setAlignment(Pos.CENTER_RIGHT);

        HBox finishedPiecesPnl = new HBox(5d);
        finishedPiecesPnl.setMinHeight(55d);
        finishedPiecesPnl.setMaxHeight(55d);
        finishedPiecesPnl.setPadding(new Insets(5d, 5d, 10d, 5d));

        Label piecesToGoLbl = new Label("Pieces to go:");
        piecesToGoLbl.setMinSize(80d, 55d);
        piecesToGoLbl.setMaxSize(410d, 55d);
        HBox.setHgrow(piecesToGoLbl, Priority.ALWAYS);
        piecesToGoLbl.setAlignment(Pos.CENTER_RIGHT);

        HBox piecesToGoPnl = new HBox(5d);
        piecesToGoPnl.setMinHeight(55d);
        piecesToGoPnl.setMaxHeight(55d);
        piecesToGoPnl.setPadding(new Insets(5d, 5d, 10d, 5d));

        piecesPnl.getChildren().addAll(finishedPiecesLbl, finishedPiecesPnl, piecesToGoLbl, piecesToGoPnl);
        player.setPiecesBoxes(finishedPiecesPnl, piecesToGoPnl);
        return piecesPnl;
    }

    public static Node createGameBoard(final AnchorPane gameBoardPnl) {
        gameBoardPnl.setMinSize(500d, 200d);
        gameBoardPnl.setMaxSize(500d, 200d);
        gameBoardPnl.getStyleClass().addAll("board", "border_down");
        gameBoardPnl.setPadding(new Insets(10d));
        GridPane.setConstraints(gameBoardPnl, 0, 1);

        return gameBoardPnl;
    }

    public static Node createDicePanel(
            final ImageView diceImg,
            final Text diceRollResultTxt,
            final Button rollDiceBtn
    ) {
        VBox dicePnl = new VBox();
        dicePnl.setMinSize(170d, 290d);
        dicePnl.setMaxSize(170d, 290d);
        dicePnl.getStyleClass().add("border_down_and_left");

        Label titleLbl = new Label("The Dice");
        titleLbl.setMinSize(170d, 60d);
        titleLbl.setMaxSize(170d, 60d);
        titleLbl.setAlignment(Pos.CENTER);

        VBox diceImagePnl = new VBox();
        diceImagePnl.setMinSize(170d, 90d);
        diceImagePnl.setMaxSize(170d, 90d);
        diceImagePnl.setAlignment(Pos.CENTER);

        ClassLoader classLoader = BoardBuilder.class.getClassLoader();
        diceImg.setImage(new Image(classLoader.getResource("img/dice0.png").toExternalForm()));
        diceImg.getStyleClass().add("dice");
        diceImagePnl.getChildren().add(diceImg);

        VBox diceValuePnl = new VBox();
        diceValuePnl.setMinSize(170d, 70d);
        diceValuePnl.setMaxSize(170d, 70d);
        diceValuePnl.setAlignment(Pos.CENTER);

        diceRollResultTxt.setText("X");
        diceValuePnl.getChildren().add(diceRollResultTxt);

        rollDiceBtn.setText("Roll the dice");
        rollDiceBtn.setMinSize(170d, 40d);
        rollDiceBtn.setMaxSize(170d, 40d);

        dicePnl.getChildren().addAll(titleLbl, diceImagePnl, diceValuePnl, rollDiceBtn);
        return dicePnl;
    }

    public static Node createMessagePanel(final Text turnInfoTxt, final Text messageTxt) {
        VBox messagePnl = new VBox(5d);
        messagePnl.setMinSize(170d, 90d);
        messagePnl.setMaxSize(170d, 90d);
        messagePnl.setPadding(new Insets(5d));
        messagePnl.getStyleClass().add("border_left");

        turnInfoTxt.setText("Human Player's turn");
        turnInfoTxt.setWrappingWidth(160d);
        turnInfoTxt.setTextAlignment(TextAlignment.CENTER);
        turnInfoTxt.getStyleClass().add("message");

        messageTxt.setText("Roll the dice to start the game.");
        messageTxt.setWrappingWidth(160d);
        messageTxt.setTextAlignment(TextAlignment.CENTER);
        messageTxt.getStyleClass().add("message");

        messagePnl.getChildren().addAll(turnInfoTxt, messageTxt);
        return messagePnl;
    }

    public static Timeline createDiceRollingAnimation(final ImageView diceImg) {
        Timeline timeline = new Timeline();
        final KeyValue[] keyValues = new KeyValue[3];
        ClassLoader classLoader = BoardBuilder.class.getClassLoader();

        for (int i = 0; i < keyValues.length; i++) {
            keyValues[i] = new KeyValue(diceImg.imageProperty(),
                    new Image(classLoader.getResource("img/dice" + i + ".png").toExternalForm()));
        }

        double step = DICE_ROLLING_ANIMATION_STEP;
        int currentDiceFrame = 0;
        for (double i = step; i < DICE_ROLLING_ANIMATION_DURATION; i += (step *= DICE_ROLLING_ANIMATION_STEP_MODIFIER)) {
            timeline.getKeyFrames().add(new KeyFrame(
                    Duration.millis(i),
                    keyValues[currentDiceFrame++ % 3]
            ));
        }

        return timeline;
    }

    public static FadeTransition createMessageFadeTransition(Text messageTxt) {
        FadeTransition transition = new FadeTransition(Duration.seconds(5), messageTxt);
        transition.setFromValue(1d);
        transition.setByValue(1d);
        transition.setToValue(0d);
        return transition;
    }

}
