package com.example.seamus.wordfox.datamodels;

import android.content.ContentValues;

import com.example.seamus.wordfox.database.OpponentTable;

import java.util.UUID;

/**
 * Created by Gilroy on 2/10/2018.
 */

public class OpponentItem {
    private final String name;
    private final String opponentName;
    private final int wins;
    private final int loses;
    private final int draws;

    public OpponentItem(String name, String opponentName, int wins, int loses, int draws) {
        this.name = name;
        this.opponentName = opponentName;
        this.wins = wins;
        this.loses = loses;
        this.draws = draws;
    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues(5);
        values.put(OpponentTable.COLUMN_NAME, name);
        values.put(OpponentTable.COLUMN_OPPONENT_NAME, opponentName);
        values.put(OpponentTable.COLUMN_WINS, wins);
        values.put(OpponentTable.COLUMN_LOSES, loses);
        values.put(OpponentTable.COLUMN_DRAWS, draws);
        return values;
    }

    public String getName() {
        return name;
    }

    public String getOpponentName() {
        return opponentName;
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
