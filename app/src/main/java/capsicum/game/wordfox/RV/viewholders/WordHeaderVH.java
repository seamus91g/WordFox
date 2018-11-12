package capsicum.game.wordfox.RV.viewholders;

import android.view.View;
import android.widget.TextView;
import capsicum.game.wordfox.R;
import capsicum.game.wordfox.RV.BaseWFViewHolder;
import capsicum.game.wordfox.RV.DataListItem;
import capsicum.game.wordfox.RV.RVTypes.TypeWordsHeader;
import capsicum.game.wordfox.RV.WFAdapter;

public class WordHeaderVH extends BaseWFViewHolder {
    private final TextView wordTV;
    private final TextView timesFoundTV;

    public WordHeaderVH(View wordHeaderView, WFAdapter.OnItemClickListener listener) {
        super(wordHeaderView);
        wordTV = wordHeaderView.findViewById(R.id.player_word);
        timesFoundTV = wordHeaderView.findViewById(R.id.times_found);
        wordHeaderView.setOnClickListener(v -> listener.onRecyclerItemSelected(getAdapterPosition(), v));
    }

    @Override
    public void bindType(DataListItem item) {
        super.setSelected(item.isListExpanded());
        TypeWordsHeader wordHeader = (TypeWordsHeader) item;
        String word = wordHeader.getWord() + " (" + wordHeader.getWord().length() + ")";
        wordTV.setText(word);
        String timesFound = "x " + wordHeader.getTimesFound();
        timesFoundTV.setText(timesFound);
    }
}
