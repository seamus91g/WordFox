package com.example.seamus.wordfox.RV.RVTypes;

import com.example.seamus.wordfox.PlayerIdentity;
import com.example.seamus.wordfox.RV.DataListItem;
import com.example.seamus.wordfox.database.DataPerGame;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TypeGamesDetail implements DataListItem {
    private final UUID ID;

    private boolean isExpanded = false;
    private DataPerGame data;

    public TypeGamesDetail(DataPerGame data) {
        this.data = data;
        ID = UUID.randomUUID();
    }
    public ArrayList<PlayerIdentity> getPlayers(){
        return data.players;
    }
    public Map<UUID, ArrayList<String>> getPlayerWords(){
        return data.wordsPerPlayer;
    }
    public String getLetters(int round){
        return data.letters.get(round);
    }
    public String getBestPossible(int round){
        return data.bestPossible.get(round);
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
        return DataListItem.GAMES_DETAIL;
    }

    @Override
    public List<DataListItem> getSubItems() {
        return null;
    }

    @Override
    public int getSubItemCount() {
        return 0;
    }

    @Override
    public UUID getID() {
        return ID;
    }

}
