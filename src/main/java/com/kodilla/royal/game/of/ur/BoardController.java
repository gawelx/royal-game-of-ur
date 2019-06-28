package com.kodilla.royal.game.of.ur;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.Bloom;
import javafx.scene.effect.BoxBlur;
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
import java.util.Objects;
import java.util.Optional;

import static com.kodilla.royal.game.of.ur.Game.PIECES_COUNT;

public class BoardController extends GridPane {

    public static final String GREEN_LIGHT = "green";
    public static final String RED_LIGHT = "red";
    public static final String YELLOW_LIGHT = "yellow";

    public static final String GREEN_HIGHLIGHT = "green";
    public static final String RED_HIGHLIGHT = "red";

    private static final double DICE_ROLLING_ANIMATION_DURATION = 4000d;
    private static final double DICE_ROLLING_ANIMATION_STEP = 100d;
    private static final double DICE_ROLLING_ANIMATION_STEP_MODIFIER = 1.1d;
    private static final int FADE_TIME = 500;

//    private final Game game;

    private final Button newGameBtn;
    private final Button instructionsBtn;

    private final ImageView diceImg;
    private final Text diceRollResultTxt;
    private final Text messageTxt;
    private final Button rollDiceBtn;

    private final StackPane playPnl;
    private final AnchorPane gameBoardPnl;

    private final HBox humanPlayerFinishedPiecesPnl;
    private final HBox humanPlayerReadyToGoPiecesPnl;
    private final ImageView humanPlayerTurnIndicatorImg;

    private final HBox computerPlayerFinishedPiecesPnl;
    private final HBox computerPlayerReadyToGoPiecesPnl;

    private final Timeline diceRollAnimation;
    private final FadeTransition messageFadeOutTransition;
    private final ImageView computerPlayerTurnIndicatorImg;

    private final ImageView highlightField;
    private Image greenFieldHighlight;
    private Image redFieldHighlight;

    private final ImageView highlightMeta;
    private Image greenMetaHighlight;
    private Image redMetaHighlight;

    public BoardController(final Game game) {
//        this.game = game;

        this.newGameBtn = new Button();
        this.instructionsBtn = new Button();

        this.humanPlayerFinishedPiecesPnl = new HBox();
        this.humanPlayerReadyToGoPiecesPnl = new HBox();
        this.humanPlayerTurnIndicatorImg = new ImageView();

        this.playPnl = new StackPane();
        this.gameBoardPnl = new AnchorPane();

        this.computerPlayerFinishedPiecesPnl = new HBox();
        this.computerPlayerReadyToGoPiecesPnl = new HBox();
        this.computerPlayerTurnIndicatorImg = new ImageView();

        this.diceImg = new ImageView();
        this.diceRollResultTxt = new Text();
        this.rollDiceBtn = new Button();
        this.diceRollAnimation = new Timeline();

        this.messageTxt = new Text();
        this.messageFadeOutTransition = new FadeTransition();

        this.highlightField = new ImageView();
        this.highlightMeta = new ImageView();

        setMinSize(670d, 420d);
        setMaxSize(670d, 420d);
        getStyleClass().add("root");

        ClassLoader classLoader = getClass().getClassLoader();
        getStylesheets().add(Objects.requireNonNull(
                classLoader.getResource("css/board.css"),
                "Accessing the CSS file unsuccessful."
        ).toExternalForm());

        add(createTopBar(), 0, 0, 2, 1);
        add(createHumanPlayerPanel(), 0, 1);
        add(createGameBoard(), 0, 2);
        add(createComputerPlayerPanel(), 0, 3);
        add(createDicePanel(), 1, 1, 1, 2);
        add(createMessagePanel(), 1, 3);

        initDiceRollingAnimation();
        initMessageFadeOutTransition();
        initHighlights();
    }

    /*
    * Initializing the UI
     */
    private Node createTopBar() {
        HBox topBarPnl = new HBox(1);
        topBarPnl.setMinSize(670d, 40d);
        topBarPnl.setMaxSize(670d, 40d);
        topBarPnl.setPadding(new Insets(1));

        newGameBtn.setText("N");
        newGameBtn.setMinSize(38d, 38d);
        newGameBtn.setMaxSize(38d, 38d);
        newGameBtn.setAlignment(Pos.CENTER);
        newGameBtn.setTooltip(new Tooltip("New game"));

        Label titleLbl = new Label("The Royal Game Of Ur");
        titleLbl.setMinSize(590d, 40d);
        titleLbl.setMaxSize(590d, 40d);
        titleLbl.setAlignment(Pos.CENTER);
        titleLbl.getStyleClass().add("title");

        titleLbl.setAlignment(Pos.CENTER);

        instructionsBtn.setText("?");
        instructionsBtn.setMinSize(38d, 38d);
        instructionsBtn.setMaxSize(38d, 38d);
        instructionsBtn.setAlignment(Pos.CENTER);
        instructionsBtn.setTooltip(new Tooltip("Game instructions"));

        topBarPnl.getChildren().addAll(newGameBtn, titleLbl, instructionsBtn);
        return topBarPnl;
    }

