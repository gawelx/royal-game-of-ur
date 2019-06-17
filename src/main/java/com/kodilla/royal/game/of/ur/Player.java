package com.kodilla.royal.game.of.ur;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {

    private static final int PIECES_COUNT = 7;

    private final int playerNo;
    private final PieceColor color;
    protected final List<Piece> pieces;
    private final List<Field> route;
    private final Board gameBoard;

    private HBox finishedPiecesBox;
    private HBox piecesToGoBox;

    public Player(final int playerNo, final PieceColor color, final List<Field> route, Board gameBoard) {
        this.playerNo = playerNo;
        this.color = color;
        this.route = route;
        this.pieces = new ArrayList<>(PIECES_COUNT);
        this.gameBoard = gameBoard;
    }

    public int getPlayerNo() {
        return playerNo;
    }

    public PieceColor getColor() {
        return color;
    }

    public List<Field> getRoute() {
        return new ArrayList<>(route);
    }

    public AnchorPane getGameBoardPane() {
        return gameBoard.getGameBoardPnl();
    }

    public boolean hasPiecesInGame() {
        for (Piece piece : pieces) {
            if (piece.isInGame()) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPiecesToMove(int howFar) {
        for (Piece piece : pieces) {
            if (piece.canMove(howFar, route)) {
                return true;
            }
        }
        return false;
    }

    public void setPiecesBoxes(HBox finishedPiecesBox, HBox piecesToGoBox) {
        this.finishedPiecesBox = finishedPiecesBox;
        this.piecesToGoBox = piecesToGoBox;
        createPieces();
    }

    public void putPieceAtStart(Piece piece) {
        piecesToGoBox.getChildren().add(piece);
    }

    private void createPieces() {
        for (int i = 0; i < PIECES_COUNT; i++) {
            Piece piece = new Piece(this);
            pieces.add(piece);
            piecesToGoBox.getChildren().add(piece);
        }
    }

    public void movePiece(Piece piece, int howFar) {
        if (piece.getFieldNo() == 0) {
            fadeOutPiece(piece, howFar);
        } else {
            route.get(piece.getFieldNo()).setPiece(null);
            movePieceOnBoard(piece, howFar);
        }
    }

    private void fadeOutPiece(Piece piece, int howFar) {
        FadeTransition transition = new FadeTransition(Duration.millis(250), piece);
        transition.setFromValue(1d);
        transition.setToValue(0d);
        if (howFar > 0) {
            transition.setOnFinished(event -> putPieceOnBoardAndFadeIn(piece, howFar));
        } else {
            transition.setOnFinished(event -> returnCapturedPieceToStartAndFadeIn(piece));
        }
        transition.play();
    }

    private void putPieceOnBoardAndFadeIn(Piece piece, int howFar) {
        piecesToGoBox.getChildren().remove(piece);

        Field field = route.get(1);
        AnchorPane.setLeftAnchor(piece, field.getColumn() * 60d + 10d);
        AnchorPane.setTopAnchor(piece, field.getRow() * 60d + 10d);
        getGameBoardPane().getChildren().add(piece);

        piece.increaseFieldNo(1);

        FadeTransition transition = new FadeTransition(Duration.millis(250), piece);
        transition.setFromValue(0d);
        transition.setToValue(1d);
        transition.setOnFinished(event -> movePieceOnBoard(piece, howFar - 1));
        transition.play();
    }

    private void returnCapturedPieceToStartAndFadeIn(Piece piece) {
        getGameBoardPane().getChildren().remove(piece);
        piece.getBackToStart();

        FadeTransition transition = new FadeTransition(Duration.millis(250), piece);
        transition.setFromValue(0d);
        transition.setToValue(1d);
        transition.play();
    }

    private void movePieceOnBoard(Piece piece, int howFar) {
        if (howFar > 0) {
            // howFar can be zero in one case -  when the dice roll result is 1 and player starts new piece
            PathTransition transition = new PathTransition();
            if (piece.getFieldNo() + howFar == route.size() - 1) {
                // when piece finishes route
                howFar--;
                transition.setOnFinished(event -> finishRouteFadeOutPiece(piece));
            } else {
                Piece oldPiece = route.get(piece.getFieldNo() + howFar).setPiece(piece);
                if (oldPiece != null) {
                    oldPiece.returnToStart();
                    fadeOutPiece(oldPiece, -1);
                }
                transition.setOnFinished(event -> gameBoard.nextTurn());
            }
            transition.setDuration(Duration.millis(howFar * 500));
            transition.setPath(createPath(piece.getFieldNo(), howFar));
            transition.setNode(piece);
            transition.play();
            piece.increaseFieldNo(howFar);
        } else {
            route.get(piece.getFieldNo()).setPiece(piece);
            gameBoard.nextTurn();
        }
    }

    private Shape createPath(int from, int howFar) {
        Field field = route.get(1);
        int xModifier = field.getColumn();
        int yModifier = field.getRow();

        Polyline path = new Polyline();
        for (int i = from; i <= from + howFar; i++) {
            field = route.get(i);
            path.getPoints().addAll(
                    (field.getColumn() - xModifier) * 60d + 20d,
                    (field.getRow() - yModifier) * 60d + 20d
            );
        }

        return path;
    }

    private void finishRouteFadeOutPiece(Piece piece) {
        FadeTransition transition = new FadeTransition(Duration.millis(500), piece);
        transition.setFromValue(1d);
        transition.setToValue(0d);
        transition.setOnFinished(event -> movePieceToFinishedAndFadeIn(piece));
        transition.play();
    }

    private void movePieceToFinishedAndFadeIn(Piece piece) {
        piece.finishRoute();
        getGameBoardPane().getChildren().remove(piece);
        finishedPiecesBox.getChildren().add(piece);

        FadeTransition transition = new FadeTransition(Duration.millis(500), piece);
        transition.setFromValue(0d);
        transition.setToValue(1d);
        if (!hasPiecesInGame()) {
            transition.setOnFinished(event -> gameBoard.finishGame(this));
        } else {
            transition.setOnFinished(event -> gameBoard.nextTurn());
        }
        transition.play();
    }

}
