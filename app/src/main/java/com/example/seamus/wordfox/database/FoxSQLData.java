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
import com.example.seamus.wordfox.datamodels.OpponentItem;
import com.example.seamus.wordfox.datamodels.PlayerStatsItem;
import com.example.seamus.wordfox.datamodels.RoundItem;
import com.example.seamus.wordfox.datamodels.WordItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by spgilroy on 11/20/2017.
 */

public class FoxSQLData {
    private Context mContext;
    private SQLiteDatabase wfDatabase;
    SQLiteOpenHelper wfDbHelper;

    public FoxSQLData(Context context) {
        this.mContext = context;
        wfDbHelper = new DBHelper(mContext);
        wfDatabase = wfDbHelper.getWritableDatabase();
    }

    public void open() {
        wfDatabase = wfDbHelper.getWritableDatabase();
    }

    public void close() {
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

    // Count of total games by a specific player
    public long getCountGames(String player) {
        Cursor cursor = wfDatabase.query(
                PlayerStatsTable.TABLE_PLAYER_STATS,
                PlayerStatsTable.SUM_GAMES,
                PlayerStatsTable.COLUMN_NAME + " = '" + player + "'",
                null, null, null, null);

        cursor.moveToNext();
        long gameCount = cursor.getInt(0);
        cursor.close();
        return gameCount;
    }

    public void createWordItem(WordItem item) {
        if (item.getWordSubmitted().equals("")) {
            return;
        }
        ContentValues values = item.toValues();
        wfDatabase.insert(WordTable.TABLE_WORDS, null, values);
    }

    public void createRoundItem(RoundItem item) {
        ContentValues values = item.toValues();
        wfDatabase.insert(RoundTable.TABLE_ROUNDS, null, values);
    }

    public void createGameItem(GameItem item) {
        ContentValues values = item.toValues();
        wfDatabase.insert(GameTable.TABLE_GAMES, null, values);
//        Log.d(MainActivity.MONITOR_TAG, "create Game item!!, END");
    }

    public void updatePlayerStats(String player, String result) {
        // Ensure exists
        PlayerStatsItem item1 = new PlayerStatsItem(player, 0, 0, 0);
        wfDatabase.insertWithOnConflict(PlayerStatsTable.TABLE_PLAYER_STATS, null, item1.toValues(), SQLiteDatabase.CONFLICT_IGNORE);  /// Given names and then 0, 0, 0
        // Increment the relevant column by 1
        // UPDATE player_stats SET wins = wins + 1 WHERE name = fox
        wfDatabase.execSQL("UPDATE " + PlayerStatsTable.TABLE_PLAYER_STATS +
                " SET '" + result + "' = '" + result + "' + 1" +
                " WHERE " + PlayerStatsTable.COLUMN_NAME + " = '" + player + "';"
        );
    }

    // Winner, Loser, Draw?
    public void updateOpponentItem(String p1, String p2, boolean draw) {
        // Ensure exists
        wfDatabase.insertWithOnConflict(OpponentTable.TABLE_OPPONENTS, null, new OpponentItem(p1, p2, 0, 0, 0).toValues(), SQLiteDatabase.CONFLICT_IGNORE);  /// Given names and then 0, 0, 0
        wfDatabase.insertWithOnConflict(OpponentTable.TABLE_OPPONENTS, null, new OpponentItem(p2, p1, 0, 0, 0).toValues(), SQLiteDatabase.CONFLICT_IGNORE);  /// Given names and then 0, 0, 0
        // If draw, don't change win or lose column
        String[] bindingArgs = new String[]{p1, p2};
        String incrementRowWin = OpponentTable.COLUMN_WINS;
        String incrementRowLose = OpponentTable.COLUMN_LOSES;
        if (draw) {
            incrementRowWin = OpponentTable.COLUMN_DRAWS;
            incrementRowLose = OpponentTable.COLUMN_DRAWS;
        }
        // Increment the relevant columns by 1
        wfDatabase.execSQL("UPDATE " + OpponentTable.TABLE_OPPONENTS +
                        " SET " + incrementRowWin + " = " + incrementRowWin + " + 1" +
                        " WHERE " + OpponentTable.COLUMN_NAME + " = ? AND " + OpponentTable.COLUMN_OPPONENT_NAME + " = ?;",
                bindingArgs);
        wfDatabase.execSQL("UPDATE " + OpponentTable.TABLE_OPPONENTS +
                        " SET " + incrementRowLose + " = " + incrementRowLose + " + 1" +
                        " WHERE " + OpponentTable.COLUMN_OPPONENT_NAME + " = ? AND " + OpponentTable.COLUMN_NAME + " = ?;",
                bindingArgs);
    }

    public PlayerStatsItem getStats(String p1) {
        PlayerStatsItem psi;
        Cursor cursor = wfDatabase.query(
                PlayerStatsTable.TABLE_PLAYER_STATS,
                PlayerStatsTable.ALL_COLUMNS,
                RoundTable.COLUMN_ID + " = '" + p1 + "'",
                null, null, null, null);

        cursor.moveToNext();
        psi = new PlayerStatsItem(
                cursor.getString(cursor.getColumnIndex(PlayerStatsTable.COLUMN_NAME)),
                cursor.getInt(cursor.getColumnIndex(PlayerStatsTable.COLUMN_WINS)),
                cursor.getInt(cursor.getColumnIndex(PlayerStatsTable.COLUMN_LOSES)),
                cursor.getInt(cursor.getColumnIndex(PlayerStatsTable.COLUMN_DRAWS))
        );

        cursor.close();
        return psi;
    }

    public OpponentItem getOpponentStats(String p1, String p2) {
        OpponentItem opStats;
        Cursor cursor = wfDatabase.query(
                OpponentTable.TABLE_OPPONENTS,
                OpponentTable.ALL_COLUMNS,
                OpponentTable.COLUMN_NAME + " = '" + p1 + "' && " + OpponentTable.COLUMN_OPPONENT_NAME + " = '" + p2 + "'",
                null, null, null, null);

        cursor.moveToNext();
        opStats = new OpponentItem(
                cursor.getString(cursor.getColumnIndex(OpponentTable.COLUMN_NAME)),
                cursor.getString(cursor.getColumnIndex(OpponentTable.COLUMN_OPPONENT_NAME)),
                cursor.getInt(cursor.getColumnIndex(OpponentTable.COLUMN_WINS)),
                cursor.getInt(cursor.getColumnIndex(OpponentTable.COLUMN_LOSES)),
                cursor.getInt(cursor.getColumnIndex(OpponentTable.COLUMN_DRAWS))
        );
        cursor.close();
        return opStats;
    }

    public List<WordItem> getAllWords() {
        List<WordItem> words = new ArrayList<>();
        Cursor cursor = wfDatabase.query(WordTable.TABLE_WORDS, WordTable.ALL_COLUMNS, null, null, null, null, null);

        while (cursor.moveToNext()) {
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
        String whereClause = WordTable.COLUMN_PLAYER + " = '" + player + "' AND " + WordTable.COLUMN_VALID + " = 1";
        Cursor cursor = wfDatabase.query(WordTable.TABLE_WORDS, WordTable.ALL_COLUMNS, whereClause, null, null, null, null);
//        Cursor cursor = wfDatabase.query(WordTable.TABLE_WORDS, WordTable.ALL_COLUMNS, null, null, null, null, null);

        while (cursor.moveToNext()) {
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

        while (cursor.moveToNext()) {
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

    public RoundItem getRound(String rID) {
        RoundItem round;
        Cursor cursor = wfDatabase.query(RoundTable.TABLE_ROUNDS, RoundTable.ALL_COLUMNS, RoundTable.COLUMN_ID + " = '" + rID + "'", null, null, null, null);

        if (cursor.getCount() == 0) {
            round = new RoundItem(
                    "unknown",
                    "unknown",
                    "unknown"
            );
        } else {
            cursor.moveToNext();
            round = new RoundItem(
                    cursor.getString(cursor.getColumnIndex(RoundTable.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndex(RoundTable.COLUMN_LETTERS)),
                    cursor.getString(cursor.getColumnIndex(RoundTable.COLUMN_LONGEST_POSSIBLE))
            );
        }
        cursor.close();
        return round;
    }

    public WordItem getWord(String wID) {
        WordItem word;
        Cursor cursor = wfDatabase.query(WordTable.TABLE_WORDS, WordTable.ALL_COLUMNS, WordTable.COLUMN_ID + " = '" + wID + "'", null, null, null, null);

        cursor.moveToNext();
        word = new WordItem(
                cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_WORD)),
                cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_PLAYER)),
                (cursor.getInt(cursor.getColumnIndex(WordTable.COLUMN_VALID)) == 1),   // Convert to bool.  If 1, true. If not 1, false
                (cursor.getInt(cursor.getColumnIndex(WordTable.COLUMN_FINAL)) == 1),
                cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_GAME_ID))
        );
        cursor.close();
        return word;
    }

