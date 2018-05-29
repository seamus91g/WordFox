package com.example.seamus.wordfox.RV.RVTypes;

import com.example.seamus.wordfox.RV.DataListItem;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TypePlayer implements DataListItem {
    private final String ID;
    private String player;
    private ArrayList<DataListItem> subItems;
    private boolean isExpanded = false;

    public TypePlayer(String playerName, ArrayList<DataListItem> subItems) {
        this.player = playerName;
        this.subItems = subItems;
        this.ID = UUID.randomUUID().toString();
    }

    public String getPlayer() {
        return player;
    }

    @Override
    public boolean isListExpanded() {
        return isExpanded;
    }

    @Override
    public void toggleExpanded() {
        isExpanded = !isExpanded;
    }

    @Override
    public int getListItemType() {
        return DataListItem.PLAYER;
    }

    @Override
    public List<DataListItem> getSubItems() {
        return subItems;
    }

    @Override
    public int getSubItemCount() {
        return subItems.size();
    }

    @Override
    public String getID() {
        return ID;
    }
}
