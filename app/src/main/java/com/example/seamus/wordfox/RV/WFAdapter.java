package com.example.seamus.wordfox.RV;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.RV.viewholders.CategoryHeadingVH;
import com.example.seamus.wordfox.RV.viewholders.GameDetailsVH;
import com.example.seamus.wordfox.RV.viewholders.GameHeadingVH;
import com.example.seamus.wordfox.RV.viewholders.PlayerHeadingVH;
import com.example.seamus.wordfox.RV.viewholders.StatItemVH;
import com.example.seamus.wordfox.RV.viewholders.WordDetailsVH;
import com.example.seamus.wordfox.RV.viewholders.WordHeaderVH;

import java.util.ArrayList;
import java.util.List;

public class WFAdapter extends RecyclerView.Adapter<BaseWFViewHolder> {

    private static final String TAG = "WFAdapter";
    private ArrayList<DataListItem> dataset;

    //        private OnItemClickListener clickListener;
    public interface OnItemClickListener {
        void onRecyclerItemSelected(int adapterPosition, android.view.View v);
    }

    private WFAdapter.OnItemClickListener clickListener = new OnItemClickListener() {
        @Override
        public void onRecyclerItemSelected(int adapterPosition, View view) {
            DataListItem item = dataset.get(adapterPosition);

            if (item.getListItemType() == DataListItem.WORDS_HEADER ||
                    item.getListItemType() == DataListItem.GAMES_HEADER ||
                    item.getListItemType() == DataListItem.PLAYER ||
                    item.getListItemType() == DataListItem.CATEGORY) {
                Log.d(TAG, "== Clicked Header ==");
                List<DataListItem> newDataSet = new ArrayList<>();
                if (item.isListExpanded()) {
//                    int subCount = item.getSubItemCount();
                    int countIncludingChildren = sumItemsTotal(item);
                    newDataSet.addAll(dataset.subList(0, adapterPosition + 1));
                    newDataSet.addAll(dataset.subList(adapterPosition + countIncludingChildren, dataset.size()));
                    WFAdapter.this.swapItems(newDataSet);
                } else {
                    dataset.addAll(adapterPosition + 1, item.getSubItems());
                    WFAdapter.this.notifyItemRangeInserted(adapterPosition + 1, item.getSubItemCount());
                    item.toggleExpanded();
                }
            }
        }
    };

    // If child count > 0 && is expanded, recurse
    // Add child count
    // Toggle expanded
    // return
    // TODO: This logic does not belong in adapter. Move to DataListItem
    public int sumItemsTotal(DataListItem item) {
        int count = 0;
        if (item.getSubItemCount() > 0 && item.isListExpanded()) {
            ++count;
            for (DataListItem child : item.getSubItems()) {
                count += sumItemsTotal(child);
            }
            item.toggleExpanded();
        }else{
            return 1;
        }
        return count;
    }

    public WFAdapter(ArrayList<DataListItem> dataset) {
        this.dataset = dataset;
    }

    public void swapItems(List<DataListItem> words) {
        final WordsDiffCallback diff = new WordsDiffCallback(this.dataset, words);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(diff);

        this.dataset.clear();
        this.dataset.addAll(words);
        result.dispatchUpdatesTo(this);
    }

    @Override
    public BaseWFViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case DataListItem.PLAYER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_data_player_header, parent, false);
                return new PlayerHeadingVH(v, clickListener);
            case DataListItem.CATEGORY:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_data_category_header, parent, false);
                return new CategoryHeadingVH(v, clickListener);
            case DataListItem.GAMES_HEADER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_data_games_header, parent, false);
                return new GameHeadingVH(v, clickListener);
            case DataListItem.WORDS_HEADER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_data_word_header, parent, false);
                return new WordHeaderVH(v, clickListener);
            case DataListItem.STATS:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_data_stat_item, parent, false);
                return new StatItemVH(v);
            case DataListItem.GAMES_DETAIL:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_data_game_details, parent, false);
                // NOTE: Seems to be a bug in recyclerview causing the view to wrap content.
                // Below line will force the width to equal parent width
                v.setLayoutParams(new RecyclerView.LayoutParams(
                        ((RecyclerView) parent).getLayoutManager().getWidth(),
                        RecyclerView.LayoutParams.WRAP_CONTENT));
                return new GameDetailsVH(v);
            case DataListItem.WORDS_DETAIL:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_data_word_details, parent, false);
                return new WordDetailsVH(v);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return dataset.get(position).getListItemType();
    }

    @Override
    public void onBindViewHolder(BaseWFViewHolder holder, int position) {
        holder.bindType(dataset.get(position));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }
}
