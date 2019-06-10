package com.kodilla.royal.game.of.ur;

public class FieldOccupiedException extends Exception {

    public FieldOccupiedException() {
        super("This move is not possible. The field is already occupied by the piece with the same color.");
    }
}
