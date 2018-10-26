package com.example.seamus.wordfox.RV.RVTypes;

import android.graphics.Bitmap;
import android.util.Log;

import com.example.seamus.wordfox.RV.DataListItem;

import java.util.List;
import java.util.UUID;

public class TypeHeadingImage implements DataListItem {
    private UUID ID;
    private Bitmap headingImage;

    public TypeHeadingImage(Bitmap headingImage) {
        ID = UUID.randomUUID();
        Log.d("myTag", " Creating TypeHeadingImage. ");
        this.headingImage = headingImage;
        Log.d("myTag", "@ Height: " + headingImage.getHeight());
    }

    public Bitmap getHeadingImage() {
        return headingImage;
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
