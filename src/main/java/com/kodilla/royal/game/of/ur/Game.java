package com.kodilla.royal.game.of.ur;

import java.util.Optional;

import static com.kodilla.royal.game.of.ur.BoardController.*;

public class Game {

    public final static int PIECES_COUNT = 1;

    private final static int ROW_COUNT = 3;
    private final static int COLUMN_COUNT = 8;

    private final Dice dice;
    private final HumanPlayer humanPlayer;
    private final ComputerPlayer computerPlayer;
    private final BoardController boardController;
    private final GameState gameState;

    public Game() throws Exception {
        Field[][] fields = createFields();

        boardController = new BoardController();
        humanPlayer = new HumanPlayer(this, Route.SIMPLE_ROUTE, fields);
        computerPlayer = new ComputerPlayer(this, Route.SIMPLE_ROUTE, fields);
        gameState = new GameState(humanPlayer);
        dice = new Dice();

        EventHandlerService eventHandlerService = new EventHandlerService(this);
        boardController.setNewGameButtonAction(event -> eventHandlerService.handleNewGameButtonAction());
        boardController.setInstructionsButtonAction(event -> eventHandlerService.handleInstructionsButtonAction());
        boardController.setRollDiceButtonAction(event -> eventHandlerService.handleRollDiceButtonClick());
        boardController.setHumanPiecesMouseClickAction(eventHandlerService::handlePieceMouseClicked);
        boardController.setHumanPiecesMouseEnterAction(eventHandlerService::handlePieceMouseEntered);
        boardController.setHumanPiecesMouseExitAction(eventHandlerService::handlePieceMouseExited);

        boardController.setTurnIndicator(humanPlayer, GREEN_LIGHT);
        boardController.setTurnIndicator(computerPlayer, RED_LIGHT);
    }

    private Field[][] createFields() {
        Field[][] fields = new Field[ROW_COUNT][COLUMN_COUNT];
        for (int row = 0; row < ROW_COUNT; row++) {
            for (int col = 0; col < COLUMN_COUNT; col++) {
                boolean createField = row % 2 == 1      // in the middle row all fields are in game
                        || (col < 2 || col > 3);        // in the upper or lower row fields no 2 and 3 are excluded from game
                if (createField) {
                    boolean isCaptureAllowed = row % 2 == 1 && col != 4;    // capturing opponent piece is allowed only in the middle row with field no 4 excluded (field with star on it)
                    fields[row][col] = new Field(row, col, isCaptureAllowed);
                }
            }
        }
        return fields;
    }

    public BoardController getBoardController() {
        return boardController;
    }

    public GameState getGameState() {
        return gameState;
    }

    public HumanPlayer getHumanPlayer() {
        return humanPlayer;
    }

    public Dice getDice() {
        return dice;
    }

    public void humanPlayerMoves() {
        if (gameState.getDiceRollResult() > 0) {
            if (humanPlayer.hasPiecesToMove(gameState.getDiceRollResult())) {
                gameState.allowHumanPickPiece();
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
        if (gameState.getDiceRollResult() > 0) {
            Optional<Piece> piece = computerPlayer.chooseBestMove(gameState.getDiceRollResult());
            if (piece.isPresent()) {
                computerPlayer.movePiece(piece.get(), gameState.getDiceRollResult());
            } else {
                showMessageNoPiecesToMove();
            }
        } else {
            nextTurn();
        }
    }

    public void nextTurn() {
        boardController.setTurnIndicator(gameState.getCurrentPlayer(), RED_LIGHT);
        if (gameState.isCurrentPlayer(humanPlayer)) {
            gameState.setCurrentPlayer(computerPlayer);
            gameState.setDiceRollResult(dice.roll());
            boardController.setTurnIndicator(computerPlayer, YELLOW_LIGHT);
            boardController.animateDice(gameState.getDiceRollResult(), this::computerPlayerMoves);
        } else {
            gameState.setCurrentPlayer(humanPlayer);
            boardController.enableButtons();
            boardController.showMessage("Roll the dice.");
            boardController.setTurnIndicator(humanPlayer, GREEN_LIGHT);
        }
    }

    private void resetPlayerPieces(Player player) {
        for (Piece piece : player.getPieces()) {
            if (piece.isInGame() && piece.getFieldNo() > 0) {
                piece.getField(player.getRoute()).setPiece(null);
            }
            boardController.resetPiece(piece);
            piece.reset();
        }
    }

    public void resetGame() {
        resetPlayerPieces(humanPlayer);
        resetPlayerPieces(computerPlayer);
        boardController.clearDiceRollResult();
        gameState.setCurrentPlayer(computerPlayer);
        nextTurn();
    }

    public void finishGame() {
        boardController.showGameOverInfo(gameState.getCurrentPlayer().toString());
    }

}
