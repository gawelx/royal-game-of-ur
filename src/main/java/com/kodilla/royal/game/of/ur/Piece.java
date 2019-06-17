package com.kodilla.royal.game.of.ur;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;

import java.util.List;

public class Piece extends ImageView {

    public final static int CAN_CAPTURE = 0;
    public final static int CAN_BE_CAPTURED = 5;
    public final static int WILL_BE_SAFE_AFTER_MOVE = 10;
    public final static int CAN_MOVE = 15;
    public final static int CANT_MOVE = 100;
    public final static int FINNISHED = 200;

    private boolean inGame;
    private int fieldNo;
    private final PieceColor color;
    private final Player owner;

    public Piece(final Player owner) {
        this.inGame = true;
        this.fieldNo = 0;
        this.color = owner.getColor();
        this.owner = owner;
        ClassLoader classLoader = getClass().getClassLoader();
        String fileName = color == PieceColor.DARK ? "pawn_dark.png" : "pawn_light.png";
        String imageUrl = classLoader.getResource("img/" + fileName).toString();
        setImage(new Image(imageUrl));
        getStyleClass().add("piece");
    }

    public boolean isInGame() {
        return inGame;
    }

    public int getFieldNo() {
        return fieldNo;
    }

    public void increaseFieldNo(int howMuch) {
        this.fieldNo += howMuch;
    }

    public void getBackToStart() {
        owner.putPieceAtStart(this);
    }

    public boolean canMove(int howFar, List<Field> route) {
        if (fieldNo + howFar > route.size()) {
            return false;
        }
        if (fieldNo + howFar == route.size()) {
            return true;
        }
        return route.get(fieldNo + howFar).canTakePiece(this);
    }

    public boolean canCapture(int howFar, List<Field> route) {
        return canMove(howFar, route) && route.get(fieldNo + howFar).canBeCapturedBy(this);
    }

    public boolean canBeCaptured(List<Field> route) {
        if (fieldNo == 0) {
            return false;
        }
        int start = Math.max(1, fieldNo - 4);
        for (int i = start; i < fieldNo; i++) {
            if (route.get(i).getPiece() != null && route.get(fieldNo).canBeCapturedBy(route.get(i).getPiece())) {
                return true;
            }
        }
        return false;
    }

    public boolean willBeSaveAfterMove(int howFar, List<Field> route) {
        return canMove(howFar, route) && !route.get(fieldNo + howFar).isCaptureAllowed();
    }

    public int getMovePriority(int howFar, List<Field> route) {
        if (canCapture(howFar, route)) {
            return CAN_CAPTURE;
        }
        if (canBeCaptured(route)) {
            return CAN_BE_CAPTURED;
        }
        if (willBeSaveAfterMove(howFar, route)) {
            return WILL_BE_SAFE_AFTER_MOVE;
        }
        if (canMove(howFar, route)) {
            return CAN_MOVE;
        }
        if (!isInGame()) {
            return FINNISHED;
        }
        return CANT_MOVE;
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
