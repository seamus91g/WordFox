package capsicum.game.wordfox.RV.viewholders;

import android.view.View;
import android.widget.FrameLayout;

import capsicum.game.wordfox.R;
import capsicum.game.wordfox.RV.BaseWFViewHolder;
import capsicum.game.wordfox.RV.DataListItem;
import capsicum.game.wordfox.RV.RVTypes.TypeAdvert;

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
