package com.kodilla.royal.game.of.ur;

import javafx.event.ActionEvent;
import javafx.scene.input.MouseEvent;

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

        boardController = new BoardController();

        currentPlayer = humanPlayer = new HumanPlayer(this, Route.SIMPLE_ROUTE, fields);
        canHumanPickPiece = false;
        computerPlayer = new ComputerPlayer(this, Route.SIMPLE_ROUTE, fields);

        dice = new Dice(1);

        boardController.setHumanPiecesMouseClickAction(this::handlePieceMouseClicked);
        boardController.setRollDiceButtonAction(this::handleRollDiceButtonClick);

        // just testing...
        humanPlayer.setPieceAtField(0, 14);
//        computerPlayer.setPieceAtField(0, 14);
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
                    boardController.clearMessage();
                    humanPlayer.movePiece(piece, diceRollResult);
                } else {
                    boardController.showMessage("Chosen piece can not move - pick another one.");
                }
            }
        }
    }

    private void handleRollDiceButtonClick(ActionEvent event) {
        diceRollResult = dice.roll();
        boardController.animateDice(diceRollResult, this::humanPlayerMoves);
    }

    public void humanPlayerMoves() {
        if (diceRollResult > 0) {
            if (humanPlayer.hasPiecesToMove(diceRollResult)) {
                canHumanPickPiece = true;
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
        if (currentPlayer == humanPlayer) {
            currentPlayer = computerPlayer;
            diceRollResult = dice.roll();
            boardController.animateDice(diceRollResult, this::computerPlayerMoves);
        } else {
            currentPlayer = humanPlayer;
            boardController.enableRollDiceButton();
            boardController.showMessage("Roll the dice.");
        }
        boardController.setTurnInfo(currentPlayer + "'s turn");
    }

    public void finishGame(Player winner) {
        boardController.showGameOverInfo(currentPlayer.toString());
    }

}
