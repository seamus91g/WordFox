package capsicum.game.wordfox.database;

/**
 * Created by Gilroy
 */

public class OpponentTable {
    public static final String TABLE_OPPONENTS = "opponents";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_OPPONENT_NAME = "opponent_name";
    public static final String COLUMN_WINS = "wins";
    public static final String COLUMN_LOSES = "loses";
    public static final String COLUMN_DRAWS = "draws";

    public static final String[] ALL_COLUMNS = {
            COLUMN_NAME, COLUMN_OPPONENT_NAME, COLUMN_WINS, COLUMN_LOSES, COLUMN_DRAWS
    };

    // Create an SQL table for storing information on each game round
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_OPPONENTS + "(" +
                    COLUMN_NAME + " TEXT," +
                    COLUMN_OPPONENT_NAME + " TEXT," +
                    COLUMN_WINS + " INTEGER," +
                    COLUMN_LOSES + " INTEGER," +
                    COLUMN_DRAWS + " INTEGER," +
                    "primary key (" + COLUMN_NAME + "," + COLUMN_OPPONENT_NAME + ")" +
                    ");";
    // Delete SQL table containing round information
    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_OPPONENTS;
}
