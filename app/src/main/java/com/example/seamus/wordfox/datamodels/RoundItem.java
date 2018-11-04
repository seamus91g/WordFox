package com.example.seamus.wordfox.datamodels;

import android.content.ContentValues;

import com.example.seamus.wordfox.database.RoundTable;
import com.example.seamus.wordfox.database.WordTable;

import java.util.UUID;

/**
 * Created by Gilroy
 */

public class RoundItem {
    private final UUID roundId;
    private final String letters;
    private final String longestPossible;

    public RoundItem(UUID roundId, String letters, String longestPossible) {
        if (roundId == null){
            roundId = UUID.randomUUID();
        }
        this.roundId = roundId;
        this.letters = letters;
        this.longestPossible = longestPossible;
    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues(3);
        values.put(RoundTable.COLUMN_ID, roundId.toString());
        values.put(RoundTable.COLUMN_LETTERS, letters);
        values.put(RoundTable.COLUMN_LONGEST_POSSIBLE, longestPossible);
        return values;
    }

    public UUID getRoundId() {
        return roundId;
    }

    public String getLetters() {
        return letters;
    }

    public String getLongestPossible() {
        return longestPossible;
    }
}
