package capsicum.game.wordfox.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;

import org.jetbrains.annotations.Nullable;

import capsicum.game.wordfox.datamodels.GameItem;
import capsicum.game.wordfox.datamodels.ImageItem;
import capsicum.game.wordfox.datamodels.OpponentItem;
import capsicum.game.wordfox.datamodels.PlayerStatsItem;
import capsicum.game.wordfox.datamodels.ProfileImageItem;
import capsicum.game.wordfox.datamodels.RoundItem;
import capsicum.game.wordfox.datamodels.WordItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by spgilroy
 */

public class FoxSQLData {
    private Context mContext;
    private SQLiteDatabase wfDatabase;
    private SQLiteOpenHelper wfDbHelper;

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

    public void addProfileImage(UUID player, Bitmap profileImage, int screenWidth) {
        deleteProfileImage(player);
        ProfileImageItem profileImageItem = new ProfileImageItem(player, profileImage, screenWidth);
        wfDatabase.insert(ImageTable.TABLE_IMAGES, null, profileImageItem.toValues());
        wfDatabase.insert(ImageTable.TABLE_IMAGES, null, profileImageItem.toValuesSmall());
    }

    public void deleteProfileImage(UUID playerId) {
        wfDatabase.delete(ImageTable.TABLE_IMAGES,
                ImageTable.COLUMN_IMAGE_ID + " = '" + ProfileImageItem.PROFILE_PICTURE_ID + "' AND " +
                        ImageTable.COLUMN_PLAYER_ID + " = '" + playerId.toString() + "'", null);
        wfDatabase.delete(ImageTable.TABLE_IMAGES,
                ImageTable.COLUMN_IMAGE_ID + " = '" + ProfileImageItem.PROFILE_PICTURE_ID_SMALL + "' AND " +
                        ImageTable.COLUMN_PLAYER_ID + " = '" + playerId.toString() + "'", null);
    }

    @Nullable
    public Bitmap getProfileIcon(UUID player) {
        return getImageByPlayer(player, ProfileImageItem.PROFILE_PICTURE_ID_SMALL);
    }

    @Nullable
    public Bitmap getProfileImage(UUID player) {
        return getImageByPlayer(player, ProfileImageItem.PROFILE_PICTURE_ID);
    }

