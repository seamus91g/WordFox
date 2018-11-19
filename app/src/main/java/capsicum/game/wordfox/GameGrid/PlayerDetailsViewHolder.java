package capsicum.game.wordfox.GameGrid;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import capsicum.game.wordfox.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerDetailsViewHolder extends PlayerResultViewHolder {
    private final CircleImageView profilePicView;
    private final TextView resultPlayerScoreView;
    private final TextView resultPlayerNameView;
    private final ImageView resultPlayerFoxPicView;
    private final TextView resultPlayerRankNameView;
    private int maxScore;

    PlayerDetailsViewHolder(@NonNull View view, int maxScore) {
        super(view);
        this.profilePicView = view.findViewById(R.id.results_screen_profile_pic);
        this.resultPlayerScoreView = view.findViewById(R.id.result_player_score);
        this.resultPlayerNameView = view.findViewById(R.id.result_player_name);
        this.resultPlayerFoxPicView = view.findViewById(R.id.result_player_fox_pic);
        this.resultPlayerRankNameView = view.findViewById(R.id.result_player_rank_name);
        this.maxScore = maxScore;
    }

    @Override
    public void onBind(PlayerResultListItem playerListItem) {
        TypePlayerResultDetails playerResultDetails = (TypePlayerResultDetails) playerListItem;
        String scoreString = playerResultDetails.score + "/" + maxScore + " (" + (playerResultDetails.score * 100 / maxScore) + "%)";
        resultPlayerScoreView.setText(scoreString);
        resultPlayerNameView.setText(playerResultDetails.name);
        resultPlayerRankNameView.setText(playerResultDetails.rank);
        profilePicView.setImageBitmap(playerResultDetails.profilePic);
        resultPlayerFoxPicView.setImageBitmap(playerResultDetails.rankPic);
    }
}
