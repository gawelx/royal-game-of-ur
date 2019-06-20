package com.kodilla.royal.game.of.ur;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;

public class ComputerPlayer extends Player {

    public ComputerPlayer(
            final Game game,
            final int routeType,
            final Field[][] fields
    ) throws Exception {
        super(PieceColor.LIGHT, game, routeType, fields);
    }

    public Piece chooseBestMove(int howFar) {
        Comparator<Piece> pieceMoveComparator = Comparator.comparingInt(p -> p.getMovePriority(howFar, getRoute()));
        Queue<Piece> moves = new PriorityQueue<>(pieceMoveComparator);
        for (Piece piece : pieces) {
            if (piece.isInGame() && piece.canMove(howFar, getRoute())) {
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
