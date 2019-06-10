package com.kodilla.royal.game.of.ur;

public class Piece {

    private boolean inGame;
    private byte fieldNo;
    private PieceColor color;

    public Piece(PieceColor color) {
        this.inGame = true;
        this.fieldNo = 0;
        this.color = color;
    }

    public boolean isInGame() {
        return inGame;
    }

    public byte move(byte howFar) {
        return fieldNo += howFar;
    }

    public boolean isMovePossible(byte howFar, byte routeLength) {
        return fieldNo + howFar < routeLength + 2;
    }

    public void returnToStart() {
        fieldNo = 0;
    }

    public void finishRoute() {
        inGame = false;
    }

    public boolean isFriendOf(Piece piece) {
        return this.color == piece.color;
    }
}
