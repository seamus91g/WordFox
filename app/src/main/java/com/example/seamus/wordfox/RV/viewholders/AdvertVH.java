package com.example.seamus.wordfox.RV.viewholders;

import android.view.View;
import android.widget.FrameLayout;

import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.RV.BaseWFViewHolder;
import com.example.seamus.wordfox.RV.DataListItem;
import com.example.seamus.wordfox.RV.RVTypes.TypeAdvert;

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
