package capsicum.game.wordfox.recyclerview_game_data.viewholders;

import android.view.View;
import android.widget.TextView;

import capsicum.game.wordfox.R;
import capsicum.game.wordfox.recyclerview_game_data.BaseWFViewHolder;
import capsicum.game.wordfox.recyclerview_game_data.DataListItem;
import capsicum.game.wordfox.recyclerview_game_data.RVTypes.TypeWordsDetail;

public class WordDetailsVH extends BaseWFViewHolder {
    private final TextView lettersGivenTV;
    private final TextView longestPossibleTV;

    public WordDetailsVH(View v) {
        super(v);
        lettersGivenTV = v.findViewById(R.id.letters_given);
        longestPossibleTV = v.findViewById(R.id.longest_possible_word);
    }

    @Override
    public void bindType(DataListItem item) {
        TypeWordsDetail wordDetail = (TypeWordsDetail) item;
        lettersGivenTV.setText(wordDetail.getLetters());
        String longestPossible = wordDetail.getLongestPossible() + " (" + wordDetail.getLongestPossible().length() + ")";
        longestPossibleTV.setText(longestPossible);
    }
}
