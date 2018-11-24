package capsicum.game.wordfox.recyclerview_game_data.viewholders;

import android.view.View;
import android.widget.ImageView;

import capsicum.game.wordfox.R;
import capsicum.game.wordfox.recyclerview_game_data.BaseWFViewHolder;
import capsicum.game.wordfox.recyclerview_game_data.DataListItem;
import capsicum.game.wordfox.recyclerview_game_data.RVTypes.TypeHeadingImage;

public class HeadingImageVH extends BaseWFViewHolder {
    private final ImageView headingImageIV;
    private final ImageView speechBubbleImageIV;

    public HeadingImageVH(View v) {
        super(v);
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
