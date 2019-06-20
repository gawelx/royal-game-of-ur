package com.kodilla.royal.game.of.ur;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public class Piece extends ImageView {

    private final static int CAN_CAPTURE = 0;
    private final static int CAN_BE_CAPTURED = 5;
    private final static int WILL_BE_SAFE_AFTER_MOVE = 10;
    private final static int CAN_MOVE = 15;
    private final static int CANT_MOVE = 100;

    private final PieceColor color;
    private final Player owner;

    private boolean inGame;
    private int fieldNo;

    public Piece(final Player owner) {
        this.color = owner.getColor();
        this.owner = owner;
        this.inGame = true;
        this.fieldNo = 0;

        ClassLoader classLoader = getClass().getClassLoader();
        String fileName = color == PieceColor.DARK ? "pawn_dark.png" : "pawn_light.png";
        String imageUrl = classLoader.getResource("img/" + fileName).toExternalForm();
        setImage(new Image(imageUrl));
        getStyleClass().add("piece");
    }

    public boolean isInGame() {
        return inGame;
    }

    public int getFieldNo() {
        return fieldNo;
    }

    public PieceColor getColor() {
        return color;
    }

    public void increaseFieldNo(int howMuch) {
        fieldNo += howMuch;
        if (fieldNo == owner.getRoute().size()) {
            finishRoute();
        }
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

    private boolean canCapture(int howFar, List<Field> route) {
//        return canMove(howFar, route) && route.get(fieldNo + howFar).canBeCapturedBy(this);
        if (canMove(howFar, route) && fieldNo + howFar < route.size()) {
//            if (fieldNo + howFar == route.size()) {
//                return false;
//            }
            return route.get(fieldNo + howFar).canBeCapturedBy(this);
        }
        return false;
    }

    private boolean canBeCaptured(List<Field> route) {
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

    private boolean willBeSafeAfterMove(int howFar, List<Field> route) {
//        return canMove(howFar, route) && !route.get(fieldNo + howFar).isCaptureAllowed();
        if (canMove(howFar, route)) {
            if (fieldNo + howFar < route.size()){
                return !route.get(fieldNo + howFar).isCaptureAllowed();
            }
            return true;
        }
        return false;
    }

    public int getMovePriority(int howFar, List<Field> route) {
        if (canCapture(howFar, route)) {
            return CAN_CAPTURE;
        }
        if (canBeCaptured(route)) {
            return CAN_BE_CAPTURED;
        }
        if (willBeSafeAfterMove(howFar, route)) {
            return WILL_BE_SAFE_AFTER_MOVE;
        }
        if (canMove(howFar, route)) {
            return CAN_MOVE;
        }
        return CANT_MOVE;
    }

    public void returnToStart() {
        fieldNo = 0;
    }

    private void finishRoute() {
        inGame = false;
    }

    public boolean isFriendOf(Piece piece) {
        return this.color == piece.color;
    }

}
