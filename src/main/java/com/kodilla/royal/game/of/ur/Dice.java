package com.kodilla.royal.game.of.ur;

import javafx.scene.text.Text;

import java.util.Random;

public class Dice {

    private final Random diceRoller;
    private final Text diceRollResultTxt;

    public Dice(final Text diceRollResultTxt) {
        this.diceRoller = new Random();
        this.diceRollResultTxt = diceRollResultTxt;
    }

    public int roll() {
        int diceRollResult = diceRoller.nextInt(4) + 1;
        diceRollResultTxt.setText(Integer.toString(diceRollResult));
        return diceRollResult;
    }
}
