package com.kodilla.royal.game.of.ur;

public class Field {

    private final boolean captureAllowed;
    private final byte column;
    private final byte row;
    private Piece piece;

    public Field(final byte column, final byte row, final boolean captureAllowed) {
        this.column = column;
        this.row = row;
        this.captureAllowed = captureAllowed;
        this.piece = null;
    }

    public boolean isCaptureAllowed() {
        return captureAllowed;
    }

    public byte getColumn() {
        return column;
    }

    public byte getRow() {
        return row;
    }

    public boolean canTakePiece(Piece piece) {
        if (this.piece == null) {
            return true;
        }
        return !this.piece.isFriendOf(piece) && captureAllowed;
    }

    public Piece setPiece(Piece piece) throws FieldOccupiedException, CaptureNotAllowedException {
        // this exceptions are only to show that I know how to use them ;)
        // the ability of setting the piece should be checked with canTakePiece() method before calling this one
        if (this.piece != null) {
            if (this.piece.isFriendOf(piece)) {
                throw new FieldOccupiedException();
            } else {
                if (!captureAllowed) {
                    throw new CaptureNotAllowedException();
                }
            }
        }
        Piece oldPiece = this.piece;
        this.piece = piece;
        return oldPiece;
    }
}
