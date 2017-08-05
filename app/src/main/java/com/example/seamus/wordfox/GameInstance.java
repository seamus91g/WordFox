package com.example.seamus.wordfox;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Desmond on 05/07/2017.
 */

public class GameInstance {

    public final String MONITOR_TAG = "myTag";
    private int totalScore;      // total Score tracks the accumulated score across rounds.
    private int score;   // score is just the score from the current round.
    private int round;   // round is a counter for the round of the game.
    private String longestPossible;   // round is a counter for the round of the game.
    private String longestWord;
    private String round1Word = "";
    private String round2Word = "";
    private String round3Word = "";
    private int round1Length = 0;
    private int round2Length = 0;
    private int round3Length = 0;


    GameInstance() {
        totalScore = 0;     // These initialisations seem unnecessary since MainActivity clears scores
        score = 0;
        round = 0;
        longestWord = "";   // Longest of the current round
        longestPossible = "";   // Longest possible word of the current round
        Log.d(MONITOR_TAG, "New game instance");
    }

    public int getTotalScore() {
        return totalScore;
    }

    private void setTotalScore(int point) {
        totalScore += point;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int point) {
        setTotalScore(point - score);
        score = point;
    }

    public void setLongestWord(String word) {
        longestWord = word;
    }

    public String getLongestWord() {
        return longestWord;
    }
    public void setLongestPossible(String word) {
        longestPossible = word;
    }

    public String getLongestPossible() {
        return longestPossible;
    }

    public int getRound() {
        return round;
    }

    public void clearAllScores() {
        totalScore = 0;
        score = 0;
        round = 0;
        longestWord = "";
        Log.d(MONITOR_TAG, "Clearing all scores");
    }

    public void clearRoundScores() {
        score = 0;
        longestWord = "";
        Log.d(MONITOR_TAG, "Clearing round scores");
    }

    public void setRound1Word(String word) {
        round1Word = word;
    }

    public void setRound1Length(int len) {
        round1Length = len;
    }

    public void setRound2Word(String word) {
        round2Word = word;
    }

    public void setRound2Length(int len) {
        round2Length = len;
    }

    public void setRound3Word(String word) {
        round3Word = word;
    }

    public void setRound3Length(int len) {
        round3Length = len;
    }

    public String getRound1Word() {
        return round1Word;
    }

    public int getRound1Length() {
        return round1Length;
    }

    public String getRound2Word() {
        return round2Word;
    }

    public int getRound2Length() {
        return round2Length;
    }

    public String getRound3Word() {
        return round3Word;
    }

    public int getRound3Length() {
        return round3Length;
    }



    public void startGame(Context context) {
//        Log.d("Count number of rounds", "GameInstance: no. of completed rounds = " + round);
        round++;
        Log.d(MONITOR_TAG, "GameInstance: no. of rounds = " + round);
        if (round < 3) {
//            Log.d(MONITOR_TAG, "In startGame");
            Intent gameIntent = new Intent(context, GameActivity.class);
//            Log.d(MONITOR_TAG, "In startGame 2");
            context.startActivity(gameIntent);
        } else {
            Log.d(MONITOR_TAG, "starting Score Screen 2");
            Intent ScoreScreen2Intent = new Intent(context, ScoreScreen2Activity.class);
            context.startActivity(ScoreScreen2Intent);
        }
    }
}
