package com.kodilla.royal.game.of.ur;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Route {

    public final static int SIMPLE_ROUTE = 1;
    public final static int LONG_ROUTE = 2;

    public static List<Field> getRoute(PieceColor color, int routeType, Field[][] fields) throws Exception {
        switch (routeType) {
            case SIMPLE_ROUTE:
                return getSimpleRoute(color, fields);
            case LONG_ROUTE:
                return getLongRoute(color, fields); // not yet implemented
            default:
                throw new Exception("Wrong type of route.");
        }
    }

    private static List<Field> getSimpleRoute(PieceColor color, Field[][] fields) throws Exception {
        Field[] route = new Field[15];
        int playerRowNo = color == PieceColor.DARK ? 0 : 2;
        System.arraycopy(fields[playerRowNo], 4, route, 1, 4);
        for (int i = 7; i >= 0; i--) {
            route[5 + 7 - i] = fields[1][i];
        }
        System.arraycopy(fields[playerRowNo], 0, route, 13, 2);
        return Arrays.asList(route);
    }

    private static List<Field> getLongRoute(PieceColor color, Field[][] fields) {
        return new ArrayList<>(); // to implement (eventually)
    }

}
