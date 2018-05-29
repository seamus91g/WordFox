package com.example.seamus.wordfox.RV.RVTypes;

import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.RV.DataListItem;
import com.example.seamus.wordfox.database.DataPerGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.UUID;

public class TypeGamesDetail implements DataListItem {
    private final String ID;

    private List<String> letters;
    private List<String> bestPossible;
    private List<String> players = new ArrayList<>();
    private Map<String, ArrayList<String>> wordsPerPlayer;
    private boolean isExpanded = false;

    public TypeGamesDetail(DataPerGame data) {
        this.letters = data.letters;
        this.bestPossible = data.bestPossible;
        players.addAll(data.players);
        this.wordsPerPlayer = data.wordsPerPlayer;
        ID = UUID.randomUUID().toString();
    }
    public List<String> getPlayers(){
        return players;
    }
    public Map<String, ArrayList<String>> getPlayerWords(){
        return wordsPerPlayer;
    }
    public String getLetters(int round){
        return letters.get(round);
    }
    public String getBestPossible(int round){
        return bestPossible.get(round);
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
    public String getID() {
        return ID;
    }

}
