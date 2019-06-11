package com.kodilla.royal.game.of.ur;

public class HumanPlayer extends Player {

    public HumanPlayer(final PieceColor color, final Field[] route) {
        super(BoardController.HUMAN_PLAYER, color, route);
    }

    @Override
    public void makeAMove(byte howFar) {
        // to implement
    }

    @Override
    public String toString() {
        return "Human Player";
    }
}
