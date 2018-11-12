package capsicum.game.wordfox.RV.viewholders;

import android.view.View;
import android.widget.TextView;

import capsicum.game.wordfox.PlayerIdentity;
import capsicum.game.wordfox.R;
import capsicum.game.wordfox.RV.BaseWFViewHolder;
import capsicum.game.wordfox.RV.DataListItem;
import capsicum.game.wordfox.RV.RVTypes.TypeGamesDetail;
import capsicum.game.wordfox.RV.RVTypes.TypeGamesHeader;
import capsicum.game.wordfox.RV.WFAdapter;

import java.util.ArrayList;

public class GameHeadingVH extends BaseWFViewHolder {
    private final TextView gameHeaderTV;

    public GameHeadingVH(View gameHeaderView, WFAdapter.OnItemClickListener listener) {
        super(gameHeaderView);
        gameHeaderTV = gameHeaderView.findViewById(R.id.data_game_header);
        gameHeaderView.setOnClickListener(v -> listener.onRecyclerItemSelected(getAdapterPosition(), v));
    }

    @Override
    public void bindType(DataListItem item) {
        super.setSelected(item.isListExpanded());
        TypeGamesHeader categoryHeader = (TypeGamesHeader) item;
        String winnerString = makeWinnerString(categoryHeader.getGameWinner());
        String winString;
        if (categoryHeader.isJustMe()) {
            winString = "Just me (" + categoryHeader.getWinnerScore() + ")";
        } else {
            winString = "Winner:  " + winnerString + "  (" + categoryHeader.getWinnerScore() + ")";
        }
        gameHeaderTV.setText(winString);
    }

    private String makeWinnerString(ArrayList<PlayerIdentity> gameWinner) {
        StringBuilder winStr = new StringBuilder();
        for (int i = 0; i < (gameWinner.size() - 1); ++i) {
            winStr.append(gameWinner.get(i).username);
            winStr.append(", ");
        }
        winStr.append(gameWinner.get(gameWinner.size() - 1).username);
        return winStr.toString();
    }
}