    @Nullable
    public Bitmap getImageByPlayer(UUID player, String imageID) {
        ImageItem imageItem;
        Cursor cursor = wfDatabase.query(
                ImageTable.TABLE_IMAGES,
                ImageTable.ALL_COLUMNS,
                ImageTable.COLUMN_IMAGE_ID + " = '" + imageID + "' AND " +
                        ImageTable.COLUMN_PLAYER_ID + " = '" + player.toString() + "'",
                null, null, null, null);
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        } else {
            cursor.moveToNext();
            imageItem = new ImageItem(
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(ImageTable.COLUMN_PLAYER_ID))),
                    cursor.getString(cursor.getColumnIndex(ImageTable.COLUMN_IMAGE_ID)),
                    cursor.getBlob(cursor.getColumnIndex(ImageTable.COLUMN_IMAGE_BLOB))
            );
        }
        cursor.close();
        return imageItem.getImage();
    }

    public void deleteAllImages() {
        wfDatabase.delete(ImageTable.TABLE_IMAGES, null, null);
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

    }

    public void updatePlayerStats(UUID player, String result) {
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
    public void updateOpponentItem(UUID p1, UUID p2, boolean draw) {
        // Ensure exists
        wfDatabase.insertWithOnConflict(OpponentTable.TABLE_OPPONENTS, null, new OpponentItem(p1, p2, 0, 0, 0).toValues(), SQLiteDatabase.CONFLICT_IGNORE);  /// Given names and then 0, 0, 0
        wfDatabase.insertWithOnConflict(OpponentTable.TABLE_OPPONENTS, null, new OpponentItem(p2, p1, 0, 0, 0).toValues(), SQLiteDatabase.CONFLICT_IGNORE);  /// Given names and then 0, 0, 0
        // If draw, don't change win or lose column
        String[] bindingArgs = new String[]{p1.toString(), p2.toString()};
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
                UUID.fromString(cursor.getString(cursor.getColumnIndex(PlayerStatsTable.COLUMN_NAME))),
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
                UUID.fromString(cursor.getString(cursor.getColumnIndex(OpponentTable.COLUMN_NAME))),
                UUID.fromString(cursor.getString(cursor.getColumnIndex(OpponentTable.COLUMN_OPPONENT_NAME))),
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
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_ID))),
                    cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_WORD)),
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_PLAYER))),
                    (cursor.getInt(cursor.getColumnIndex(WordTable.COLUMN_VALID)) == 1),   // Convert to bool.  If 1, true. If not 1, false
                    (cursor.getInt(cursor.getColumnIndex(WordTable.COLUMN_FINAL)) == 1),
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_GAME_ID)))
            );
            words.add(word);
        }
        cursor.close();
        return words;
    }

    public List<WordItem> getValidWords(UUID player) {
        List<WordItem> words = new ArrayList<>();
        String whereClause = WordTable.COLUMN_PLAYER + " = '" + player.toString() + "' AND " + WordTable.COLUMN_VALID + " = 1";
        Cursor cursor = wfDatabase.query(WordTable.TABLE_WORDS, WordTable.ALL_COLUMNS, whereClause, null, null, null, null);

        while (cursor.moveToNext()) {
            WordItem word = new WordItem(
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_ID))),
                    cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_WORD)),
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_PLAYER))),
                    (cursor.getInt(cursor.getColumnIndex(WordTable.COLUMN_VALID)) == 1),   // Convert to bool.  If 1, true. If not 1, false
                    (cursor.getInt(cursor.getColumnIndex(WordTable.COLUMN_FINAL)) == 1),
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_GAME_ID)))
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
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(RoundTable.COLUMN_ID))),
                    cursor.getString(cursor.getColumnIndex(RoundTable.COLUMN_LETTERS)),
                    cursor.getString(cursor.getColumnIndex(RoundTable.COLUMN_LONGEST_POSSIBLE))
            );
            rounds.add(round);
        }
        cursor.close();
        return rounds;
    }

    public RoundItem getRound(UUID rID) {
        RoundItem round;
        Cursor cursor = wfDatabase.query(RoundTable.TABLE_ROUNDS, RoundTable.ALL_COLUMNS, RoundTable.COLUMN_ID + " = '" + rID.toString() + "'", null, null, null, null);

        // If the app is killed before the round finishes, the round might not be recorded even though the submitted words will be.
        if (cursor.getCount() == 0) {
            return null;
        } else {
            cursor.moveToNext();
            round = new RoundItem(
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(RoundTable.COLUMN_ID))),
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
                UUID.fromString(cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_ID))),
                cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_WORD)),
                UUID.fromString(cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_PLAYER))),
                (cursor.getInt(cursor.getColumnIndex(WordTable.COLUMN_VALID)) == 1),   // Convert to bool.  If 1, true. If not 1, false
                (cursor.getInt(cursor.getColumnIndex(WordTable.COLUMN_FINAL)) == 1),
                UUID.fromString(cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_GAME_ID)))
        );
        cursor.close();
        return word;
    }

    public List<WordItem> getWordsByRound(UUID rID) {
        List<WordItem> words = new ArrayList<>();
        Cursor cursor = wfDatabase.query(WordTable.TABLE_WORDS, WordTable.ALL_COLUMNS, WordTable.COLUMN_GAME_ID + " = '" + rID.toString() + "'", null, null, null, null);

        while (cursor.moveToNext()) {
            WordItem word = new WordItem(
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_ID))),
                    cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_WORD)),
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_PLAYER))),
                    (cursor.getInt(cursor.getColumnIndex(WordTable.COLUMN_VALID)) == 1),   // Convert to bool.  If 1, true. If not 1, false
                    (cursor.getInt(cursor.getColumnIndex(WordTable.COLUMN_FINAL)) == 1),
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(WordTable.COLUMN_GAME_ID)))
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
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_R1_ID))),
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_R2_ID))),
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_R3_ID))),
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

    @Nullable
    public GameItem getGame(String gameId) {
        GameItem game;
        Cursor cursor = wfDatabase.query(GameTable.TABLE_GAMES, GameTable.ALL_COLUMNS, GameTable.COLUMN_R1_ID + " = '" + gameId + "'", null, null, null, null);
        if (gameId.equals("")) {
            return null;        // TODO: Throw exception
        }
        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        } else {
            cursor.moveToNext();
            game = new GameItem(
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_R1_ID))),
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_R2_ID))),
                    UUID.fromString(cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_R3_ID))),
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_W1_ID)),
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_W2_ID)),
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_W3_ID)),
                    cursor.getString(cursor.getColumnIndex(GameTable.COLUMN_WINNER)),
                    cursor.getInt(cursor.getColumnIndex(GameTable.COLUMN_PLAYER_COUNT))
            );
        }
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
