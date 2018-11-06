package com.example.seamus.wordfox.RV.viewholders;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.RV.BaseWFViewHolder;
import com.example.seamus.wordfox.RV.DataListItem;
import com.example.seamus.wordfox.RV.RVTypes.TypeHeadingImage;
import com.example.seamus.wordfox.RV.RVTypes.TypeWordsDetail;

public class HeadingImageVH extends BaseWFViewHolder {
    private final ImageView headingImageIV;
    private final ImageView speechBubbleImageIV;

    public HeadingImageVH(View v) {
        super(v);
        Log.d("myTag", "Creating HeadingImageVH ");
        headingImageIV = v.findViewById(R.id.datafox_stats_heading_image);
        speechBubbleImageIV = v.findViewById(R.id.data_heading_image_speech_bubble);
    }

    @Override
    public void bindType(DataListItem item) {
        TypeHeadingImage headingImage = (TypeHeadingImage) item;
        headingImageIV.setImageBitmap(headingImage.getHeadingImage());
        speechBubbleImageIV.setImageBitmap(headingImage.getSpeechBubble());
    }
}
