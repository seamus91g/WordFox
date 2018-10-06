package com.example.seamus.wordfox;


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
    private final boolean isOnline;
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
    private PlayerIdentity player;

    private final ArrayList<UUID> roundIDs = new ArrayList<>();
    private boolean isGroupOwner;       // TODO: Not required to store this????

    public static int getNumberRounds() {
        return NUMBER_ROUNDS;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public boolean isGroupOwner() {
        return isGroupOwner;
    }

    public enum GameState {ONGOING, FINISHED}

    private GameState myGameState;

    // TODO: Unit test to validate scores
    {
        totalScore = 0;         // TODO: These initialisations seem unnecessary since MainActivity clears scores
        score = 0;
        round = 0;
        longestWord = "";       // Longest of the current round
        myGameState = GameState.ONGOING;
    }

    public GameInstance(UUID playerId, String nm, int thisGameIndex) {
        this(playerId, nm, thisGameIndex, null, false, false);
    }
    public GameInstance(UUID playerId, String playerName, int thisGameIndex, ArrayList<UUID> roundIDs) {
        this(playerId, playerName, thisGameIndex, roundIDs, false, false);
    }
    public GameInstance(UUID playerId, String nm, int thisGameIndex, boolean isOnline, boolean isGO) {
        this(playerId, nm, thisGameIndex, null, isOnline, isGO);
    }
    public GameInstance(UUID playerId, String playerName, int thisGameIndex, ArrayList<UUID> roundIDs, boolean isOnline, boolean isGO) {
        this.isGroupOwner = isGO;
        if (roundIDs == null) {
            for (int i = 0; i < NUMBER_ROUNDS; i++) {
                this.roundIDs.add(UUID.randomUUID());
            }
        } else {                              // Defensive copying
            this.roundIDs.addAll(roundIDs);
        }
        this.isOnline = isOnline;
        player = new PlayerIdentity(playerId, playerName);
        this.thisGameIndex = thisGameIndex;
    }

    public void setMaxNumberOfRounds(int maxNumberOfRounds) {
        NUMBER_ROUNDS = maxNumberOfRounds;
    }

    public UUID getID() {
        return player.ID;
    }

    public PlayerIdentity getPlayer() {
        return player;
    }

    public void gamestateFinished() {
        myGameState = GameState.FINISHED;
    }

    public boolean isGameOngoing() {
        return (myGameState.equals(GameState.ONGOING));
    }

    public UUID getRoundID() {
        return roundIDs.get(round);
    }

    public UUID getRoundID(int roundNum) {
        return roundIDs.get(roundNum);
    }

    public ArrayList<UUID> getRoundIDs() {
        return roundIDs;
    }

    public String getName() {
        return player.username;
    }

    public int getHighestPossibleScore() {
        return highestPossibleScore;
    }

    public String getLetters(int roundIndex) {
        return letters.get(roundIndex);
    }

    public String getRoundLetters() {
        if(letters.size() > round){
            return letters.get(round);
        }
        return null;
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
        if(allLongestPossible.size() > round){
            return allLongestPossible.get(round);
        }
        return null;
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

    public void incrementRound() {
        round++;
    }

    public int getRoundScore(int whichRound) {
        return bestWordFoundEachRound[whichRound].length();
    }

}
