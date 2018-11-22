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
    private final boolean softUpgrade;

    public DBHelper(Context context) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
        this.softUpgrade = false;
    }

    public DBHelper(Context context, boolean softUpgrade) {
        super(context, DB_FILE_NAME, null, DB_VERSION);
        this.softUpgrade = softUpgrade;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        if (!softUpgrade) {
            sqLiteDatabase.execSQL(GameTable.SQL_CREATE);
            sqLiteDatabase.execSQL(WordTable.SQL_CREATE);
            sqLiteDatabase.execSQL(RoundTable.SQL_CREATE);
            sqLiteDatabase.execSQL(OpponentTable.SQL_CREATE);
            sqLiteDatabase.execSQL(PlayerStatsTable.SQL_CREATE);
        }
        sqLiteDatabase.execSQL(ImageTable.SQL_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVer, int newVer) {
        if (!softUpgrade) {
            sqLiteDatabase.execSQL(GameTable.SQL_DELETE);
            sqLiteDatabase.execSQL(WordTable.SQL_DELETE);
            sqLiteDatabase.execSQL(RoundTable.SQL_DELETE);
            sqLiteDatabase.execSQL(OpponentTable.SQL_DELETE);
            sqLiteDatabase.execSQL(PlayerStatsTable.SQL_DELETE);
            sqLiteDatabase.execSQL(ImageTable.SQL_DELETE);
        }
        onCreate(sqLiteDatabase);
    }
}
