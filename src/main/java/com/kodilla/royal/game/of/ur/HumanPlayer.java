package com.kodilla.royal.game.of.ur;

public class HumanPlayer extends Player {

    private final String name;

    public HumanPlayer(final PieceColor color, final Field[] route, final String name) {
        super(Board.PLAYER1, color, route);
        this.name = name;
    }

    @Override
    public void makeAMove(byte howFar) {
        // to implement
    }
}
