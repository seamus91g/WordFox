package com.example.seamus.wordfox.datamodels;

import android.content.ContentValues;
import android.content.Context;

import com.example.seamus.wordfox.database.FoxSQLData;
import com.example.seamus.wordfox.database.GameTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by spgilroy on 11/20/2017.
 */

public class GameItem {
    private final String round1Id;
    private final String round2Id;
    private final String round3Id;
    private final String word1;
    private final String word2;
    private final String word3;
    private final String winner;
    private final int playerCount;
    private final ArrayList<String> winners;
    private final ArrayList<String> letters = new ArrayList<>();
    private final ArrayList<String> longestPossible = new ArrayList<>();
    private final ArrayList<ArrayList<String>> words = new ArrayList<>();   // Words of the winners, multiple winners if draw

    public GameItem(String round1Id, String round2Id, String round3Id,
                    String word1, String word2, String word3,
                    String winner, int playerCount) {
        this.round1Id = round1Id;
        this.round2Id = round2Id;
        this.round3Id = round3Id;
        this.word1 = word1;
        this.word2 = word2;
        this.word3 = word3;
        this.winner = winner;
        this.playerCount = playerCount;
        winners = new ArrayList<String>(Arrays.asList(winner.split("\\s*,\\s*")));
        organiseWinnerWords(word1, word2, word3);
    }

    private void organiseWinnerWords(String word1, String word2, String word3){
        ArrayList<String> firstWords = new ArrayList<>(Arrays.asList (word1.split("\\s*,\\s*")));
        ArrayList<String> secondWords = new ArrayList<>(Arrays.asList(word2.split("\\s*,\\s*")));
        ArrayList<String> thirdWords = new ArrayList<>(Arrays.asList (word3.split("\\s*,\\s*")));
        for(int i=0; i<firstWords.size(); ++i){
            words.add(new ArrayList<String>(Arrays.asList(
                    firstWords.get(i),
                    secondWords.get(i),
                    thirdWords.get(i))));
        }
    }

    public ArrayList<ArrayList<String>> getWinnerWords(){
        return words;
    }
    public ArrayList<String> getLetters(){
        return letters;
    }
    public ArrayList<String> getLongestWords(){
        return letters;
    }
    public ArrayList<String> getLetters(FoxSQLData myDB){
        if (letters.size()>0){
            return letters;
        }
        populateRoundData(myDB);
        return letters;
    }
    public ArrayList<String> getLongestWords(FoxSQLData myDB){
        if (longestPossible.size()>0){
            return longestPossible;
        }
        populateRoundData(myDB);
        return letters;
    }
    private void populateRoundData(FoxSQLData myDB){
        RoundItem thisRound1 = myDB.getRound(round1Id);
        RoundItem thisRound2 = myDB.getRound(round2Id);
        RoundItem thisRound3 = myDB.getRound(round3Id);
        longestPossible.add(thisRound1.getLongestPossible());
        longestPossible.add(thisRound2.getLongestPossible());
        longestPossible.add(thisRound3.getLongestPossible());
        letters.add(thisRound1.getLetters());
        letters.add(thisRound2.getLetters());
        letters.add(thisRound3.getLetters());
    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues(8);
        values.put(GameTable.COLUMN_R1_ID, round1Id);
        values.put(GameTable.COLUMN_R2_ID, round2Id);
        values.put(GameTable.COLUMN_R3_ID, round3Id);
        values.put(GameTable.COLUMN_W1_ID, word1);
        values.put(GameTable.COLUMN_W2_ID, word2);
        values.put(GameTable.COLUMN_W3_ID, word3);
        values.put(GameTable.COLUMN_WINNER, winner);
        values.put(GameTable.COLUMN_PLAYER_COUNT, playerCount);
        return values;
    }

    public String getRound1Id() {
        return round1Id;
    }

    public String getRound2Id() {
        return round2Id;
    }

    public String getRound3Id() {
        return round3Id;
    }

    public String getWord1Id() {
        return word1;
    }

    public String getWord2Id() {
        return word2;
    }

    public String getWord3Id() {
        return word3;
    }

//    public String getWinner() {
//        return winner;
//    }
    public String getWinner(int index) {
        return winners.get(index);
    }
    public String getWinnerString(){
        return winner;
    }
    public ArrayList<String> getWinners(){
        return winners;
    }
    public boolean isWinner(String userName){
        return winners.contains(userName);
    }
    public boolean isDraw(){
        return (winners.size() > 1);
    }
    public String getLetters(int index){
        return letters.get(index);
    }
    public int getPlayerCount() {
        return playerCount;
    }
}
