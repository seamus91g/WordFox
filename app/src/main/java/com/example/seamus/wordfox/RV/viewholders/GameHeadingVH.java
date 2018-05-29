package com.example.seamus.wordfox.RV.viewholders;

import android.view.View;
import android.widget.TextView;

import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.RV.BaseWFViewHolder;
import com.example.seamus.wordfox.RV.DataListItem;
import com.example.seamus.wordfox.RV.RVTypes.TypeGamesHeader;
import com.example.seamus.wordfox.RV.WFAdapter;

public class GameHeadingVH extends BaseWFViewHolder {
    private final TextView gameHeaderTV;

    public GameHeadingVH(View gameHeaderView, WFAdapter.OnItemClickListener listener) {
        super(gameHeaderView);
        gameHeaderTV = gameHeaderView.findViewById(R.id.data_game_header);
        gameHeaderView.setOnClickListener(v -> listener.onRecyclerItemSelected(getAdapterPosition(), v));
    }

    @Override
    public void bindType(DataListItem item) {
        TypeGamesHeader categoryHeader = (TypeGamesHeader) item;
        String winString = "Winner:  " +  categoryHeader.getGameWinner() + "  (" + categoryHeader.getWinnerScore() + ")";
        gameHeaderTV.setText(winString);
    }
}
