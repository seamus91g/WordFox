package capsicum.game.wordfox.database;

/**
 * Created by Gilroy
 */

public class PlayerStatsTable {
    public static final String TABLE_PLAYER_STATS = "player_stats";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_WINS = "wins";
    public static final String COLUMN_LOSES = "loses";
    public static final String COLUMN_DRAWS = "draws";

    public static final String[] ALL_COLUMNS = {
            COLUMN_NAME, COLUMN_WINS, COLUMN_LOSES, COLUMN_DRAWS
    };
    public static final String[] SUM_GAMES = {
            "SUM(" + COLUMN_WINS + "+" + COLUMN_LOSES + "+" + COLUMN_DRAWS + ")"
    };

    // Create an SQL table for storing information on each game round
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_PLAYER_STATS + "(" +
                    COLUMN_NAME + " TEXT PRIMARY KEY," +
                    COLUMN_WINS + " INTEGER," +
                    COLUMN_LOSES + " INTEGER," +
                    COLUMN_DRAWS + " INTEGER" +
                    ");";
    // Delete SQL table containing round information
    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_PLAYER_STATS;
    public static final String SQL_DELETE_IF_EXIST =
            "DROP TABLE IF EXISTS '" + TABLE_PLAYER_STATS + "'";
}
