package com.example.seamus.wordfox.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.seamus.wordfox.HomeScreen;

/**
 * Created by spgilroy on 11/20/2017.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_FILE_NAME = "wordfox.db";
    public static final int DB_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(HomeScreen.MONITOR_TAG, "onCreate wf DBHelper!!, END");
        sqLiteDatabase.execSQL(GameTable.SQL_CREATE);
        sqLiteDatabase.execSQL(WordTable.SQL_CREATE);
        sqLiteDatabase.execSQL(RoundTable.SQL_CREATE);
        sqLiteDatabase.execSQL(OpponentTable.SQL_CREATE);
        sqLiteDatabase.execSQL(PlayerStatsTable.SQL_CREATE);
        Log.d(HomeScreen.MONITOR_TAG, "Finished onCreate wf DBHelper!!, END");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
        Log.d(HomeScreen.MONITOR_TAG, "onUpgrade DBHelper!!, END");
        sqLiteDatabase.execSQL(GameTable.SQL_DELETE);
        sqLiteDatabase.execSQL(WordTable.SQL_DELETE);
        sqLiteDatabase.execSQL(RoundTable.SQL_DELETE);
        sqLiteDatabase.execSQL(OpponentTable.SQL_DELETE);
        sqLiteDatabase.execSQL(PlayerStatsTable.SQL_DELETE);
        onCreate(sqLiteDatabase);
    }
}
