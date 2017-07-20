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
    private static int score;   // score is just the score from the current round.
    private static int round;   // round is a counter for the round of the game.
    private static String longestPossible;   // round is a counter for the round of the game.
    private static String longestWord;
    private static String round1Word = "";
    private static String round2Word = "";
    private static String round3Word = "";
    private static int round1Length = 0;
    private static int round2Length = 0;
    private static int round3Length = 0;


    gameInstance() {
        totalScore = 0;     // These initialisations seem unnecessary since MainActivity clears scores
        score = 0;
        longestWord = "";   // Longest of the current round
        longestPossible = "";   // Longest possible word of the current round
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
        int tempScore = score;
        score = point;
        setTotalScore(point - tempScore);
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

    public static void clearAllScores() {
        totalScore = 0;
        score = 0;
        round = 0;
        longestWord = "";
    }

    public void clearRoundScores() {
        score = 0;
        longestWord = "";
    }

    public static void setRound1Word(String word) {
        round1Word = word;
    }

    public static void setRound1Length(int len) {
        round1Length = len;
    }

    public static void setRound2Word(String word) {
        round2Word = word;
    }

    public static void setRound2Length(int len) {
        round2Length = len;
    }

    public static void setRound3Word(String word) {
        round3Word = word;
    }

    public static void setRound3Length(int len) {
        round3Length = len;
    }

    public static String getRound1Word() {
        return round1Word;
    }

    public static int getRound1Length() {
        return round1Length;
    }

    public static String getRound2Word() {
        return round2Word;
    }

    public static int getRound2Length() {
        return round2Length;
    }

    public static String getRound3Word() {
        return round3Word;
    }

    public static int getRound3Length() {
        return round3Length;
    }



    public void startGame(Context context) {
//        Log.d("Count number of rounds", "gameInstance: no. of completed rounds = " + round);
        round++;
        Log.d("Count number of rounds", "gameInstance: no. of rounds started = " + round);
        if (round < 4) {
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
