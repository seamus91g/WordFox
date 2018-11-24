package capsicum.game.wordfox.recyclerview_game_data.viewholders;

import android.view.View;
import android.widget.FrameLayout;

import capsicum.game.wordfox.R;
import capsicum.game.wordfox.recyclerview_game_data.BaseWFViewHolder;
import capsicum.game.wordfox.recyclerview_game_data.DataListItem;
import capsicum.game.wordfox.recyclerview_game_data.RVTypes.TypeAdvert;

public class AdvertVH extends BaseWFViewHolder {
    private FrameLayout fl;

    public AdvertVH(View v) {
        super(v);
        fl = v.findViewById(R.id.adViewLL);
    }

    @Override
    public void bindType(DataListItem item) {
        TypeAdvert advert = (TypeAdvert) item;
        if(advert.getAdView().getParent() == null){
            fl.addView(advert.getAdView());
        }
    }
}