    private Node createHumanPlayerPanel() {
        HBox titlePnl = new HBox();
        HBox piecesPnl = new HBox();

        VBox playerPnl = createPlayerPanel(
                titlePnl,
                piecesPnl,
                "Human player",
                humanPlayerFinishedPiecesPnl,
                humanPlayerReadyToGoPiecesPnl,
                humanPlayerTurnIndicatorImg
        );

        playerPnl.getChildren().addAll(titlePnl, piecesPnl);

        return playerPnl;
    }

    private Node createComputerPlayerPanel() {
        HBox titlePnl = new HBox();
        HBox piecesPnl = new HBox();

        VBox playerPnl = createPlayerPanel(
                titlePnl,
                piecesPnl,
                "Computer Player",
                computerPlayerFinishedPiecesPnl,
                computerPlayerReadyToGoPiecesPnl,
                computerPlayerTurnIndicatorImg
        );

        playerPnl.getChildren().addAll(piecesPnl, titlePnl);

        return playerPnl;
    }

    private VBox createPlayerPanel(
            final HBox titlePnl,
            final HBox piecesPnl,
            final String title,
            final HBox finishedPiecesPnl,
            final HBox readyToGoPiecesPnl,
            final ImageView turnIndicatorImg
    ) {
        VBox playerPnl = new VBox();
        playerPnl.setMinSize(500d, 90d);
        playerPnl.setMaxSize(500d, 90d);

        titlePnl.setMinSize(500d, 35d);
        titlePnl.setMaxSize(500d, 35d);

        Label titleLbl = new Label(title);
        titleLbl.setMinSize(465d, 35d);
        titleLbl.setMaxSize(465d, 35d);
        titleLbl.setAlignment(Pos.CENTER_LEFT);
        titleLbl.getStyleClass().add("header");

        ClassLoader classLoader = getClass().getClassLoader();
        String imgUrl = Objects.requireNonNull(
                classLoader.getResource("img/green_light.png"),
                "Accessing the light file unsuccessful."
        ).toExternalForm();
        turnIndicatorImg.setImage(new Image(imgUrl));

        titlePnl.getChildren().addAll(titleLbl, turnIndicatorImg);

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

        return playerPnl;
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
        gameBoardPnl.getStyleClass().add("board");
        gameBoardPnl.setPadding(new Insets(10d));

        playPnl.getChildren().add(gameBoardPnl);

        return playPnl;
    }

    private Node createDicePanel() {
        VBox dicePnl = new VBox();
        dicePnl.setMinSize(170d, 290d);
        dicePnl.setMaxSize(170d, 290d);
        dicePnl.setAlignment(Pos.TOP_CENTER);

        Label titleLbl = new Label("The Dice");
        titleLbl.setMinSize(170d, 35d);
        titleLbl.setMaxSize(170d, 35d);
        titleLbl.setAlignment(Pos.CENTER_LEFT);
        titleLbl.getStyleClass().add("header");

        VBox diceImagePnl = new VBox();
        diceImagePnl.setMinSize(170d, 100d);
        diceImagePnl.setMaxSize(170d, 100d);
        diceImagePnl.setAlignment(Pos.CENTER);

        ClassLoader classLoader = BoardController.class.getClassLoader();
        diceImg.setImage(new Image(Objects.requireNonNull(
                classLoader.getResource("img/dice0.png"),
                "Accessing the dice image (0) unsuccessful."
        ).toExternalForm()));
        diceImg.getStyleClass().add("dice");
        diceImagePnl.getChildren().add(diceImg);

        VBox diceValuePnl = new VBox();
        diceValuePnl.setMinSize(170d, 100d);
        diceValuePnl.setMaxSize(170d, 100d);
        diceValuePnl.setPadding(new Insets(5d, 0d, 0d, 0d));
        diceValuePnl.setAlignment(Pos.TOP_CENTER);

        diceRollResultTxt.getStyleClass().add("dice_result");
        diceValuePnl.getChildren().add(diceRollResultTxt);

        rollDiceBtn.setText("Roll the dice");
        rollDiceBtn.setMinSize(160d, 40d);
        rollDiceBtn.setMaxSize(160d, 40d);
        rollDiceBtn.setDefaultButton(true);
        rollDiceBtn.setAlignment(Pos.CENTER);

        dicePnl.getChildren().addAll(titleLbl, diceImagePnl, diceValuePnl, rollDiceBtn);
        return dicePnl;
    }

