package com.kodilla.royal.game.of.ur;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

import static com.kodilla.royal.game.of.ur.Game.PIECES_COUNT;

public class BoardController extends GridPane {

    private final double DICE_ROLLING_ANIMATION_DURATION = 4000d;
    private final double DICE_ROLLING_ANIMATION_STEP = 100d;
    private final double DICE_ROLLING_ANIMATION_STEP_MODIFIER = 1.1d;
    private final int FADE_TIME = 500;

    private final ImageView diceImg;
    private final Text diceRollResultTxt;
    private final Text turnInfoTxt;
    private final Text messageTxt;
    private final Button rollDiceBtn;

    private final StackPane playPnl;
    private final AnchorPane gameBoardPnl;

    private final HBox humanPlayerFinishedPiecesPnl;
    private final HBox humanPlayerReadyToGoPiecesPnl;

    private final HBox computerPlayerFinishedPiecesPnl;
    private final HBox computerPlayerReadyToGoPiecesPnl;

    private final Timeline diceRollAnimation;
    private final FadeTransition messageFadeOutTransition;

    public BoardController() {
        this.humanPlayerFinishedPiecesPnl = new HBox();
        this.humanPlayerReadyToGoPiecesPnl = new HBox();

        this.playPnl = new StackPane();
        this.gameBoardPnl = new AnchorPane();

        this.computerPlayerFinishedPiecesPnl = new HBox();
        this.computerPlayerReadyToGoPiecesPnl = new HBox();

        this.diceImg = new ImageView();
        this.diceRollResultTxt = new Text();
        this.rollDiceBtn = new Button();
        this.diceRollAnimation = new Timeline();

        this.turnInfoTxt = new Text();

        this.messageTxt = new Text();
        this.messageFadeOutTransition = new FadeTransition();

        setMinSize(670d, 420d);
        setMaxSize(670d, 420d);
        getStyleClass().add("root");

        ClassLoader classLoader = getClass().getClassLoader();
        getStylesheets().add(classLoader.getResource("css/board.css").toExternalForm());

        add(createTopBar(), 0, 0, 2, 1);
        add(createHumanPlayerPanel(), 0, 1);
        add(createGameBoard(), 0, 2);
        add(createComputerPlayerPanel(), 0, 3);
        add(createDicePanel(), 1, 1, 1, 2);
        add(createMessagePanel(), 1, 3);

        initDiceRollingAnimation();
        initMessageFadeOutTransition();
    }

