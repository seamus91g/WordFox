package capsicum.game.wordfox.RV.RVTypes;

import capsicum.game.wordfox.RV.DataListItem;
import com.google.android.gms.ads.AdView;

import java.util.List;
import java.util.UUID;

public class TypeAdvert implements DataListItem {
    private final UUID ID;
    private final AdView adView;

    public TypeAdvert(AdView adView) {
        ID = UUID.randomUUID();
        this.adView = adView;
    }

    public AdView getAdView() {
        return adView;
    }

    @Override
    public boolean isListExpanded() {
        return false;
    }

    @Override
    public void toggleExpanded() {
        return;
    }

    @Override
    public int getListItemType() {
        return DataListItem.ADVERT;
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
