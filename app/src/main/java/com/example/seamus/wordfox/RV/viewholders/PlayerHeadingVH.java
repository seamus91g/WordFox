package com.example.seamus.wordfox.RV.viewholders;

import android.view.View;
import android.widget.TextView;

import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.RV.BaseWFViewHolder;
import com.example.seamus.wordfox.RV.DataListItem;
import com.example.seamus.wordfox.RV.RVTypes.TypePlayer;
import com.example.seamus.wordfox.RV.WFAdapter;

public class PlayerHeadingVH extends BaseWFViewHolder {
    private final TextView playerNameTV;

    public PlayerHeadingVH(View playerHeaderView, WFAdapter.OnItemClickListener listener) {
        super(playerHeaderView);
        playerNameTV = playerHeaderView.findViewById(R.id.data_player_name);
        playerHeaderView.setOnClickListener(v -> listener.onRecyclerItemSelected(getAdapterPosition(), v));
    }

    @Override
    public void bindType(DataListItem item) {
        TypePlayer playerHeader = (TypePlayer) item;
        playerNameTV.setText(playerHeader.getPlayer());
    }
}
