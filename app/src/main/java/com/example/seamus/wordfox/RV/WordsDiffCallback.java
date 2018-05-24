package com.example.seamus.wordfox.RV;

import android.support.v7.util.DiffUtil;

import java.util.ArrayList;
import java.util.List;

public class WordsDiffCallback extends DiffUtil.Callback {
    private ArrayList<DataListItem> oldList;
    private List<DataListItem> newList;

    public WordsDiffCallback(ArrayList<DataListItem> oldList, List<DataListItem> newList) {
        this.oldList = oldList;
        this.newList = newList;
    }

    @Override
    public int getOldListSize() {
        return oldList.size();
    }

    @Override
    public int getNewListSize() {
        return newList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getID().equals(newList.get(newItemPosition).getID());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldList.get(oldItemPosition).getID().equals(newList.get(newItemPosition).getID());
    }
}
