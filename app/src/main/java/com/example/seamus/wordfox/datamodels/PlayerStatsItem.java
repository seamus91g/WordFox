package com.example.seamus.wordfox.datamodels;

import android.content.ContentValues;

import com.example.seamus.wordfox.database.PlayerStatsTable;

import java.util.UUID;

/**
 * Created by Gilroy on 2/10/2018.
 */

public class PlayerStatsItem {
    private final UUID playerID;
    private final int wins;
    private final int loses;
    private final int draws;

    public PlayerStatsItem(UUID playerID, int wins, int loses, int draws) {
        this.playerID = playerID;
        this.wins = wins;
        this.loses = loses;
        this.draws = draws;
    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues(4);
        values.put(PlayerStatsTable.COLUMN_NAME, playerID.toString());
        values.put(PlayerStatsTable.COLUMN_WINS, wins);
        values.put(PlayerStatsTable.COLUMN_LOSES, loses);
        values.put(PlayerStatsTable.COLUMN_DRAWS, draws);
        return values;
    }

    public UUID getPlayerID() {
        return playerID;
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
