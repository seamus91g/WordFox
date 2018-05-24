package com.example.seamus.wordfox.RV;

import java.util.List;

public interface DataListItem {
    int PLAYER = 0;
    int CATEGORY = 1;
    int STATS = 2;
    int GAMES_HEADER = 3;
    int GAMES_DETAIL = 4;
    int WORDS_HEADER = 5;
    int WORDS_DETAIL = 6;

    boolean isListExpanded();

    void toggleExpanded();

    int getListItemType();

    List<DataListItem> getSubItems();

    int getSubItemCount();

    String getID();
}
