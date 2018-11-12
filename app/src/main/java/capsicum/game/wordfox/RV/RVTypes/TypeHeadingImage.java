package capsicum.game.wordfox.RV.RVTypes;

import android.graphics.Bitmap;
import android.util.Log;

import capsicum.game.wordfox.RV.DataListItem;

import java.util.List;
import java.util.UUID;

public class TypeHeadingImage implements DataListItem {
    private UUID ID;
    private Bitmap headingImage;
    private Bitmap speechBubble;

    public TypeHeadingImage(Bitmap headingImage, Bitmap speechBubble) {
        this.speechBubble = speechBubble;
        this.headingImage = headingImage;
        ID = UUID.randomUUID();
    }

    public Bitmap getHeadingImage() {
        return headingImage;
    }

    public Bitmap getSpeechBubble() {
        return speechBubble;
    }

    @Override
    public boolean isListExpanded() {
        return false;
    }

    @Override
    public void toggleExpanded() {

    }

    @Override
    public int getListItemType() {
        return DataListItem.HEADING_IMAGE;
    }

    @Override
    public List<DataListItem> getSubItems() {
        return null;
    }

    @Override
    public int getSubItemCount() {
        return 0;
    }

    @Override
    public UUID getID() {
        return ID;
    }

}