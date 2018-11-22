package capsicum.game.wordfox.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import capsicum.game.wordfox.HomeScreen;
import capsicum.game.wordfox.WordfoxConstants;
import timber.log.Timber;

/**
 * Created by spgilroy
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String DB_FILE_NAME = "wordfox.db";
    public static final int DB_VERSION = 2;

    public DBHelper(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(GameTable.SQL_CREATE);
        sqLiteDatabase.execSQL(WordTable.SQL_CREATE);
        sqLiteDatabase.execSQL(RoundTable.SQL_CREATE);
        sqLiteDatabase.execSQL(OpponentTable.SQL_CREATE);
        sqLiteDatabase.execSQL(PlayerStatsTable.SQL_CREATE);
        sqLiteDatabase.execSQL(ImageTable.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
        sqLiteDatabase.execSQL(GameTable.SQL_DELETE_IF_EXIST);
        sqLiteDatabase.execSQL(WordTable.SQL_DELETE_IF_EXIST);
        sqLiteDatabase.execSQL(RoundTable.SQL_DELETE_IF_EXIST);
        sqLiteDatabase.execSQL(OpponentTable.SQL_DELETE_IF_EXIST);
        sqLiteDatabase.execSQL(PlayerStatsTable.SQL_DELETE_IF_EXIST);
        sqLiteDatabase.execSQL(ImageTable.SQL_DELETE_IF_EXIST);
        onCreate(sqLiteDatabase);
    }
}
