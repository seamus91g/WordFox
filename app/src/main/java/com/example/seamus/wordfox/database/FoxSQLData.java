package com.example.seamus.wordfox.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.example.seamus.wordfox.MainActivity;
import com.example.seamus.wordfox.database.WordTable;
import com.example.seamus.wordfox.datamodels.GameItem;
import com.example.seamus.wordfox.datamodels.RoundItem;
import com.example.seamus.wordfox.datamodels.WordItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by spgilroy on 11/20/2017.
 */

public class FoxSQLData {
    private Context mContext;
    private SQLiteDatabase wfDatabase;
    SQLiteOpenHelper wfDbHelper;

    public FoxSQLData(Context context){
        this.mContext = context;
        wfDbHelper = new DBHelper(mContext);
        wfDatabase = wfDbHelper.getWritableDatabase();
    }

    public void open(){
        wfDatabase = wfDbHelper.getWritableDatabase();
    }

    public void close(){
        wfDbHelper.close();
    }

    public long getCountWords() {
        return DatabaseUtils.queryNumEntries(wfDatabase, WordTable.TABLE_WORDS);
    }
    public long getCountRounds() {
        return DatabaseUtils.queryNumEntries(wfDatabase, RoundTable.TABLE_ROUNDS);
    }
    public long getCountGames() {
        return DatabaseUtils.queryNumEntries(wfDatabase, GameTable.TABLE_GAMES);
    }

    public void createWordItem(WordItem item){
        if(item.getWordSubmitted().equals("")){
            return;
        }
        ContentValues values = item.toValues();
        wfDatabase.insert(WordTable.TABLE_WORDS, null, values);
    }
    public void createRoundItem(RoundItem item){
        ContentValues values = item.toValues();
        wfDatabase.insert(RoundTable.TABLE_ROUNDS, null, values);
    }
    public void createGameItem(GameItem item){
        ContentValues values = item.toValues();
        wfDatabase.insert(GameTable.TABLE_GAMES, null, values);
//        Log.d(MainActivity.MONITOR_TAG, "create Game item!!, END");
    }

    public List<WordItem> getAllWords() {
        List<WordItem> words = new ArrayList<>();
        Cursor cursor = wfDatabase.query(WordTable.TABLE_WORDS, WordTable.ALL_COLUMNS, null, null, null, null, null);

        while (cursor.moveToNext()){
            WordItem word = new WordItem(
                    cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_WORD)),
                    cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_PLAYER)),
                    (cursor.getInt(cursor.getColumnIndex(WordTable.COLUMN_VALID)) == 1),   // Convert to bool.  If 1, true. If not 1, false
                    (cursor.getInt(cursor.getColumnIndex(WordTable.COLUMN_FINAL)) == 1),
                    cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_GAME_ID))
            );
            words.add(word);
        }
        cursor.close();
        return words;
    }
    public List<WordItem> getValidWords(String player) {
//    public List<WordItem> getWords(String player, boolean isValid, boolean isFinal) {
        List<WordItem> words = new ArrayList<>();
//        (String table,
//            String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy, String limit)
        Cursor cursor = wfDatabase.query(WordTable.TABLE_WORDS, WordTable.ALL_COLUMNS, WordTable.COLUMN_PLAYER + " = '" + player + "'", null, null, null, null);
//        Cursor cursor = wfDatabase.query(WordTable.TABLE_WORDS, WordTable.ALL_COLUMNS, null, null, null, null, null);

        while (cursor.moveToNext()){
            WordItem word = new WordItem(
                    cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_WORD)),
                    cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_PLAYER)),
                    (cursor.getInt(cursor.getColumnIndex(WordTable.COLUMN_VALID)) == 1),   // Convert to bool.  If 1, true. If not 1, false
                    (cursor.getInt(cursor.getColumnIndex(WordTable.COLUMN_FINAL)) == 1),
                    cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_GAME_ID))
            );
            words.add(word);
        }
        cursor.close();
        return words;
    }
    public List<RoundItem> getAllRounds() {
        List<RoundItem> rounds = new ArrayList<>();
        Cursor cursor = wfDatabase.query(RoundTable.TABLE_ROUNDS, RoundTable.ALL_COLUMNS, null, null, null, null, null);

        while (cursor.moveToNext()){
            RoundItem round = new RoundItem(
                    cursor.getString(cursor.getColumnIndex(RoundTable.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(RoundTable.COLUMN_LETTERS)),
                    cursor.getString(cursor.getColumnIndex(RoundTable.COLUMN_LONGEST_POSSIBLE))
            );
            rounds.add(round);
        }
        cursor.close();
        return rounds;
    }

    public List<GameItem> getAllGames() {
        List<GameItem> games = new ArrayList<>();
        Cursor cursor = wfDatabase.query(GameTable.TABLE_GAMES, GameTable.ALL_COLUMNS, null, null, null, null, null);

        while (cursor.moveToNext()){
            GameItem round = new GameItem(
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_R1_ID)),
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_R2_ID)),
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_R3_ID)),
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_W1_ID)),
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_W2_ID)),
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_W3_ID)),
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_WINNER)),
                    cursor.getInt(cursor.getColumnIndex(GameTable.COLUMN_PLAYER_COUNT))
            );
            games.add(round);
        }
        cursor.close();
        return games;
    }

}
