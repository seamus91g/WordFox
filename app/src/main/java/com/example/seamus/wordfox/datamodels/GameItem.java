package com.example.seamus.wordfox.datamodels;

import android.content.ContentValues;

import com.example.seamus.wordfox.database.GameTable;

import java.util.UUID;

/**
 * Created by spgilroy on 11/20/2017.
 */

public class GameItem {
    private final String round1Id;
    private final String round2Id;
    private final String round3Id;
    private final String word1Id;
    private final String word2Id;
    private final String word3Id;
    private final String winner;
    private final int playerCount;

    public GameItem(String round1Id, String round2Id, String round3Id, String word1Id, String word2Id, String word3Id, String winner, int playerCount) {
        this.round1Id = round1Id;
        this.round2Id = round2Id;
        this.round3Id = round3Id;
        this.word1Id = word1Id;
        this.word2Id = word2Id;
        this.word3Id = word3Id;
        this.winner = winner;
        this.playerCount = playerCount;
    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues(7);
        values.put(GameTable.COLUMN_R1_ID, round1Id);
        values.put(GameTable.COLUMN_R2_ID, round2Id);
        values.put(GameTable.COLUMN_R3_ID, round3Id);
        values.put(GameTable.COLUMN_W1_ID, word1Id);
        values.put(GameTable.COLUMN_W2_ID, word2Id);
        values.put(GameTable.COLUMN_W3_ID, word3Id);
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
        return word1Id;
    }

    public String getWord2Id() {
        return word2Id;
    }

    public String getWord3Id() {
        return word3Id;
    }

    public String getWinner() {
        return winner;
    }

    public int getPlayerCount() {
        return playerCount;
    }
}