    /*
    * Initializing the UI
     */
    private Node createTopBar() {
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

    private Node createHumanPlayerPanel() {
        VBox playerPnl = new VBox();
        Label titleLbl = new Label("Human Player");
        HBox piecesPnl = new HBox();

        createPlayerPanel(
                playerPnl,
                piecesPnl,
                titleLbl,
                humanPlayerFinishedPiecesPnl,
                humanPlayerReadyToGoPiecesPnl
        );

        playerPnl.getChildren().addAll(titleLbl, piecesPnl);

        return playerPnl;
    }

    private Node createComputerPlayerPanel() {
        VBox playerPnl = new VBox();
        Label titleLbl = new Label("Computer Player");
        HBox piecesPnl = new HBox();

        createPlayerPanel(
                playerPnl,
                piecesPnl,
                titleLbl,
                computerPlayerFinishedPiecesPnl,
                computerPlayerReadyToGoPiecesPnl
        );

        playerPnl.getChildren().addAll(piecesPnl, titleLbl);

        return playerPnl;
    }

    private void createPlayerPanel(
            final VBox playerPnl,
            final HBox piecesPnl,
            final Label titleLbl,
            final HBox finishedPiecesPnl,
            final HBox readyToGoPiecesPnl) {

        playerPnl.setMinSize(500d, 90d);
        playerPnl.setMaxSize(500d, 90d);
        playerPnl.getStyleClass().add("border_down");

        titleLbl.setMinSize(500d, 35d);
        titleLbl.setMaxSize(500d, 35d);
        titleLbl.setAlignment(Pos.CENTER);

        piecesPnl.setMinSize(500d, 55d);
        piecesPnl.setMaxSize(500d, 55d);

        Label finishedPiecesLbl = new Label("Finished pieces:");
        finishedPiecesLbl.setMinSize(90d, 55d);
        finishedPiecesLbl.setMaxSize(90d, 55d);
        finishedPiecesLbl.setAlignment(Pos.CENTER_RIGHT);

        finishedPiecesPnl.setSpacing(5d);
        finishedPiecesPnl.setMinHeight(55d);
        finishedPiecesPnl.setMaxHeight(55d);
        finishedPiecesPnl.setPadding(new Insets(5d, 5d, 10d, 5d));

        Label readyToGoPiecesLbl = new Label("Pieces to go:");
        readyToGoPiecesLbl.setMinSize(80d, 55d);
        readyToGoPiecesLbl.setMaxSize(410d, 55d);
        HBox.setHgrow(readyToGoPiecesLbl, Priority.ALWAYS);
        readyToGoPiecesLbl.setAlignment(Pos.CENTER_RIGHT);

        readyToGoPiecesPnl.setSpacing(5d);
        readyToGoPiecesPnl.setMinHeight(55d);
        readyToGoPiecesPnl.setMaxHeight(55d);
        readyToGoPiecesPnl.setPadding(new Insets(5d, 5d, 10d, 5d));

        piecesPnl.getChildren().addAll(finishedPiecesLbl, finishedPiecesPnl, readyToGoPiecesLbl, readyToGoPiecesPnl);
    }

    public List<Piece> createPieces(Player player) {
        List<Piece> pieces = new ArrayList<>(PIECES_COUNT);
        for (int i = 0; i < PIECES_COUNT; i++) {
            Piece piece = new Piece(player);
            pieces.add(piece);
            addPieceToReadyToGoBox(piece);
        }
        return pieces;
    }

    private Node createGameBoard() {
        playPnl.setMinSize(500d, 200d);
        playPnl.setMaxSize(500d, 200d);
        GridPane.setConstraints(playPnl, 0, 1);

        gameBoardPnl.setMinSize(500d, 200d);
        gameBoardPnl.setMaxSize(500d, 200d);
        gameBoardPnl.getStyleClass().addAll("board", "border_down");
        gameBoardPnl.setPadding(new Insets(10d));

        playPnl.getChildren().add(gameBoardPnl);

        return playPnl;
    }

    private Node createDicePanel() {
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

        ClassLoader classLoader = BoardController.class.getClassLoader();
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

    private Node createMessagePanel() {
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

    public void setHumanPiecesMouseClickAction(EventHandler<? super MouseEvent> eventHandler) {
        for (Node node : humanPlayerReadyToGoPiecesPnl.getChildren()) {
            ((Piece) node).setOnMouseClicked(eventHandler);
        }
    }

    public void setRollDiceButtonAction(EventHandler<ActionEvent> eventEventHandler) {
        rollDiceBtn.setOnAction(eventEventHandler);
    }

    /*
     * Creating transitions and animations
     */
    private void initDiceRollingAnimation() {
        final KeyValue[] keyValues = new KeyValue[3];
        ClassLoader classLoader = BoardController.class.getClassLoader();
        for (int i = 0; i < keyValues.length; i++) {
            keyValues[i] = new KeyValue(diceImg.imageProperty(),
                    new Image(classLoader.getResource("img/dice" + i + ".png").toExternalForm()));
        }

        double step = DICE_ROLLING_ANIMATION_STEP;
        int currentDiceFrame = 0;
        for (double d = step; d < DICE_ROLLING_ANIMATION_DURATION; d += (step *= DICE_ROLLING_ANIMATION_STEP_MODIFIER)) {
            diceRollAnimation.getKeyFrames().add(new KeyFrame(
                    Duration.millis(d),
                    keyValues[currentDiceFrame++ % 3]
            ));
        }
    }

    private void initMessageFadeOutTransition() {
        messageFadeOutTransition.setNode(messageTxt);
        messageFadeOutTransition.setDuration(Duration.seconds(5));
        messageFadeOutTransition.setFromValue(1d);
        messageFadeOutTransition.setByValue(1d);
        messageFadeOutTransition.setToValue(0d);
    }

    public FadeTransition createPieceFadeOutTransition(Piece piece) {
        FadeTransition transition = new FadeTransition(Duration.millis(FADE_TIME), piece);
        transition.setFromValue(1d);
        transition.setToValue(0d);
        return transition;
    }

    public FadeTransition createPieceFadeInTransition(Piece piece) {
        FadeTransition transition = new FadeTransition(Duration.millis(FADE_TIME), piece);
        transition.setFromValue(0d);
        transition.setToValue(1d);
        return transition;
    }

    public PathTransition createPathTransitionForMove(Piece piece, int howFar, List<Field> route) {
        PathTransition transition = new PathTransition();
        transition.setDuration(Duration.millis(howFar * 500));
        transition.setPath(createPath(piece.getFieldNo(), howFar, route));
        transition.setNode(piece);
        return transition;
    }

    private Shape createPath(int from, int howFar, List<Field> route) {
        Field field = route.get(1);
        int xModifier = field.getColumn();
        int yModifier = field.getRow();

        Polyline path = new Polyline();
        int to = Math.min(from + howFar, route.size() - 1);
        for (int i = from; i <= to; i++) {
            field = route.get(i);
            path.getPoints().addAll(
                    (field.getColumn() - xModifier) * 60d + 20d,
                    (field.getRow() - yModifier) * 60d + 20d
            );
        }

        return path;
    }

    /*
    * Access to pieces containers
     */
    private HBox getFinishedPiecesBox(PieceColor color) {
        return color == PieceColor.DARK ? humanPlayerFinishedPiecesPnl : computerPlayerFinishedPiecesPnl;
    }

    private HBox getReadyToGoPiecesBox(PieceColor color) {
        return color == PieceColor.DARK ? humanPlayerReadyToGoPiecesPnl : computerPlayerReadyToGoPiecesPnl;
    }

    /*
    * Messages
     */
    private void showDiceRollResult(int diceRollResult, Runnable afterDiceRollAction) {
        diceRollResultTxt.setText(Integer.toString(diceRollResult));
        if (diceRollResult > 0) {
            showMessage("Pick the piece to move.");
            afterDiceRollAction.run();
        } else {
            showMessage("Player looses his move due to dice roll result equals to 0.", event -> afterDiceRollAction.run());
        }
    }

    public void showMessage(String message) {
        messageTxt.setOpacity(1d);
        messageTxt.setText(message);
    }

    public void showMessage(String message, boolean fadeOut) {
        showMessage(message);
        if (fadeOut) {
            messageFadeOutTransition.setOnFinished(null);
            messageFadeOutTransition.play();
        }
    }

    public void showMessage(String message, EventHandler<ActionEvent> fadeOutOnFinishAction) {
        showMessage(message);
        messageFadeOutTransition.setOnFinished(fadeOutOnFinishAction);
        messageFadeOutTransition.play();
    }

    public void clearMessage() {
        showMessage("");
    }

    public void setTurnInfo(String info) {
        turnInfoTxt.setText(info);
    }

    public void showGameOverInfo(String winner) {
        VBox gameOverPnl = new VBox();

        Label gameOverLbl = new Label("GAME OVER!");
        gameOverLbl.setMinSize(500d, 70d);
        gameOverLbl.setMaxSize(500d, 70d);
        gameOverLbl.getStyleClass().add("game_over");
        gameOverLbl.setAlignment(Pos.CENTER);

        Label winnerLbl = new Label(winner + "\nis the winner!");
        winnerLbl.setMinSize(500d, 130d);
        winnerLbl.setMaxSize(500d, 130d);
        winnerLbl.getStyleClass().add("game_over");
        winnerLbl.setAlignment(Pos.CENTER);
        winnerLbl.setTextAlignment(TextAlignment.CENTER);

        gameOverPnl.getChildren().addAll(gameOverLbl, winnerLbl);
        FadeTransition transition = new FadeTransition(Duration.millis(750), gameOverPnl);
        transition.setFromValue(1d);
        transition.setToValue(0.7);
        transition.setAutoReverse(true);
        transition.setCycleCount(Transition.INDEFINITE);
        transition.play();

        gameBoardPnl.setOpacity(0.4);
        turnInfoTxt.setText("");
        playPnl.getChildren().add(gameOverPnl);
    }

    /*
    * Others
     */
    public void disableRollDiceButton() {
        rollDiceBtn.setDisable(true);
    }

    public void enableRollDiceButton() {
        rollDiceBtn.setDisable(false);
    }

    private void addPieceToReadyToGoBox(Piece piece) {
        getReadyToGoPiecesBox(piece.getColor()).getChildren().add(piece);
    }

    public void animateDice(int diceRollResult, Runnable afterDiceRollAction) {
        disableRollDiceButton();
        showMessage("The dice is rolling...");
        diceRollResultTxt.setText("");
        diceRollAnimation.setOnFinished(event -> showDiceRollResult(diceRollResult, afterDiceRollAction));
        diceRollAnimation.play();
    }

    public void putPieceOnBoard(Piece piece, Field startField) {
        getReadyToGoPiecesBox(piece.getColor()).getChildren().remove(piece);
        HBox.clearConstraints(piece);
        AnchorPane.setLeftAnchor(piece, startField.getColumn() * 60d + 10d);
        AnchorPane.setTopAnchor(piece, startField.getRow() * 60d + 10d);
        gameBoardPnl.getChildren().add(piece);
    }

    public void putPieceBackToReadyToGoBox(Piece piece) {
        gameBoardPnl.getChildren().remove(piece);
        piece.setTranslateX(0d);
        piece.setTranslateY(0d);
        getReadyToGoPiecesBox(piece.getColor()).getChildren().add(piece);
    }

    public void putPieceInFinishedPiecesBox(Piece piece) {
        gameBoardPnl.getChildren().remove(piece);
        piece.setTranslateX(0d);
        piece.setTranslateY(0d);
        getFinishedPiecesBox(piece.getColor()).getChildren().add(piece);
    }

}
