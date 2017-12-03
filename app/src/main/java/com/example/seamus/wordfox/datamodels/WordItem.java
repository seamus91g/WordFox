package com.example.seamus.wordfox.datamodels;

import android.content.ContentValues;

import com.example.seamus.wordfox.database.WordTable;

import java.util.UUID;

/**
 * Created by spgilroy on 11/20/2017.
 */

public class WordItem {
    private final String wordId;
    private final String wordSubmitted;
    private final String playerName;
    private final boolean isValid;
    private final boolean isFinal;
    private final String gameId;

    public WordItem(String wordId, String wordSubmitted, String playerName, boolean isValid, boolean isFinal, String gameId) {
        if (wordId == null){
            wordId = UUID.randomUUID().toString();
        }
        this.wordId = wordId;
        this.wordSubmitted = wordSubmitted;
        this.playerName = playerName;
        this.isValid = isValid;
        this.isFinal = isFinal;
        this.gameId = gameId;
    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues(6);
        values.put(WordTable.COLUMN_ID, wordId);
        values.put(WordTable.COLUMN_WORD, wordSubmitted);
        values.put(WordTable.COLUMN_PLAYER, playerName);
        values.put(WordTable.COLUMN_VALID, (isValid) ? 1 : 0);
        values.put(WordTable.COLUMN_FINAL, (isFinal) ? 1 : 0);
        values.put(WordTable.COLUMN_GAME_ID, gameId);
        return values;
    }

    public String getWordId() {
        return wordId;
    }

    public String getWordSubmitted() {
        return wordSubmitted;
    }

    public String getPlayerName() {
        return playerName;
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public String getGameId() {
        return gameId;
    }
}
