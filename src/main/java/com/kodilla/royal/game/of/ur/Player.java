package com.kodilla.royal.game.of.ur;

public abstract class Player {

    private static final int PIECES_COUNT = 7;

    private final byte playerNo;
    private final PieceColor color;
    private final Piece[] pieces;
    private final Field[] route;

    public Player(final byte playerNo, final PieceColor color, final Field[] route) {
        this.playerNo = playerNo;
        this.color = color;
        this.route = route;
        this.pieces = new Piece[PIECES_COUNT];
        for (byte i = 0; i < PIECES_COUNT; i++) {
            pieces[i] = new Piece(color);
        }
    }

    public byte getPlayerNo() {
        return playerNo;
    }

    public boolean hasPiecesInGame() {
        for (Piece piece : pieces) {
            if (piece.isInGame()) {
                return true;
            }
        }
        return false;
    }

    abstract public void makeAMove(byte howFar);
}
