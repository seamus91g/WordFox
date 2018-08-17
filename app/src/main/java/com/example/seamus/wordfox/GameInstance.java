package com.example.seamus.wordfox;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.example.seamus.wordfox.game_screen.GameActivity;
import com.example.seamus.wordfox.player_switch.PlayerSwitchActivity;
import com.example.seamus.wordfox.results_screen.RoundnGameResults;

import java.lang.reflect.Array;
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
    private String longestWord;     // TODO: .. Changes every round. Does it belong in this class??
    private ArrayList<String> letters = new ArrayList<>();
    private ArrayList<ArrayList<String>> wordForEachLengthPerRound = new ArrayList<>();
    private String[] bestWordFoundEachRound = new String[NUMBER_ROUNDS];
    private int highestPossibleScore = 0;
    private final int thisGameIndex;
    private String playerID;   // Only exists if created on the player switch screen
    private String playerName;   // Only exists if created on the player switch screen

    private final ArrayList<String> roundIDs = new ArrayList<>();

    public static int getNumberRounds() {
        return NUMBER_ROUNDS;
    }

    public void setMaxNumberOfRounds(int maxNumberOfRounds) {
        NUMBER_ROUNDS = maxNumberOfRounds;
    }

    public String getPlayerName() {
        return playerName;
    }

    public enum GameState {ONGOING, FINISHED}

    private GameState myGameState;

    public void gamestateFinished(){
        myGameState = GameState.FINISHED;
    }
    public boolean isGameOngoing(){
        return (myGameState.equals(GameState.ONGOING));
    }

    // TODO: Unit test to validate scores
    {
        totalScore = 0;         // These initialisations seem unnecessary since MainActivity clears scores
        score = 0;
        round = 0;
        longestWord = "";       // Longest of the current round
        myGameState = GameState.ONGOING;
    }

    public GameInstance(int thisGameIndex) {
        this("Player " + (thisGameIndex + 1), thisGameIndex, null);
    }

    public GameInstance(int thisGameIndex, ArrayList<String> roundIDs) {
        this("Player " + (thisGameIndex + 1), thisGameIndex, roundIDs);
    }

    public GameInstance(String pId, int thisGameIndex) {
        this(pId, thisGameIndex, null);
    }
    public GameInstance(String pId, String name, int thisGameIndex) {
        this(pId, thisGameIndex, null);
        playerName = name;
    }

    public GameInstance(String pId, int thisGameIndex, ArrayList<String> roundIDs) {
        if (pId.equals("")) {
            pId = "Unknown";
        }
        playerName = pId;       // Only different for player 1
        if (roundIDs == null) {
            for (int i = 0; i < NUMBER_ROUNDS; i++) {
                this.roundIDs.add(UUID.randomUUID().toString());
            }
        } else {                              // Defensive copying
            this.roundIDs.addAll(roundIDs);
        }
        this.thisGameIndex = thisGameIndex;
        this.playerID = pId;
    }

    public String getRoundID() {
        return roundIDs.get(round);
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

    public ArrayList<String> getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters.add(letters);
    }

    public int getThisGameIndex() {
        return thisGameIndex;
    }

    public void addListOfSuggestedWords(ArrayList<String> suggestedWords) {
        wordForEachLengthPerRound.add(suggestedWords);
    }

    public ArrayList<String> getSuggestedWordsOfRound(int requestedRound) {
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
    }

    public void clearRoundScores() {
        score = 0;
        longestWord = "";
    }

    public ArrayList<String> getAllFinalWords() {
        return new ArrayList<String>(Arrays.asList(bestWordFoundEachRound));
    }

    public void setRoundWord(int whichRound, String word) {
        bestWordFoundEachRound[whichRound] = word;
    }

    public void setRoundWord(String word) {
        bestWordFoundEachRound[round] = word;
    }

    public String getRoundWord(int whichRound) {
        return bestWordFoundEachRound[whichRound];
    }
    public void incrementRound(){
        round++;
    }

    public int getRoundScore(int whichRound) {
        return bestWordFoundEachRound[whichRound].length();
    }

}
