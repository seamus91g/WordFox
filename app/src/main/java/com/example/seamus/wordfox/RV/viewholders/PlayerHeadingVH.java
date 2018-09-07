package com.example.seamus.wordfox.RV.viewholders;

import android.view.View;
import android.widget.TextView;
import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.RV.BaseWFViewHolder;
import com.example.seamus.wordfox.RV.DataListItem;
import com.example.seamus.wordfox.RV.RVTypes.TypePlayer;
import com.example.seamus.wordfox.RV.WFAdapter;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerHeadingVH extends BaseWFViewHolder {
    private final CircleImageView playerPicIV;
    private final TextView playerNameTV;
    private final TextView playerRankTV;

    public PlayerHeadingVH(View playerHeaderView, WFAdapter.OnItemClickListener listener) {
        super(playerHeaderView);
        playerPicIV = playerHeaderView.findViewById(R.id.data_page_player_pic);
        playerNameTV = playerHeaderView.findViewById(R.id.data_player_name);
        playerRankTV = playerHeaderView.findViewById(R.id.data_player_rank);
        playerHeaderView.setOnClickListener(v -> listener.onRecyclerItemSelected(getAdapterPosition(), v));
    }

    @Override
    public void bindType(DataListItem item) {
        super.setSelected(item.isListExpanded());
        TypePlayer playerHeader = (TypePlayer) item;
        playerPicIV.setImageBitmap(playerHeader.getProfPic());
        playerNameTV.setText(playerHeader.getPlayer());
        playerRankTV.setText(playerHeader.getRank());
    }
}
