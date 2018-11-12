package capsicum.game.wordfox.datamodels;

import android.content.ContentValues;

import capsicum.game.wordfox.database.WordTable;

import java.util.UUID;

/**
 * Created by spgilroy
 */

public class WordItem {
    private final UUID wordId;
    private final String wordSubmitted;
    private final UUID playerID;
    private final boolean isValid;
    private final boolean isFinal;
    private final UUID gameId;

    public WordItem(UUID wordId, String wordSubmitted, UUID playerID, boolean isValid, boolean isFinal, UUID gameId) {
        if (wordId == null){
            wordId = UUID.randomUUID();
        }
        this.wordId = wordId;
        this.wordSubmitted = wordSubmitted;
        this.playerID = playerID;
        this.isValid = isValid;
        this.isFinal = isFinal;
        this.gameId = gameId;
    }

    public ContentValues toValues(){
        ContentValues values = new ContentValues(6);
        values.put(WordTable.COLUMN_ID, wordId.toString());
        values.put(WordTable.COLUMN_WORD, wordSubmitted);
        values.put(WordTable.COLUMN_PLAYER, playerID.toString());
        values.put(WordTable.COLUMN_VALID, (isValid) ? 1 : 0);
        values.put(WordTable.COLUMN_FINAL, (isFinal) ? 1 : 0);
        values.put(WordTable.COLUMN_GAME_ID, gameId.toString());
        return values;
    }

    public UUID getWordId() {
        return wordId;
    }

    public String getWordSubmitted() {
        return wordSubmitted;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public boolean isValid() {
        return isValid;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public UUID getGameId() {
        return gameId;
    }
}
