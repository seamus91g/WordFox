package capsicum.game.wordfox.datamodels;

import android.content.ContentValues;
import android.graphics.Bitmap;
import java.util.UUID;
import capsicum.game.wordfox.ImageHandler;
import capsicum.game.wordfox.WordfoxConstants;
import capsicum.game.wordfox.database.ImageTable;

public class ProfileImageItem extends ImageItem {
    public static final String PROFILE_PICTURE_ID = "player_profile_picture";
    public static final String PROFILE_PICTURE_ID_SMALL = "player_profile_picture_small";
    private final UUID playerId;
    private final Bitmap image;
    int maxDimension;

    public ProfileImageItem(UUID playerId, Bitmap image, int screenWidth) {
        super(playerId, PROFILE_PICTURE_ID, image);
        this.playerId = playerId;
        this.image = image;
        maxDimension = (int) (WordfoxConstants.PROFILE_ICON_SCREEN_WIDTH_PERCENT * screenWidth);
    }

    public ContentValues toValuesSmall() {
        ContentValues values = new ContentValues(3);
        values.put(ImageTable.COLUMN_PLAYER_ID, playerId.toString());
        values.put(ImageTable.COLUMN_IMAGE_ID, PROFILE_PICTURE_ID_SMALL);
        values.put(ImageTable.COLUMN_IMAGE_BLOB,
                ImageItem.bitmapToByteArray(
                        ImageHandler.cropToSquare(
                                ImageHandler.getResizedBitmapByShortest(image, maxDimension))));
        return values;
    }
}
