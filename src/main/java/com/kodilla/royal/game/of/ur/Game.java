package com.kodilla.royal.game.of.ur;

import javafx.scene.input.MouseEvent;

import static com.kodilla.royal.game.of.ur.BoardController.*;

public class Game {

    public final static int PIECES_COUNT = 1;

    private final static int ROW_COUNT = 3;
    private final static int COLUMN_COUNT = 8;

    private int diceRollResult;
    private Player currentPlayer;
    private final Dice dice;

    private final HumanPlayer humanPlayer;
    private boolean canHumanPickPiece;
    private final ComputerPlayer computerPlayer;
    private final BoardController boardController;

    public Game() throws Exception {
        Field[][] fields = new Field[ROW_COUNT][COLUMN_COUNT];
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COLUMN_COUNT; col++) {
                if (row % 2 == 1 || col < 2 || col > 3) {
                    fields[row][col] = new Field(row, col, !(row % 2 == 0 || col == 4));
                }
            }
        }

        boardController = new BoardController(this);

        currentPlayer = humanPlayer = new HumanPlayer(this, Route.SIMPLE_ROUTE, fields);
        canHumanPickPiece = false;
        computerPlayer = new ComputerPlayer(this, Route.SIMPLE_ROUTE, fields);

//        dice = new Dice();
        dice = new Dice(3);

        boardController.setHumanPiecesMouseClickAction(this::handlePieceMouseClicked);
        boardController.setRollDiceButtonAction(event -> handleRollDiceButtonClick());

        boardController.setTurnIndicator(humanPlayer, GREEN_LIGHT);
        boardController.setTurnIndicator(computerPlayer, RED_LIGHT);

//        capture test
//        humanPlayer.setPieceAtField(0, 4);
//        computerPlayer.setPieceAtField(0, 7);

//        finish game test
//        humanPlayer.setPieceAtField(0, 11);
//        computerPlayer.setPieceAtField(0, 12);

//        cant move test
//        humanPlayer.setPieceAtField(0, 5);
//        humanPlayer.setPieceAtField(1, 8);

//        no moves available
        humanPlayer.setPieceAtField(0, 5);
        computerPlayer.setPieceAtField(0, 8);
    }

    public BoardController getBoardController() {
        return boardController;
    }

    private void handlePieceMouseClicked(MouseEvent event) {
        if (canHumanPickPiece) {
            Piece piece = (Piece) event.getSource();
            if (!piece.isInGame()) {
                boardController.showMessage(
                        "Chosen piece finished it's route - pick another piece.",
                        true
                );
            } else {
                if (piece.canMove(diceRollResult, humanPlayer.getRoute())) {
                    canHumanPickPiece = false;
                    boardController.setTurnIndicator(humanPlayer, YELLOW_LIGHT);
                    boardController.clearMessage();
                    humanPlayer.movePiece(piece, diceRollResult);
                } else {
                    boardController.showMessage("Chosen piece can not move - pick another one.");
                }
            }
        }
    }

    private void handleRollDiceButtonClick() {
        diceRollResult = dice.roll();
        boardController.setTurnIndicator(humanPlayer, YELLOW_LIGHT);
        boardController.animateDice(diceRollResult, this::humanPlayerMoves);
    }

    private void humanPlayerMoves() {
        if (diceRollResult > 0) {
            if (humanPlayer.hasPiecesToMove(diceRollResult)) {
                canHumanPickPiece = true;
                boardController.setTurnIndicator(humanPlayer, GREEN_LIGHT);
            } else {
                showMessageNoPiecesToMove();
            }
        } else {
            nextTurn();
        }
    }

    private void showMessageNoPiecesToMove() {
        boardController.showMessage("You have no pieces to move. You loose your turn.", event -> nextTurn());
    }

    private void computerPlayerMoves() {
        if (diceRollResult > 0) {
            Piece piece = computerPlayer.chooseBestMove(diceRollResult);
            if (piece != null) {
                computerPlayer.movePiece(piece, diceRollResult);
            } else {
                showMessageNoPiecesToMove();
            }
        } else {
            nextTurn();
        }
    }

    public void nextTurn() {
        boardController.setTurnIndicator(currentPlayer, RED_LIGHT);
        if (currentPlayer == humanPlayer) {
            currentPlayer = computerPlayer;
            diceRollResult = dice.roll();
            boardController.setTurnIndicator(computerPlayer, YELLOW_LIGHT);
            boardController.animateDice(diceRollResult, this::computerPlayerMoves);
        } else {
            currentPlayer = humanPlayer;
            boardController.enableRollDiceButton();
            boardController.showMessage("Roll the dice.");
            boardController.setTurnIndicator(currentPlayer, GREEN_LIGHT);
        }
        boardController.setTurnInfo(currentPlayer + "'s turn");
    }

    public void finishGame() {
        boardController.showGameOverInfo(currentPlayer.toString());
    }

}
