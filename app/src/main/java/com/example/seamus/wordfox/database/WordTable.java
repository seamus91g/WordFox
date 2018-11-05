package com.example.seamus.wordfox.database;

/**
 * Created by spgilroy
 */

public class WordTable {
    //////// Words submitted
    public static final String TABLE_WORDS = "words";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_WORD = "word";
    public static final String COLUMN_PLAYER = "player";
    public static final String COLUMN_VALID = "valid";
    public static final String COLUMN_FINAL = "final";
    public static final String COLUMN_GAME_ID = "round_id";

    public static final String[] ALL_COLUMNS = {
            COLUMN_ID, COLUMN_WORD, COLUMN_PLAYER, COLUMN_VALID, COLUMN_FINAL, COLUMN_GAME_ID
    };

    // Create an SQL table for storing information on each word found
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_WORDS + "(" +
                    COLUMN_ID + " TEXT PRIMARY KEY," +
                    COLUMN_WORD + " TEXT," +
                    COLUMN_PLAYER + " TEXT," +
                    COLUMN_VALID + " INTEGER," +            // Boolean
                    COLUMN_FINAL + " INTEGER," +            // Boolean
                    COLUMN_GAME_ID + " TEXT" + ");";
    // Delete SQL table containing word information
    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_WORDS;
}
