package capsicum.game.wordfox.database;

/**
 * Created by spgilroy
 */

public class GameTable {
    //////// Games played
    public static final String TABLE_GAMES = "games";
    public static final String COLUMN_R1_ID = "R1_id";
    public static final String COLUMN_R2_ID = "R2_id";
    public static final String COLUMN_R3_ID = "R3_id";
    public static final String COLUMN_W1_ID = "W1_id";
    public static final String COLUMN_W2_ID = "W2_id";
    public static final String COLUMN_W3_ID = "W3_id";
    public static final String COLUMN_WINNER = "winner";
    public static final String COLUMN_PLAYER_COUNT = "player_count";

    public static final String[] ALL_COLUMNS = {
            COLUMN_R1_ID, COLUMN_R2_ID, COLUMN_R3_ID, COLUMN_W1_ID, COLUMN_W2_ID, COLUMN_W3_ID, COLUMN_WINNER, COLUMN_PLAYER_COUNT
    };
    // Create an SQL table for storing information on each game
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_GAMES + "(" +
                    COLUMN_R1_ID + " TEXT," +
                    COLUMN_R2_ID + " TEXT," +
                    COLUMN_R3_ID + " TEXT," +
                    COLUMN_W1_ID + " TEXT," +
                    COLUMN_W2_ID + " TEXT," +
                    COLUMN_W3_ID + " TEXT," +
                    COLUMN_WINNER + " TEXT," +
                    COLUMN_PLAYER_COUNT + " INTEGER" + ");";
    // Delete SQL table containing game information
    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_GAMES;
}
