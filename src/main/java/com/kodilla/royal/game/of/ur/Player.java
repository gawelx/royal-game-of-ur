package com.kodilla.royal.game.of.ur;

import javafx.animation.FadeTransition;
import javafx.animation.PathTransition;

import java.util.ArrayList;
import java.util.List;

public abstract class Player {

    private final PieceColor color;
    private final List<Piece> pieces;
    private final List<Field> route;
    private final Game game;

    public Player(
            final PieceColor color,
            final Game game,
            final int routeType,
            final Field[][] fields) throws Exception {
        this.color = color;
        this.route = Route.getRoute(color, routeType, fields);
        this.pieces = game.getBoardController().createPieces(this);
        this.game = game;
    }

    public PieceColor getColor() {
        return color;
    }

    public List<Piece> getPieces() {
        return pieces;
    }

    public List<Field> getRoute() {
        return new ArrayList<>(route);
    }

    private boolean hasPiecesInGame() {
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

    public void movePiece(Piece piece, int howFar) {
        if (piece.getFieldNo() == 0) {
            fadeOutPiece(piece, howFar);
        } else {
            route.get(piece.getFieldNo()).setPiece(null);
            if (piece.getFieldNo() == route.size() - 1 && howFar == 1) {
                piece.increaseFieldNo(howFar);
                finishedRouteFadeOutPiece(piece);
            } else {
                movePieceOnBoard(piece, howFar);
            }
        }
    }

    private void fadeOutPiece(Piece piece, int howFar) {
        FadeTransition transition = game.getBoardController().createPieceFadeOutTransition(piece);
        if (howFar > 0) {
            transition.setOnFinished(event -> putPieceOnBoardAndFadeIn(piece, howFar));
        } else {
            transition.setOnFinished(event -> returnCapturedPieceToStartAndFadeIn(piece));
        }
        transition.play();
    }

    private void putPieceOnBoardAndFadeIn(Piece piece, int howFar) {
        game.getBoardController().putPieceOnBoard(piece, route.get(1));
        piece.increaseFieldNo(1);

        FadeTransition transition = game.getBoardController().createPieceFadeInTransition(piece);
        transition.setOnFinished(event -> movePieceOnBoard(piece, howFar - 1));
        transition.play();
    }

    private void returnCapturedPieceToStartAndFadeIn(Piece piece) {
        game.getBoardController().movePieceFromBoardToReadyToGoBox(piece);
        piece.returnToStart();

        FadeTransition transition = game.getBoardController().createPieceFadeInTransition(piece);
        transition.play();
    }

    private void movePieceOnBoard(Piece piece, int howFar) {
        if (howFar > 0) {
            // howFar can be zero in one case -  when the dice roll result is 1 and player starts new piece
            PathTransition transition = game.getBoardController().createPathTransitionForMove(piece, howFar, route);
            if (piece.getFieldNo() + howFar == route.size()) {
                // when piece finishes route
                transition.setOnFinished(event -> finishedRouteFadeOutPiece(piece));
            } else {
                Piece oldPiece = route.get(piece.getFieldNo() + howFar).setPiece(piece);
                if (oldPiece != null) {
                    oldPiece.returnToStart();
                    fadeOutPiece(oldPiece, -1);
                }
                transition.setOnFinished(event -> game.nextTurn()); // the end of the players turn
            }
            piece.toFront();
            piece.increaseFieldNo(howFar);
            transition.play();
        } else {
            route.get(piece.getFieldNo()).setPiece(piece);
            game.nextTurn();
        }
    }

    private void finishedRouteFadeOutPiece(Piece piece) {
        FadeTransition transition = game.getBoardController().createPieceFadeOutTransition(piece);
        transition.setOnFinished(event -> movePieceToFinishedAndFadeIn(piece));
        transition.play();
    }

    private void movePieceToFinishedAndFadeIn(Piece piece) {
        game.getBoardController().putPieceInFinishedPiecesBox(piece);
        FadeTransition transition = game.getBoardController().createPieceFadeInTransition(piece);

        if (!hasPiecesInGame()) {
            transition.setOnFinished(event -> game.finishGame());
        } else {
            transition.setOnFinished(event -> game.nextTurn()); // the end of the players turn
        }
        transition.play();
    }

    /*
    * Method used for testing only
     */
    public void setPieceAtField(int pieceNo, int fieldNo) {
        Piece piece = pieces.get(pieceNo);
        Field startField = route.get(1);
        Field destinationField = route.get(fieldNo);
        game.getBoardController().putPieceOnBoard(piece, startField);
        piece.setTranslateX((destinationField.getColumn() - startField.getColumn()) * 60d);
        piece.setTranslateY((destinationField.getRow() - startField.getRow()) * 60d);
        piece.increaseFieldNo(fieldNo);
        destinationField.setPiece(piece);

    }

}
