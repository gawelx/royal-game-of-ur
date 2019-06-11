package com.kodilla.royal.game.of.ur;

public class ComputerPlayer extends Player {

    private final static int MAX_NO_OF_PIECES_ON_BOARD = 3;

    public ComputerPlayer(final PieceColor color, final Field[] route) {
        super(BoardController.COMPUTER_PLAYER, color, route);
    }

    @Override
    public void makeAMove(byte howFar) {
        // to implement with PriorityQueue
    }

    @Override
    public String toString() {
        return "Computer Player";
    }
}
