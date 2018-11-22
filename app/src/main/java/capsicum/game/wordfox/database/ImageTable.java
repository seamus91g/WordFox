package capsicum.game.wordfox.database;

public class ImageTable {

    public static final String TABLE_IMAGES = "images";
    public static final String COLUMN_IMAGE_ID = "image_id";
    public static final String COLUMN_PLAYER_ID = "player_id";
    public static final String COLUMN_IMAGE_BLOB = "image_blob";

    public static final String[] ALL_COLUMNS = {
            COLUMN_IMAGE_ID, COLUMN_PLAYER_ID, COLUMN_IMAGE_BLOB
    };
    // Create an SQL table for storing information on image stored
    public static final String SQL_CREATE =
            "CREATE TABLE " + TABLE_IMAGES + "(" +
                    COLUMN_IMAGE_ID + " TEXT," +
                    COLUMN_PLAYER_ID + " TEXT," +
                    COLUMN_IMAGE_BLOB + " BLOB" + ");";
    // Delete SQL table containing image information
    public static final String SQL_DELETE =
            "DROP TABLE " + TABLE_IMAGES;
}
