package capsicum.game.wordfox.datamodels;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import capsicum.game.wordfox.database.ImageTable;

/**
 * Created by Gilroy
 */

public class ImageItem {
    private final UUID playerId;
    private final String imageId;
    private final Bitmap image;

    public ImageItem(UUID playerId, String imageId, Bitmap image) {
        if (playerId == null) {
            playerId = UUID.randomUUID();
        }
        this.playerId = playerId;
        this.imageId = imageId;
        this.image = image;
    }

    public ImageItem(UUID playerId, String letters, byte[] image) {
        this(playerId, letters, getImageFromByteArray(image));
    }

    public ContentValues toValues() {
        ContentValues values = new ContentValues(3);
        values.put(ImageTable.COLUMN_PLAYER_ID, playerId.toString());
        values.put(ImageTable.COLUMN_IMAGE_ID, imageId);
        values.put(ImageTable.COLUMN_IMAGE_BLOB, bitmapToByteArray(image));
        return values;
    }

    public static Bitmap getImageFromByteArray(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }

    public UUID getPlayerId() {
        return playerId;
    }

    public String getImageId() {
        return imageId;
    }

    public Bitmap getImage() {
        return image;
    }
}
