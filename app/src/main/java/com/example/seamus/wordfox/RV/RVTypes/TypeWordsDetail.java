package com.example.seamus.wordfox.RV.RVTypes;

import com.example.seamus.wordfox.RV.DataListItem;
import com.example.seamus.wordfox.dataWordsRecycler.WordData;

import java.util.List;
import java.util.UUID;

public class TypeWordsDetail implements DataListItem {
    private final String ID;
    private final String letters;
    private final String longestPossible;
    private boolean isExpanded = false;

    public TypeWordsDetail(WordData wd) {
        this.letters = wd.getGivenLetters();
        this.longestPossible = wd.getLongestPossibleWord();
        ID = UUID.randomUUID().toString();
    }

    public String getLetters() {
        return letters;
    }

    public String getLongestPossible() {
        return longestPossible;
    }

    @Override
    public int getListItemType() {
        return DataListItem.WORDS_DETAIL;
    }

    @Override
    public int getSubItemCount() {
        return 0;
    }

    @Override
    public String getID() {
        return ID;
    }

    @Override
    public List<DataListItem> getSubItems() {
        return null;
    }

    @Override
    public boolean isListExpanded() {
        return isExpanded;
    }

    @Override
    public void toggleExpanded() {
        isExpanded = !isExpanded;
    }

}
