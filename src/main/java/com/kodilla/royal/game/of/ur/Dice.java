package com.kodilla.royal.game.of.ur;

import java.util.Random;

public class Dice {

    private final Random diceRoller;
    private int result;     // Used in tests only

    public Dice() {
        this.diceRoller = new Random();
    }

    /*
     * Used in tests only
     */
    public Dice(int result) {
        diceRoller = null;
        this.result = result;
    }

    public int roll() {
        if (diceRoller == null) {
            return result;      // Used in tests only
        } else {
            return diceRoller.nextInt(5);
        }
    }

}
