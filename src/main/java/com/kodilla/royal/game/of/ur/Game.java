package com.kodilla.royal.game.of.ur;

import javafx.scene.input.MouseEvent;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.kodilla.royal.game.of.ur.BoardController.*;
import static com.kodilla.royal.game.of.ur.InstructionsWindow.TAGS;

public class Game {

    public final static int PIECES_COUNT = 7;

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

        dice = new Dice();
//        dice = new Dice(4);

        boardController.setNewGameButtonAction(event -> {
            String message = "Do you really want to start a new game?\nYou will loose your current game progress.";
            if (boardController.showConfirmationDialog(message)) {
                resetGame();
            }
        });
        boardController.setInstructionsButtonAction(event -> {
            List<String> rawInstructions = retrieveGameInstructions();
            List<List<String>> instructions = prepareGameInstructions(rawInstructions);
            showInstructions(instructions);
        });

        boardController.setHumanPiecesMouseClickAction(this::handlePieceMouseClicked);
        boardController.setHumanPiecesMouseEnterAction(this::handlePieceMouseEntered);
        boardController.setHumanPiecesMouseExitAction(this::handlePieceMouseExited);
        boardController.setRollDiceButtonAction(event -> handleRollDiceButtonClick());

        boardController.setTurnIndicator(humanPlayer, GREEN_LIGHT);
        boardController.setTurnIndicator(computerPlayer, RED_LIGHT);

//        capture test
//        humanPlayer.setPieceAtField(0, 4);
//        computerPlayer.setPieceAtField(0, 7);

//        finish game test
//        humanPlayer.setPieceAtField(0, 12);
//        computerPlayer.setPieceAtField(0, 12);

//        cant move test
//        humanPlayer.setPieceAtField(0, 5);
//        humanPlayer.setPieceAtField(1, 8);

//        no moves available
//        humanPlayer.setPieceAtField(0, 5);
//        computerPlayer.setPieceAtField(0, 8);

//        Field field = fields[0][5];
//        boardController.highlightField(field, RED_HIGHLIGHT);
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
                    boardController.showMessage(
                            "Chosen piece can not move - pick another one.",
                            true
                    );
                }
                boardController.removeHighlightFromField();
            }
        }
    }

    private void handlePieceMouseEntered(MouseEvent event) {
        if (canHumanPickPiece) {
            Piece piece = (Piece) event.getSource();
            if (piece.isInGame()) {
                if (piece.getFieldNo() + diceRollResult < humanPlayer.getRoute().size()) {
                    Field field = humanPlayer.getRoute().get(piece.getFieldNo() + diceRollResult);
                    String color = field.canTakePiece(piece) ? GREEN_HIGHLIGHT : RED_HIGHLIGHT;
                    boardController.highlightField(field, color);
                } else if (piece.getFieldNo() + diceRollResult == humanPlayer.getRoute().size()) {
                    boardController.highlightMeta(GREEN_HIGHLIGHT);
                } else {
                    boardController.highlightMeta(RED_HIGHLIGHT);
                }
            }
        }
    }

    private void handlePieceMouseExited(MouseEvent event) {
        if (canHumanPickPiece) {
            Piece piece = (Piece) event.getSource();
            if (piece.isInGame()) {
                if (piece.getFieldNo() + diceRollResult < humanPlayer.getRoute().size()) {
                    boardController.removeHighlightFromField();
                } else {
                    boardController.removeHighlightFromMeta();
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
            boardController.enableButtons();
            boardController.showMessage("Roll the dice.");
            boardController.setTurnIndicator(currentPlayer, GREEN_LIGHT);
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

    private void resetGame() {
        resetPlayerPieces(humanPlayer);
        resetPlayerPieces(computerPlayer);
        boardController.clearDiceRollResult();
        currentPlayer = computerPlayer;
        nextTurn();
    }

    private List<String> retrieveGameInstructions() {
        ClassLoader classLoader = getClass().getClassLoader();
        URL instructionsFileUrl = Objects.requireNonNull(
                classLoader.getResource("instructions.xml"),
                "Accessing the game instructions file unsuccessful."
        );
        File file = new File(instructionsFileUrl.getFile());
        Path path;
        List<String> rawInstructions = null;
        try {
            path = Paths.get(URLDecoder.decode(file.getPath(), "UTF-8"));
            Stream<String> fileLines = Files.lines(path);
            rawInstructions = fileLines.collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        return rawInstructions;
    }

    private List<List<String>> prepareGameInstructions(List<String> rawInstructions) {
        List<List<String>> instructions = new LinkedList<>();
        String pattern = "^<(" + String.join("|", TAGS) + ")>(.*)</\\1>$";
        Pattern instructionPattern = Pattern.compile(pattern);
        for (String rawInstruction : rawInstructions) {
            Matcher matcher = instructionPattern.matcher(rawInstruction);
            if (matcher.matches()) {
                List<String> instruction = new ArrayList<>(2);
                instruction.add(0, matcher.group(1));
                instruction.add(1, matcher.group(2));
                instructions.add(instruction);
            }
        }
        return instructions;
    }

    private void showInstructions(List<List<String>> instructions) {
        InstructionsWindow instructionsWindow = new InstructionsWindow();
        instructionsWindow.setInstructions(instructions);
        instructionsWindow.show();
    }

    public void finishGame() {
        boardController.showGameOverInfo(currentPlayer.toString());
    }

}