    private Node createMessagePanel() {
        VBox messagePnl = new VBox(5d);
        messagePnl.setMinSize(170d, 90d);
        messagePnl.setMaxSize(170d, 90d);
        messagePnl.setPadding(new Insets(5d));
        messagePnl.setAlignment(Pos.CENTER);

        messageTxt.setText("Roll the dice to start the game.");
        messageTxt.setWrappingWidth(160d);
        messageTxt.setTextAlignment(TextAlignment.CENTER);
        messageTxt.getStyleClass().add("message");

        messagePnl.getChildren().add(messageTxt);
        return messagePnl;
    }

    public void setHumanPiecesMouseClickAction(EventHandler<? super MouseEvent> eventHandler) {
        for (Node node : humanPlayerReadyToGoPiecesPnl.getChildren()) {
            node.setOnMouseClicked(eventHandler);
        }
    }

    public void setHumanPiecesMouseEnterAction(EventHandler<? super MouseEvent> eventHandler) {
        for (Node node : humanPlayerReadyToGoPiecesPnl.getChildren()) {
            node.setOnMouseEntered(eventHandler);
        }
    }

    public void setHumanPiecesMouseExitAction(EventHandler<? super MouseEvent> eventHandler) {
        for (Node node : humanPlayerReadyToGoPiecesPnl.getChildren()) {
            node.setOnMouseExited(eventHandler);
        }
    }

    public void setRollDiceButtonAction(EventHandler<ActionEvent> eventHandler) {
        rollDiceBtn.setOnAction(eventHandler);
    }

    public void setNewGameButtonAction(EventHandler<ActionEvent> eventHandler) {
        newGameBtn.setOnAction(eventHandler);
    }

    public void setInstructionsButtonAction(EventHandler<ActionEvent> eventHandler) {
        instructionsBtn.setOnAction(eventHandler);
    }

