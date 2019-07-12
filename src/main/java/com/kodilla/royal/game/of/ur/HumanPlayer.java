package com.kodilla.royal.game.of.ur;

public class HumanPlayer extends Player {

    public HumanPlayer(
            final Game game,
            final int routeType,
            final Field[][] fields) throws Exception {
        super(PieceColor.DARK, game, routeType, fields);
    }

    @Override
    public String toString() {
        return "Human Player";
    }

}
