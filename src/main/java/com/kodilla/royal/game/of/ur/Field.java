package com.kodilla.royal.game.of.ur;

public class Field {

    private final boolean captureAllowed;
    private final int column;
    private final int row;
    private Piece piece;

    public Field(final int row, final int column, final boolean captureAllowed) {
        this.column = column;
        this.row = row;
        this.captureAllowed = captureAllowed;
        this.piece = null;
    }

    public boolean isCaptureAllowed() {
        return captureAllowed;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    public Piece getPiece() {
        return piece;
    }

    public boolean canTakePiece(Piece piece) {
        if (this.piece == null) {
            return true;
        }
        return this.piece.isNotFriendOf(piece) && captureAllowed;
    }

    public boolean canBeCapturedBy(Piece piece) {
        return this.piece != null && this.piece.isNotFriendOf(piece) && captureAllowed;
    }

    public Piece setPiece(Piece piece) {
        Piece oldPiece = this.piece;
        this.piece = piece;
        return oldPiece;
    }

}
