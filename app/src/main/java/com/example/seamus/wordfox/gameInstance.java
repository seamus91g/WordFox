package com.example.seamus.wordfox;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by Desmond on 05/07/2017.
 */

public class gameInstance {

    public static final String MONITOR_TAG = "myTag";
    private static int totalScore;      // total Score tracks the accumulated score across rounds.
    private static int score;
    private static String longestWord;

    public void gameInstance() {
        totalScore = 0;     // These initialisations seem unnecessary since MainActivity clears scores
        score = 0;
        longestWord = "";   // Longest of the current round
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
        score = point;
        setTotalScore(point);
    }

    public void setLongestWord(String word) {
        longestWord = word;
    }

    public String getLongestWord() {
        return longestWord;
    }

    public static void clearAllScores() {
        totalScore = 0;
        score = 0;
        longestWord = "";
    }

    public void clearRoundScores() {
        score = 0;
        longestWord = "";
    }

    public void startGame(Context context) {
        Log.d(MONITOR_TAG, "In startGame");
        Intent gameIntent = new Intent(context, GameActivity.class);
        Log.d(MONITOR_TAG, "In startGame 2");
        context.startActivity(gameIntent);
    }
}
