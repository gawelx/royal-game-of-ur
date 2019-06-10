package com.kodilla.royal.game.of.ur;

public class CaptureNotAllowedException extends Exception {

    public CaptureNotAllowedException() {
        super("This field is already taken by the piece belonging to the other player," +
                " but capture is not allowed on this field.");
    }
}
