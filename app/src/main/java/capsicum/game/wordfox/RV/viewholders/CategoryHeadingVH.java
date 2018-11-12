package capsicum.game.wordfox.RV.viewholders;

import android.view.View;
import android.widget.TextView;
import capsicum.game.wordfox.R;
import capsicum.game.wordfox.RV.BaseWFViewHolder;
import capsicum.game.wordfox.RV.DataListItem;
import capsicum.game.wordfox.RV.RVTypes.TypeCategory;
import capsicum.game.wordfox.RV.WFAdapter;

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
