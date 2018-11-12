package capsicum.game.wordfox.RV.viewholders;

import android.view.View;
import android.widget.TextView;

import capsicum.game.wordfox.R;
import capsicum.game.wordfox.RV.BaseWFViewHolder;
import capsicum.game.wordfox.RV.DataListItem;
import capsicum.game.wordfox.RV.RVTypes.TypeStats;

public class StatItemVH extends BaseWFViewHolder {
    private final TextView statItemTV;
    private final TextView statItemQuantityTV;

    public StatItemVH(View statItemView) {
        super(statItemView);
        statItemTV = statItemView.findViewById(R.id.data_stat_item_description);
        statItemQuantityTV = statItemView.findViewById(R.id.data_stat_item_quantity);

    }

    @Override
    public void bindType(DataListItem item) {
        TypeStats statItem = (TypeStats) item;
        statItemTV.setText(statItem.getStatistic());
        statItemQuantityTV.setText(String.valueOf(statItem.getStatValue()));
    }
}
