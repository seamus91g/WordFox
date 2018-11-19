package capsicum.game.wordfox.RV;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import capsicum.game.wordfox.R;
import capsicum.game.wordfox.RV.viewholders.AdvertVH;
import capsicum.game.wordfox.RV.viewholders.CategoryHeadingVH;
import capsicum.game.wordfox.RV.viewholders.GameDetailsVH;
import capsicum.game.wordfox.RV.viewholders.GameHeadingVH;
import capsicum.game.wordfox.RV.viewholders.HeadingImageVH;
import capsicum.game.wordfox.RV.viewholders.PlayerHeadingVH;
import capsicum.game.wordfox.RV.viewholders.StatItemVH;
import capsicum.game.wordfox.RV.viewholders.WordDetailsVH;
import capsicum.game.wordfox.RV.viewholders.WordHeaderVH;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gilroy
 * Adapter used to display all data in a recycler view on the Data page.
 * The recycler view contains various different view types, some of which are expandable.
 */

public class WFAdapter extends RecyclerView.Adapter<BaseWFViewHolder> {
    private ArrayList<DataListItem> dataset;

    public interface OnItemClickListener {
        void onRecyclerItemSelected(int adapterPosition, android.view.View v);
    }

    public WFAdapter(ArrayList<DataListItem> dataset) {
        this.dataset = dataset;
    }

    // When an expandable item is clicked on, grab child views from the clicked on item and
    //  add them to the adapter dataset.
    // When an already expanded item is clicked on, count its number of children and remove
    //  exactly that many views from the dataset.
    private WFAdapter.OnItemClickListener clickListener = new OnItemClickListener() {
        @Override
        public void onRecyclerItemSelected(int adapterPosition, View view) {
            DataListItem item = dataset.get(adapterPosition);
            List<DataListItem> newDataSet = new ArrayList<>();
            if (item.isListExpanded()) {
                int countIncludingChildren = sumItemsTotal(item);
                newDataSet.addAll(dataset.subList(0, adapterPosition + 1));
                newDataSet.addAll(dataset.subList(adapterPosition + countIncludingChildren, dataset.size()));
                WFAdapter.this.swapItems(newDataSet);
            } else {
                dataset.addAll(adapterPosition + 1, item.getSubItems());
                WFAdapter.this.notifyItemRangeInserted(adapterPosition + 1, item.getSubItemCount());
                item.toggleExpanded();
            }
            WFAdapter.this.notifyItemChanged(adapterPosition);      // So the drop down arrow gets flipped
        }
    };

    // TODO: This logic does not belong in adapter. Move to DataListItem
    // Cound child items of a view
    // Recursively searches for and includes child items of child items
    private int sumItemsTotal(DataListItem item) {
        int count = 0;
        if (item.getSubItemCount() > 0 && item.isListExpanded()) {
            ++count;
            for (DataListItem child : item.getSubItems()) {
                count += sumItemsTotal(child);
            }
            item.toggleExpanded();
        } else {
            return 1;
        }
        return count;
    }

    // Swap out the old dataset for a new one. Used when expanding/contracting items
    // Calculates the diff of the two lists first so a more efficient swap can occur
    // Calculating the diff first also causes the changes to be visually animated smoothly
    private void swapItems(List<DataListItem> items) {
        final WordsDiffCallback diff = new WordsDiffCallback(this.dataset, items);
        final DiffUtil.DiffResult result = DiffUtil.calculateDiff(diff);
        this.dataset.clear();
        this.dataset.addAll(items);
        result.dispatchUpdatesTo(this);
    }

    // Identify the dataset item and proceed to create the appropriate view holder
    @Override
    public BaseWFViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v;
        switch (viewType) {
            case DataListItem.PLAYER:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_data_player_header, parent, false);
                // NOTE: Seems to be a bug in recyclerview causing the view to wrap content.
                // Below line will force the width to equal parent width
                v.setLayoutParams(new RecyclerView.LayoutParams(
                        ((RecyclerView) parent).getLayoutManager().getWidth(),
                        RecyclerView.LayoutParams.WRAP_CONTENT));
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
                return new GameDetailsVH(v);
            case DataListItem.WORDS_DETAIL:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_data_word_details, parent, false);
                return new WordDetailsVH(v);
            case DataListItem.ADVERT:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_data_advert, parent, false);
                return new AdvertVH(v);
            case DataListItem.HEADING_IMAGE:
                v = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.rv_heading_image, parent, false);
                return new HeadingImageVH(v);
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
