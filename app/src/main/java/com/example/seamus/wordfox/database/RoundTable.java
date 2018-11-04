package com.example.seamus.wordfox.database;

/**
 * Created by Gilroy
 */

public class RoundTable {
    public static final String TABLE_ROUNDS = "rounds";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_LETTERS = "letters";
    public static final String COLUMN_LONGEST_POSSIBLE = "longest_possible";

    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_LETTERS, COLUMN_LONGEST_POSSIBLE
    };
    // Create an SQL table for storing information on each game round
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_ROUNDS + "(" +
                    COLUMN_ID + " TEXT PRIMARY KEY," +
                    COLUMN_LETTERS + " TEXT," +
                    COLUMN_LONGEST_POSSIBLE + " TEXT" + ");";
    // Delete SQL table containing round information
    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_ROUNDS;
}
