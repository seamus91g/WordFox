package com.example.seamus.wordfox;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Desmond on 05/07/2017.
 */

public class GameInstance {

    public static int NUMBER_ROUNDS = 3;
    public final String MONITOR_TAG = "GameInstance";
    private int totalScore;      // total Score tracks the accumulated score across rounds.
    private int score;   // score is just the score from the current round.
    private int round;   // round is a counter for the round of the game.
    private ArrayList<String> allLongestPossible = new ArrayList<>();
    private String longestWord;
    private ArrayList<String> letters = new ArrayList<>();
    private ArrayList<ArrayList<String>> wordForEachLengthPerRound = new ArrayList<>();
    private String round1Word = "";
    private String round2Word = "";
    private String round3Word = "";
    private int highestPossibleScore = 0;
    private int round1Length = 0;
    private int round2Length = 0;
    private int round3Length = 0;
    private final int thisGameIndex;
    private String playerID;   // Only exists if created on the player switch screen



    private final ArrayList<String> roundIDs = new ArrayList<>();

    public static int getNumberRounds() {
        return NUMBER_ROUNDS;
    }

    public void setMaxNumberOfRounds(int maxNumberOfRounds) {
        this.NUMBER_ROUNDS = maxNumberOfRounds;
    }

    private enum GameState {ONGOING, FINISHED}

    ;
    private GameState myGameState;

    {
        totalScore = 0;         // These initialisations seem unnecessary since MainActivity clears scores
        score = 0;
        round = 0;
        longestWord = "";       // Longest of the current round
        myGameState = GameState.ONGOING;
    }
    GameInstance(int thisGameIndex) {
        this("Player " + (thisGameIndex + 1), thisGameIndex, null);
    }
    GameInstance(int thisGameIndex, ArrayList<String> roundIDs) {
        this("Player " + (thisGameIndex + 1), thisGameIndex, roundIDs);
    }
    GameInstance(String pId, int thisGameIndex) {
        this(pId, thisGameIndex, null);
    }
    GameInstance(String pId ,int thisGameIndex, ArrayList<String> roundIDs) {
        if (pId.equals("")){
            pId = "Unknown";
        }
        if(roundIDs == null){
            for (int i = 0; i< NUMBER_ROUNDS; i++){
                this.roundIDs.add(UUID.randomUUID().toString());
            }
        }else{                              // Defensive copying
            for (String ID: roundIDs) {
                this.roundIDs.add(ID);
            }
        }
        this.thisGameIndex = thisGameIndex;
        this.playerID = pId;
    }

    public String getRoundID(int roundNum) {
        return roundIDs.get(roundNum);
    }
    public ArrayList<String> getRoundIDs() {
        return roundIDs;
    }

    public String getPlayerID() {
        return playerID;
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

    public GameState getMyGameState() {
        return myGameState;
    }

    public void addListOfSuggestedWords(ArrayList<String> suggestedWords){
        wordForEachLengthPerRound.add(suggestedWords);
    }
    public ArrayList<String> getSuggestedWordsOfRound(int requestedRound){
        return wordForEachLengthPerRound.get(requestedRound);
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
        Log.d("Check this out", "highest possible score is " + highestPossibleScore);
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
//        Log.d(MONITOR_TAG, "Clearing all scores");
    }

    public void clearRoundScores() {
        score = 0;
        longestWord = "";
//        Log.d(MONITOR_TAG, "Clearing round scores");
    }
    public ArrayList<String> getAllFinalWords(){
        ArrayList<String> finalWordList = new ArrayList<>(
                Arrays.asList(  round1Word,
                                round2Word,
                                round3Word));
        return finalWordList;
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

    public String getRoundXWord(int x) {
        String guess = "";
        switch (x){
            case 1:  guess = getRound1Word();
                    break;
            case 2: guess = getRound2Word();
                    break;
            case 3: guess = getRound3Word();
                    break;
        }
        return guess;
    }


    public void startGame(Context context) {
        round++;
        Log.d(MONITOR_TAG, "startGame: game_index = " + thisGameIndex);
        Log.d(MONITOR_TAG, "startGame: no. of rounds = " + round);

        int currentRound = MainActivity.allGameInstances.get(thisGameIndex).getRound();
        // if the current round is anything but the last round, start a new round following on
        // from the previous round
        if (round < NUMBER_ROUNDS) {

            Intent gameIntent = new Intent(context, GameActivity.class);
            gameIntent.putExtra("game_index", thisGameIndex);
            context.startActivity(gameIntent);
        } else {
            Log.d(MONITOR_TAG, "startGame: round is more than max no. of rounds so switch player or end game = " + round);
            // if the current round is the last round but there are still players to play, launch
            // the PlayerSwitchActivity
            myGameState = GameState.FINISHED;


            // check each player to see if there's anyone yet to play, if there is then start the
            // player switch activity
            for (int x = 0; x < MainActivity.allGameInstances.size(); x++) {
                if ((MainActivity.allGameInstances.get(x).myGameState).equals(GameState.ONGOING)) {


                    // the round counter is not being correctly reset to zero when the new player
                    // takes over, getting a game index of 0 with round = 4 even when p2 has started
                    // meaning you'll need to move the game index along to the next player so that
                    // the round counter resets back to zero
                    Log.d(MONITOR_TAG, "startGame: starting player switch: " + x);

                    Intent gameIntent = new Intent(context, PlayerSwitchActivity.class);
                    gameIntent.putExtra("game_index", x);
                    context.startActivity(gameIntent);
                    return;
                }
            }

            //only start the end of game screen if the current player has played all their rounds
            // and there's no player still yet to play
            Log.d(MONITOR_TAG, "GameInstance: Moving to score screen 2! ");
            //commented this out while testing the new End screen
//            Intent ScoreScreen2Intent = new Intent(context, ScoreScreen2Activity.class);
//            context.startActivity(ScoreScreen2Intent);

            //testing new end screen
//            Intent EndScreenIntent = new Intent(context, RoundnGameResults.class);
//            context.startActivity(EndScreenIntent);


            //testing new end screen
            Intent EndScreenIntent = new Intent(context, RoundnGameResults.class);
            Bundle endScreenBundle = new Bundle();
            endScreenBundle.putString("key", "game");
            endScreenBundle.putInt("gameIndexNumber", thisGameIndex);
            EndScreenIntent.putExtras(endScreenBundle);
            context.startActivity(EndScreenIntent);
        }
    }

}
