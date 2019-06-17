package com.kodilla.royal.game.of.ur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Route {

    public final static int SIMPLE_ROUTE = 1;
    public final static int LONG_ROUTE = 2;

    public static List<Field> getRoute(int playerNo, int route, Field[][] fields) throws Exception {
        switch (route) {
            case SIMPLE_ROUTE:
                return getSimpleRoute(playerNo, fields);
            case LONG_ROUTE:
                return getLongRoute(playerNo, fields); // not yet implemented
            default:
                throw new Exception("Wrong type of route.");
        }
    }

    private static List<Field> getSimpleRoute(int playerNo, Field[][] fields) throws Exception {
        Field[] route = new Field[16];
        byte playerRowNo;
        switch (playerNo) {
            case Board.HUMAN_PLAYER:
                playerRowNo = 0;
                break;
            case Board.COMPUTER_PLAYER:
                playerRowNo = 2;
                break;
            default:
                throw new Exception("Wrong player number.");
        }
        System.arraycopy(fields[playerRowNo], 4, route, 1, 4);
        for (int i = 7; i >= 0; i--) {
            route[5 + 7 - i] = fields[1][i];
        }
        System.arraycopy(fields[playerRowNo], 0, route, 13, 3);
        return Arrays.asList(route);
    }

    private static List<Field> getLongRoute(int playerNo, Field[][] fields) {
        return new ArrayList<>(); // to implement
    }

}