    /*
     * Creating transitions and animations
     */
    private void initDiceRollingAnimation() {
        final KeyValue[] keyValues = new KeyValue[3];
        ClassLoader classLoader = BoardController.class.getClassLoader();
        for (int i = 0; i < keyValues.length; i++) {
            String imageUrl = Objects.requireNonNull(
                    classLoader.getResource("img/dice" + i + ".png"),
                    "Accessing the dice image (" + i + ") unsuccessful."
            ).toExternalForm();
            keyValues[i] = new KeyValue(diceImg.imageProperty(), new Image(imageUrl));
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
        messageFadeOutTransition.setDuration(Duration.millis(400));
        messageFadeOutTransition.setFromValue(1d);
        messageFadeOutTransition.setToValue(0.3);
        messageFadeOutTransition.setAutoReverse(true);
        messageFadeOutTransition.setCycleCount(10);
    }

    private void initHighlights() {
        ClassLoader classLoader = getClass().getClassLoader();

        BoxBlur blur = new BoxBlur(3d, 3d, 2);
        Bloom bloom = new Bloom();
        bloom.setInput(blur);
        highlightField.setEffect(bloom);
        highlightMeta.setEffect(bloom);

        AnchorPane.setLeftAnchor(highlightMeta, 120d);
        AnchorPane.setTopAnchor(highlightMeta, 0d);
        highlightMeta.setVisible(false);
        highlightMeta.setOpacity(0.4);
        gameBoardPnl.getChildren().add(highlightMeta);

        String imgUrl = Objects.requireNonNull(
                classLoader.getResource("img/highlight_field_green.png"),
                "Accessing the green highlightField file unsuccessful."
        ).toExternalForm();
        greenFieldHighlight = new Image(imgUrl);

        imgUrl = Objects.requireNonNull(
                classLoader.getResource("img/highlight_field_red.png"),
                "Accessing the red highlightField file unsuccessful."
        ).toExternalForm();
        redFieldHighlight = new Image(imgUrl);

        imgUrl = Objects.requireNonNull(
                classLoader.getResource("img/highlight_meta_green.png"),
                "Accessing the green highlightMeta file unsuccessful."
        ).toExternalForm();
        greenMetaHighlight = new Image(imgUrl);

        imgUrl = Objects.requireNonNull(
                classLoader.getResource("img/highlight_meta_red.png"),
                "Accessing the red highlightMeta file unsuccessful."
        ).toExternalForm();
        redMetaHighlight = new Image(imgUrl);
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
    * Access to player containers
     */
    private HBox getFinishedPiecesBox(PieceColor color) {
        return color == PieceColor.DARK ? humanPlayerFinishedPiecesPnl : computerPlayerFinishedPiecesPnl;
    }

    private HBox getReadyToGoPiecesBox(PieceColor color) {
        return color == PieceColor.DARK ? humanPlayerReadyToGoPiecesPnl : computerPlayerReadyToGoPiecesPnl;
    }

    private ImageView getTurnIndicatorImg(PieceColor color) {
        return color == PieceColor.DARK ? humanPlayerTurnIndicatorImg : computerPlayerTurnIndicatorImg;
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

    public void clearDiceRollResult() {
        diceRollResultTxt.setText("");
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
        playPnl.getChildren().add(gameOverPnl);
    }

    /*
    * Others
     */
    public void setTurnIndicator(Player player, String light) {
        ClassLoader classLoader = getClass().getClassLoader();
        String imgUrl = Objects.requireNonNull(
                classLoader.getResource("img/" + light + "_light.png"),
                "Accessing the light file unsuccessful."
        ).toExternalForm();
        getTurnIndicatorImg(player.getColor()).setImage(new Image(imgUrl));
    }

    public void disableButtons() {
        rollDiceBtn.setDisable(true);
        newGameBtn.setDisable(true);
    }

    public void enableButtons() {
        rollDiceBtn.setDisable(false);
        newGameBtn.setDisable(false);
    }

    private void addPieceToReadyToGoBox(Piece piece) {
        getReadyToGoPiecesBox(piece.getColor()).getChildren().add(piece);
    }

    public void animateDice(int diceRollResult, Runnable afterDiceRollAction) {
        disableButtons();
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

    private void putPieceInReadyToGoBox(Piece piece) {
        piece.setTranslateX(0d);
        piece.setTranslateY(0d);
        getReadyToGoPiecesBox(piece.getColor()).getChildren().add(piece);
    }

    public void movePieceFromBoardToReadyToGoBox(Piece piece) {
        gameBoardPnl.getChildren().remove(piece);
        putPieceInReadyToGoBox(piece);
    }

    private void movePieceFromFinishedToReadyToGoBox(Piece piece) {
        getFinishedPiecesBox(piece.getColor()).getChildren().remove(piece);
        putPieceInReadyToGoBox(piece);
    }

    public void putPieceInFinishedPiecesBox(Piece piece) {
        gameBoardPnl.getChildren().remove(piece);
        piece.setTranslateX(0d);
        piece.setTranslateY(0d);
        getFinishedPiecesBox(piece.getColor()).getChildren().add(piece);
    }

    public void resetPiece(Piece piece) {
        if (piece.getFieldNo() > 0) {
            if (piece.isInGame()) {
                movePieceFromBoardToReadyToGoBox(piece);
            } else {
                movePieceFromFinishedToReadyToGoBox(piece);
            }
        }
    }

    public boolean showConfirmationDialog(String message) {
        Alert confirmationDialog = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> response = confirmationDialog.showAndWait();
        return response.map(buttonType -> buttonType.equals(ButtonType.YES)).orElse(false);
    }

    public void highlightField(Field field, String highlightColor) {
        Image highlightImg = highlightColor.equals(GREEN_HIGHLIGHT) ? greenFieldHighlight : redFieldHighlight;
        highlightField.setImage(highlightImg);
        AnchorPane.setLeftAnchor(highlightField, field.getColumn() * 60d);
        AnchorPane.setTopAnchor(highlightField, field.getRow() * 60d);
        gameBoardPnl.getChildren().add(highlightField);
    }

    public void removeHighlightFromField() {
        gameBoardPnl.getChildren().remove(highlightField);
    }

    public void highlightMeta(String highlightColor) {
        Image highlightImg = highlightColor.equals(GREEN_HIGHLIGHT) ? greenMetaHighlight : redMetaHighlight;
        highlightMeta.setImage(highlightImg);
        highlightMeta.setVisible(true);
    }

    public void removeHighlightFromMeta() {
        highlightMeta.setVisible(false);
    }

}
