package com.kodilla.royal.game.of.ur;

import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class ComputerPlayer extends Player {

    public ComputerPlayer(final PieceColor color, final List<Field> route, final Board gameBoard) {
        super(Board.COMPUTER_PLAYER, color, route, gameBoard);
    }

    public Piece chooseBestMove(int howFar) {
        Comparator<Piece> pieceMoveComparator = new Comparator<Piece>() {
            @Override
            public int compare(Piece p1, Piece p2) {
                return p1.getMovePriority(howFar, getRoute()) - p2.getMovePriority(howFar, getRoute());
            }
        };
        Queue<Piece> moves = new PriorityQueue<>(pieceMoveComparator);
        for (Piece piece : pieces) {
            if (piece.isInGame()) {
                moves.add(piece);
            }
        }

        return moves.size() == 0 ? null : moves.peek();
    }

    @Override
    public String toString() {
        return "Computer Player";
    }
}
