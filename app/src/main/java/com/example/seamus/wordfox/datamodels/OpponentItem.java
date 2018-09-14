package com.example.seamus.wordfox.datamodels;

import android.content.ContentValues;

import com.example.seamus.wordfox.database.OpponentTable;

import java.util.UUID;

/**
 * Created by Gilroy on 2/10/2018.
 */

public class OpponentItem {
    private final UUID playerID;
    private final UUID opponentID;
    private final int wins;
    private final int loses;
    private final int draws;

    public OpponentItem(UUID playerID, UUID opponentID, int wins, int loses, int draws) {
        this.playerID = playerID;
        this.opponentID = opponentID;
        this.wins = wins;
        this.loses = loses;
        this.draws = draws;
    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues(5);
        values.put(OpponentTable.COLUMN_NAME, playerID.toString());
        values.put(OpponentTable.COLUMN_OPPONENT_NAME, opponentID.toString());
        values.put(OpponentTable.COLUMN_WINS, wins);
        values.put(OpponentTable.COLUMN_LOSES, loses);
        values.put(OpponentTable.COLUMN_DRAWS, draws);
        return values;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public UUID getOpponentID() {
        return opponentID;
    }

    public int getWins() {
        return wins;
    }

    public int getLoses() {
        return loses;
    }

    public int getDraws() {
        return draws;
    }
}
