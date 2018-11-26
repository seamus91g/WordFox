package capsicum.game.wordfox.screen_rank_detail;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import capsicum.game.wordfox.R;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.RankViewHolder> {

    private List<RankDetailItem> rankDataset;

    RankAdapter(List<RankDetailItem> rankDataset) {
        this.rankDataset = rankDataset;
    }

    @NonNull
    @Override
    public RankViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.rv_rank_fox, viewGroup, false);
        return new RankViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RankViewHolder rankViewHolder, int i) {
        rankViewHolder.onBind(rankDataset.get(i));
    }

    @Override
    public int getItemCount() {
        return rankDataset.size();
    }

    public class RankViewHolder extends RecyclerView.ViewHolder {
        private final ImageView foxIV;
        private final TextView rankHeaderTV;
        private final TextView rankValueTV;
        private final TextView rankCountTV;

        public RankViewHolder(@NonNull View itemView) {
            super(itemView);
            foxIV = itemView.findViewById(R.id.rv_rank_detail_fox);
            rankHeaderTV = itemView.findViewById(R.id.rv_rank_detail_header);
            rankValueTV = itemView.findViewById(R.id.rv_rank_detail_value);
            rankCountTV = itemView.findViewById(R.id.rv_rank_detail_times_achieved);
        }

        public void onBind(RankDetailItem rank) {
            String headerString;
            String requirementString;
            String timesFoundString;
            if (rank.isLocked) {
                headerString = "NOT UNLOCKED";
            } else {
                headerString = rank.rank.toUpperCase();
            }
            if (rank.scoreRequired == 0) {
                requirementString = "Complete all 3 rounds.";
            } else {
                requirementString = "Score at least " + rank.scoreRequired + " points.";
            }
            if (rank.timesFound == 1) {
                timesFoundString = "Achieved " + rank.timesFound + " time!";
            } else {
                timesFoundString = "Achieved " + rank.timesFound + " times!";
            }
            foxIV.setImageBitmap(rank.foxBmp);
            rankHeaderTV.setText(headerString);
            rankValueTV.setText(requirementString);
            rankCountTV.setText(timesFoundString);
        }
    }
}
