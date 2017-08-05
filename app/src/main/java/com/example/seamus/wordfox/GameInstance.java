package com.example.seamus.wordfox;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Desmond on 05/07/2017.
 */

public class GameInstance {

    public final String MONITOR_TAG = "myTag";
    private int totalScore;      // total Score tracks the accumulated score across rounds.
    private int score;   // score is just the score from the current round.
    private int round;   // round is a counter for the round of the game.
    //    private String longestPossible;   // round is a counter for the round of the game.
    private ArrayList<String> allLongestPossible = new ArrayList<>();
    private String longestWord;
    private ArrayList<String> letters = new ArrayList<>();
    private String round1Word = "";
    private String round2Word = "";
    private String round3Word = "";
    private int highestPossibleScore = 0;
    private int round1Length = 0;
    private int round2Length = 0;
    private int round3Length = 0;
    private int maxNumberOfRounds = 3;
    private int thisGameIndex;

    private enum GameState {ONGOING, FINISHED}

    ;
    private GameState myGameState;

    GameInstance() {
        totalScore = 0;     // These initialisations seem unnecessary since MainActivity clears scores
        score = 0;
        round = 0;
        longestWord = "";   // Longest of the current round
//        longestPossible = "";   // Longest possible word of the current round
        myGameState = GameState.ONGOING;
        Log.d(MONITOR_TAG, "New game instance");
    }

    public int getHighestPossibleScore() {
        return highestPossibleScore;
    }

    public String getLetters(int roundIndex) {
        return letters.get(roundIndex);
    }

    public String getRoundLetters() {
        return letters.get(round);
    }

    public void setLetters(String letters) {
        this.letters.add(letters);
    }

    public int getThisGameIndex() {
        return thisGameIndex;
    }

    public void setThisGameIndex(int thisGameIndex) {
        this.thisGameIndex = thisGameIndex;
    }

    public GameState getMyGameState() {
        return myGameState;
    }

    public void setMyGameState(GameState myGameState) {
        this.myGameState = myGameState;
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
//        longestPossible = word;
        allLongestPossible.add(word);
        highestPossibleScore += word.length();
    }

    public String getLongestPossible() {
        return allLongestPossible.get(round);
    }

    public String getRoundLongestPossible(int roundIndex) {
        return allLongestPossible.get(roundIndex);
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
        Log.d(MONITOR_TAG, "!!!! This is game index: " + thisGameIndex);
        Log.d(MONITOR_TAG, "GameInstance: no. of rounds = " + round);
        if (round < maxNumberOfRounds) {
//            Log.d(MONITOR_TAG, "In startGame");
            Intent gameIntent = new Intent(context, GameActivity.class);
            gameIntent.putExtra("game_index", thisGameIndex);
//            Log.d(MONITOR_TAG, "In startGame 2");
            context.startActivity(gameIntent);
        } else {
            Log.d(MONITOR_TAG, "starting Score Screen 2");
            myGameState = GameState.FINISHED;
            for (int x = 0; x < MainActivity.allGameInstances.size(); x++) {
                if ((MainActivity.allGameInstances.get(x).myGameState).equals(GameState.ONGOING)) {

                    Log.d(MONITOR_TAG, "starting player switch: " + x);

                    Intent gameIntent = new Intent(context, PlayerSwitchActivity.class);
                    gameIntent.putExtra("game_index", x);
                    context.startActivity(gameIntent);
                    return;
                }
            }
            Log.d(MONITOR_TAG, "Moving to score screen two! ");
            Intent ScoreScreen2Intent = new Intent(context, ScoreScreen2Activity.class);
            context.startActivity(ScoreScreen2Intent);
        }
    }
}
