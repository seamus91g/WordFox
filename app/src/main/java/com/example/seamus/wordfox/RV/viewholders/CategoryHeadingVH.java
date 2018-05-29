package com.example.seamus.wordfox.RV.viewholders;

import android.view.View;
import android.widget.TextView;

import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.RV.BaseWFViewHolder;
import com.example.seamus.wordfox.RV.DataListItem;
import com.example.seamus.wordfox.RV.RVTypes.TypeCategory;
import com.example.seamus.wordfox.RV.RVTypes.TypePlayer;
import com.example.seamus.wordfox.RV.WFAdapter;

public class CategoryHeadingVH extends BaseWFViewHolder {
    private final TextView categoryTitleTV;

    public CategoryHeadingVH(View categoryHeaderView, WFAdapter.OnItemClickListener listener) {
        super(categoryHeaderView);
        categoryTitleTV = categoryHeaderView.findViewById(R.id.data_category);
        categoryHeaderView.setOnClickListener(v -> listener.onRecyclerItemSelected(getAdapterPosition(), v));
    }

    @Override
    public void bindType(DataListItem item) {
        TypeCategory categoryHeader = (TypeCategory) item;
        categoryTitleTV.setText(categoryHeader.getCategoryTitle());
    }
}
