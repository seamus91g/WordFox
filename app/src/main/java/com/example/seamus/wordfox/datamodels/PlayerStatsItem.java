package com.example.seamus.wordfox.datamodels;

import android.content.ContentValues;

import com.example.seamus.wordfox.database.OpponentTable;
import com.example.seamus.wordfox.database.PlayerStatsTable;

/**
 * Created by Gilroy on 2/10/2018.
 */

public class PlayerStatsItem {
    private final String name;
    private final int wins;
    private final int loses;
    private final int draws;

    public PlayerStatsItem(String name, int wins, int loses, int draws) {
        this.name = name;
        this.wins = wins;
        this.loses = loses;
        this.draws = draws;
    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues(4);
        values.put(PlayerStatsTable.COLUMN_NAME, name);
        values.put(PlayerStatsTable.COLUMN_WINS, wins);
        values.put(PlayerStatsTable.COLUMN_LOSES, loses);
        values.put(PlayerStatsTable.COLUMN_DRAWS, draws);
        return values;
    }

    public String getName() {
        return name;
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
