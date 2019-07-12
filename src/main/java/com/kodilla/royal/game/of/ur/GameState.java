package com.kodilla.royal.game.of.ur;

public class GameState {

    private int diceRollResult;
    private Player currentPlayer;
    private boolean canHumanPickPiece;

    public GameState(Player currentPlayer) {
        this.diceRollResult = 0;
        this.currentPlayer = currentPlayer;
        this.canHumanPickPiece = false;
    }

    public int getDiceRollResult() {
        return diceRollResult;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean canHumanPickPiece() {
        return canHumanPickPiece;
    }

    public void setDiceRollResult(int diceRollResult) {
        this.diceRollResult = diceRollResult;
    }

    public void setCurrentPlayer(Player currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public void allowHumanPickPiece() {
        this.canHumanPickPiece = true;
    }

    public void disallowHumanPickPiece() {
        this.canHumanPickPiece = false;
    }

    public boolean isCurrentPlayer(Player player) {
        return currentPlayer == player;
    }

}
