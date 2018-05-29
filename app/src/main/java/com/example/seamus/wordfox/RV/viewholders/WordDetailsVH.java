package com.example.seamus.wordfox.RV.viewholders;

import android.view.View;
import android.widget.TextView;

import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.RV.BaseWFViewHolder;
import com.example.seamus.wordfox.RV.DataListItem;
import com.example.seamus.wordfox.RV.RVTypes.TypeWordsDetail;
import com.example.seamus.wordfox.RV.WFAdapter;

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
        longestPossibleTV.setText(wordDetail.getLongestPossible());
    }
}
