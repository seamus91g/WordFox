package com.example.seamus.wordfox;

/**
 * Created by Desmond on 03/11/2017.
 * A class to be used at the end of a multiplayer game to store the player's name and game ending score
 */

public class playersFinalScoresNNames {

    private int endOfGameScore;
    private String playerName;

    public playersFinalScoresNNames(int endOfGameScore, String playerName) {
        this.endOfGameScore = endOfGameScore;
        this.playerName = playerName;
    }

    public int getEndOfGameScore() {
        return endOfGameScore;
    }

    public void setEndOfGameScore(int endOfGameScore) {
        this.endOfGameScore = endOfGameScore;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
}
