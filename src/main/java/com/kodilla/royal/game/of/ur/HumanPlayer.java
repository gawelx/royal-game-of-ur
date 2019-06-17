package com.kodilla.royal.game.of.ur;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class HumanPlayer extends Player {

    public HumanPlayer(final PieceColor color, final List<Field> route, final Board gameBoard) {
        super(Board.HUMAN_PLAYER, color, route, gameBoard);
    }

    public void setMouseClickedListener(EventHandler<? super MouseEvent> event) {
        for (Piece piece : pieces) {
            piece.setOnMouseClicked(event);
        }
    }

    @Override
    public String toString() {
        return "Human Player";
    }
}
