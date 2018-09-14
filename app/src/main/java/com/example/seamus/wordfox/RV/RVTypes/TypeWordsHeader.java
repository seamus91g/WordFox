package com.example.seamus.wordfox.RV.RVTypes;

import com.example.seamus.wordfox.RV.DataListItem;
import com.example.seamus.wordfox.dataWordsRecycler.WordData;
import com.example.seamus.wordfox.dataWordsRecycler.WordDataHeader;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TypeWordsHeader implements DataListItem {
    private final UUID ID;
    private final List<DataListItem> childItems = new ArrayList<>();
    private String word;
    private boolean isExpanded = false;

    public TypeWordsHeader(WordDataHeader wdh) {
        this.word = wdh.getWordSubmitted();
        ID = UUID.randomUUID();
        for (WordData wd : wdh.getChildList()) {
            childItems.add(new TypeWordsDetail(wd));
        }
    }

    public String getWord() {
        return word;
    }

    public int getTimesFound() {
        return childItems.size();
    }

    @Override
    public int getListItemType() {
        return DataListItem.WORDS_HEADER;
    }

    @Override
    public List<DataListItem> getSubItems() {
        return childItems;
    }

    @Override
    public int getSubItemCount() {
        return childItems.size();
    }

    @Override
    public UUID getID() {
        return ID;
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
