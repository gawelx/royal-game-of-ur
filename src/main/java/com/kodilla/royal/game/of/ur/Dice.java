package com.kodilla.royal.game.of.ur;

import java.util.Random;

public class Dice {

    private final Random rollResultGenerator;

    public Dice() {
        this.rollResultGenerator = new Random();
    }

    public int roll() {
        return rollResultGenerator.nextInt(5);
    }

}
