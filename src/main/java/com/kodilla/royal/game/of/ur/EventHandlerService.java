package com.kodilla.royal.game.of.ur;

import javafx.scene.input.MouseEvent;

import static com.kodilla.royal.game.of.ur.BoardController.*;

public class EventHandlerService {

    private final Game game;
    private final BoardController boardController;
    private final GameState gameState;
    private final Player humanPlayer;

    public EventHandlerService(final Game game) {
        this.game = game;
        this.boardController = game.getBoardController();
        this.gameState = game.getGameState();
        this.humanPlayer = game.getHumanPlayer();
    }

    public void handlePieceMouseClicked(MouseEvent event) {
        if (gameState.canHumanPickPiece()) {
            Piece piece = (Piece) event.getSource();
            if (!piece.isInGame()) {
                boardController.showMessage(
                        "Chosen piece finished it's route - pick another piece.",
                        true
                );
            } else {
                if (piece.canMove(gameState.getDiceRollResult(), humanPlayer.getRoute())) {
                    gameState.disallowHumanPickPiece();
                    boardController.setTurnIndicator(humanPlayer, YELLOW_LIGHT);
                    boardController.clearMessage();
                    humanPlayer.movePiece(piece, gameState.getDiceRollResult());
                } else {
                    boardController.showMessage(
                            "Chosen piece can not move - pick another one.",
                            true
                    );
                }
                boardController.removeHighlightFromField();
                boardController.removeHighlightFromMeta();
            }
        }
    }

    public void handlePieceMouseEntered(MouseEvent event) {
        if (gameState.canHumanPickPiece()) {
            Piece piece = (Piece) event.getSource();
            if (piece.isInGame()) {
                if (piece.getFieldNo() + gameState.getDiceRollResult() < humanPlayer.getRoute().size()) {
                    Field field = humanPlayer.getRoute().get(piece.getFieldNo() + gameState.getDiceRollResult());
                    String color = field.canTakePiece(piece) ? GREEN_HIGHLIGHT : RED_HIGHLIGHT;
                    boardController.highlightField(field, color);
                } else if (piece.getFieldNo() + gameState.getDiceRollResult() == humanPlayer.getRoute().size()) {
                    boardController.highlightMeta(GREEN_HIGHLIGHT);
                } else {
                    boardController.highlightMeta(RED_HIGHLIGHT);
                }
            }
        }
    }

    public void handlePieceMouseExited(MouseEvent event) {
        if (gameState.canHumanPickPiece()) {
            Piece piece = (Piece) event.getSource();
            if (piece.isInGame()) {
                if (piece.getFieldNo() + gameState.getDiceRollResult() < humanPlayer.getRoute().size()) {
                    boardController.removeHighlightFromField();
                } else {
                    boardController.removeHighlightFromMeta();
                }
            }
        }
    }

    public void handleRollDiceButtonClick() {
        gameState.setDiceRollResult(game.getDice().roll());
        boardController.setTurnIndicator(humanPlayer, YELLOW_LIGHT);
        boardController.animateDice(gameState.getDiceRollResult(), game::humanPlayerMoves);
    }

    public void handleInstructionsButtonAction() {
        InstructionsService instructionsService = new InstructionsService();
        instructionsService.showInstructions();
    }

    public void handleNewGameButtonAction() {
        String message = "Do you really want to start a new game?\nYou will loose your current game progress.";
        if (boardController.showConfirmationDialog(message)) {
            boardController.hideGameOverInfo();
            game.resetGame();
        }
    }
}
