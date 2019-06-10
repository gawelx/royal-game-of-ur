package com.kodilla.royal.game.of.ur;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class Board {

    public final static byte PLAYER1 = 1;
    public final static byte PLAYER2 = 2;

    public final static byte SIMPLE_ROUTE = 1;
    public final static byte LONG_ROUTE = 2;

    private final static byte ROW_COUNT = 3;
    private final static byte COLUMN_COUNT = 8;

    private final Field[][] fields;

    private final Scene scene;

    public Board() throws IOException {
        fields = new Field[ROW_COUNT][COLUMN_COUNT];
        for (byte row = 0; row < ROW_COUNT; row++) {
            for (byte col = 0; col < COLUMN_COUNT; col++) {
                if (row % 2 == 1 || col > 3 || col < 2) {
                    fields[row][col] = new Field(row, col, (row % 2 == 0 || col == 4));
                }
            }
        }

        ClassLoader classLoader = getClass().getClassLoader();
        Parent root = FXMLLoader.load(classLoader.getResource("FXML/board.fxml"));
        scene = new Scene(root, 670, 420);
    }

    public Scene getScene() {
        return scene;
    }

    public Field[] getRoute(byte playerNo, byte route) throws Exception {
        switch (route) {
            case SIMPLE_ROUTE:
                return getSimpleRoute(playerNo);
            case LONG_ROUTE:
                return null; // to implement
            default:
                throw new Exception("Wrong type of route.");
        }
    }

    private Field[] getSimpleRoute(byte playerNo) throws Exception {
        Field[] route = new Field[15];
        byte playerRowNo;
        switch (playerNo) {
            case PLAYER1:
                playerRowNo = 0;
                break;
            case PLAYER2:
                playerRowNo = 2;
                break;
            default:
                throw new Exception("Wrong player number.");
        }
        route[0] = null;
        System.arraycopy(fields[playerRowNo], 4, route, 1, 4);
        for (int i = 7; i >= 0; i--) {
            route[5 + 7 - i] = fields[1][i];
        }
        System.arraycopy(fields[playerRowNo], 0, route, 13, 2);
        return route;
    }
}
