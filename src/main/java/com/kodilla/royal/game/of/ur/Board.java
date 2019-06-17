package com.kodilla.royal.game.of.ur;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class Board extends GridPane {

    public final static int HUMAN_PLAYER = 1;
    public final static int COMPUTER_PLAYER = 2;

    private final static int ROW_COUNT = 3;
    private final static int COLUMN_COUNT = 8;

    private int diceRollResult;
    private Player currentPlayer;
    private final Dice dice;

    private final HumanPlayer humanPlayer;
    private boolean canHumanPickPiece;
    private final ComputerPlayer computerPlayer;

    private final ImageView diceImg;
    private final Timeline diceRollingAnimation;
    private final Text diceRollResultTxt;
    private final Text turnInfoTxt;
    private final Text messageTxt;
    private final FadeTransition messageFadeTransition;
    private final Button rollDiceBtn;
    private final AnchorPane gameBoardPnl;

    public Board() throws Exception {
        Field[][] fields = new Field[ROW_COUNT][COLUMN_COUNT];
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COLUMN_COUNT; col++) {
                if (row % 2 == 1 || col != 3) {
                    fields[row][col] = new Field(row, col, !(row % 2 == 0 || col == 4));
                }
            }
        }

        currentPlayer = humanPlayer = new HumanPlayer(
                PieceColor.DARK,
                Route.getRoute(HUMAN_PLAYER, Route.SIMPLE_ROUTE, fields),
                this
        );
        canHumanPickPiece = false;
        computerPlayer = new ComputerPlayer(
                PieceColor.LIGHT,
                Route.getRoute(COMPUTER_PLAYER, Route.SIMPLE_ROUTE, fields),
                this
        );

        diceImg = new ImageView();
        diceRollingAnimation = BoardBuilder.createDiceRollingAnimation(diceImg);
        diceRollResultTxt = new Text();
        rollDiceBtn = new Button();
        turnInfoTxt = new Text();
        messageTxt = new Text();
        messageFadeTransition = BoardBuilder.createMessageFadeTransition(messageTxt);
        gameBoardPnl = new AnchorPane();

        buildBoard();

        dice = new Dice(diceRollResultTxt);

        diceRollingAnimation.setOnFinished(event -> showDiceRollResult());
        rollDiceBtn.setOnAction(event -> animateDice());
        humanPlayer.setMouseClickedListener(this::handlePieceMouseClicked);
    }

    private void buildBoard() {
        setMinSize(670d, 420d);
        setMaxSize(670d, 420d);
        getStyleClass().add("root");

        ClassLoader classLoader = getClass().getClassLoader();
        getStylesheets().add(classLoader.getResource("css/board.css").toExternalForm());

        add(BoardBuilder.createTopBar(), 0, 0, 2, 1);
        add(BoardBuilder.createHumanPlayerPanel(humanPlayer), 0, 1);
        add(BoardBuilder.createGameBoard(gameBoardPnl), 0, 2);
        add(BoardBuilder.createComputerPlayerPanel(computerPlayer), 0, 3);
        add(BoardBuilder.createDicePanel(diceImg, diceRollResultTxt, rollDiceBtn), 1, 1, 1, 2);
        add(BoardBuilder.createMessagePanel(turnInfoTxt, messageTxt), 1, 3);
    }

    public AnchorPane getGameBoardPnl() {
        return gameBoardPnl;
    }

    private void animateDice() {
        rollDiceBtn.setDisable(true);
        messageTxt.setText("The dice is rolling...");
        diceRollResultTxt.setText("");
        diceRollingAnimation.play();
    }

    private void showDiceRollResult() {
        diceRollResult = dice.roll();
        if (diceRollResult > 0 && currentPlayer.hasPiecesToMove(diceRollResult)) {
            if (currentPlayer == humanPlayer) {
                messageTxt.setText("Pick the piece to move.");
                canHumanPickPiece = true;
            } else {
                messageTxt.setText("Computer player picks the piece to move.");
                Piece piece = computerPlayer.chooseBestMove(diceRollResult);
                computerPlayer.movePiece(piece, diceRollResult);
            }
        } else {
            String message;
            if (diceRollResult == 0) {
                message = currentPlayer + " looses his move because of dice roll result equal to 0.";
            } else {
                message = currentPlayer + " doesn't have a piece to move.";
            }
            messageTxt.setText(message);
            messageFadeTransition.setOnFinished(event -> nextTurn());
            messageFadeTransition.play();
        }
    }

    private void handlePieceMouseClicked(MouseEvent event) {
        if (canHumanPickPiece) {
            Piece piece = (Piece) event.getSource();
            if (!piece.isInGame()) {
                messageTxt.setText("Chosen piece finished it's route - pick another piece.");
            } else {
                if (piece.canMove(diceRollResult, humanPlayer.getRoute())) {
                    canHumanPickPiece = false;
                    humanPlayer.movePiece(piece, diceRollResult);
                } else {
                    messageTxt.setText("Chosen piece can not move - pick another one.");
                }
            }
        }
    }

    public void nextTurn() {
        messageTxt.setText("");
        messageTxt.setOpacity(1d);
        if (currentPlayer == humanPlayer) {
            currentPlayer = computerPlayer;
            animateDice();
        } else {
            currentPlayer = humanPlayer;
            rollDiceBtn.setDisable(false);
            messageTxt.setText("Roll the dice.");
        }
        turnInfoTxt.setText(currentPlayer + "'s turn");

    }

    public void finishGame(Player winner) {
        // to implement
    }
}
