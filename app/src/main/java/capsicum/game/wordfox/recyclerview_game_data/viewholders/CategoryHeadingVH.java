package capsicum.game.wordfox.recyclerview_game_data.viewholders;

import android.view.View;
import android.widget.TextView;
import capsicum.game.wordfox.R;
import capsicum.game.wordfox.recyclerview_game_data.BaseWFViewHolder;
import capsicum.game.wordfox.recyclerview_game_data.DataListItem;
import capsicum.game.wordfox.recyclerview_game_data.RVTypes.TypeCategory;
import capsicum.game.wordfox.recyclerview_game_data.WFAdapter;

public class CategoryHeadingVH extends BaseWFViewHolder {
    private final TextView categoryTitleTV;

    public CategoryHeadingVH(View categoryHeaderView, WFAdapter.OnItemClickListener listener) {
        super(categoryHeaderView);
        categoryTitleTV = categoryHeaderView.findViewById(R.id.data_category);
        categoryHeaderView.setOnClickListener(v -> listener.onRecyclerItemSelected(getAdapterPosition(), v));
    }

    @Override
    public void bindType(DataListItem item) {
        super.setSelected(item.isListExpanded());
        TypeCategory categoryHeader = (TypeCategory) item;
        categoryTitleTV.setText(categoryHeader.getCategoryTitle());
    }
}