    public List<WordItem> getWordsByRound(String rID) {
        List<WordItem> words = new ArrayList<>();
        Cursor cursor = wfDatabase.query(WordTable.TABLE_WORDS, WordTable.ALL_COLUMNS, WordTable.COLUMN_GAME_ID + " = '" + rID + "'", null, null, null, null);

        while (cursor.moveToNext()) {
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

    //    public List<GameItem> getAllGames(Context myCont) {
    public List<GameItem> getAllGames() {
        List<GameItem> games = new ArrayList<>();
        Cursor cursor = wfDatabase.query(GameTable.TABLE_GAMES, GameTable.ALL_COLUMNS, null, null, null, null, null);

        while (cursor.moveToNext()) {
            GameItem round = new GameItem(
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_R1_ID)),
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_R2_ID)),
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_R3_ID)),
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_W1_ID)),
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_W2_ID)),
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_W3_ID)),
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_WINNER)),
                    cursor.getInt(cursor.getColumnIndex(GameTable.COLUMN_PLAYER_COUNT))
//                    myCont
            );
            games.add(round);
        }
        cursor.close();

        return games;
    }

    public GameItem getGame(String gameId) {
        GameItem game;
        Cursor cursor = wfDatabase.query(GameTable.TABLE_GAMES, GameTable.ALL_COLUMNS, GameTable.COLUMN_R1_ID + " = '" + gameId + "'", null, null, null, null);

        cursor.moveToNext();
        game = new GameItem(
                cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_R1_ID)),
                cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_R2_ID)),
                cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_R3_ID)),
                cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_W1_ID)),
                cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_W2_ID)),
                cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_W3_ID)),
                cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_WINNER)),
                cursor.getInt(cursor.getColumnIndex(GameTable.COLUMN_PLAYER_COUNT))
        );
        cursor.close();
        return game;
    }


//--win/lose/draw count
//    query Opponents table
//    SELECT SUM(Wins), SUM(Loses), SUM(Draws) FROM Opponents WHERE Name = "Fox";
//
//    COLUMN_NAME = "name";
//    COLUMN_OPPONENT_NAME = "opponent_name";
//    COLUMN_WINS = "wins";
//    COLUMN_LOSES = "loses";
//    COLUMN_DRAWS = "draws";


//--NO. of games completed
//    Counter in GameData
//    SELECT SUM((Wins + Loses + Draws)) as 'Total' FROM Opponents	WHERE Name = "Fox";


}
