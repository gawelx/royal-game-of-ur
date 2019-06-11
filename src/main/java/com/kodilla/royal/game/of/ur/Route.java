package com.kodilla.royal.game.of.ur;

public class Route {

    public final static byte SIMPLE_ROUTE = 1;
    public final static byte LONG_ROUTE = 2;

    public Field[] getRoute(byte playerNo, byte route, Field[][] fields) throws Exception {
        switch (route) {
            case SIMPLE_ROUTE:
                return getSimpleRoute(playerNo, fields);
            case LONG_ROUTE:
                return getLongRoute(playerNo, fields); // not yet implemented
            default:
                throw new Exception("Wrong type of route.");
        }
    }

    private Field[] getSimpleRoute(byte playerNo, Field[][] fields) throws Exception {
        Field[] route = new Field[15];
        byte playerRowNo;
        switch (playerNo) {
            case BoardController.HUMAN_PLAYER:
                playerRowNo = 0;
                break;
            case BoardController.COMPUTER_PLAYER:
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

    private Field[] getLongRoute(byte playerNo, Field[][] fields) {
        return new Field[0]; // to implement
    }

}
