package com.example.seamus.wordfox.RV.RVTypes;

import com.example.seamus.wordfox.PlayerIdentity;
import com.example.seamus.wordfox.RV.DataListItem;
import com.example.seamus.wordfox.database.DataPerGame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class TypeGamesHeader implements DataListItem {
    private final UUID ID;
    private final ArrayList<PlayerIdentity> gameWinner;


    private final int winnerScore;
    DataListItem gameDetails;
    private boolean isExpanded = false;

    public TypeGamesHeader(DataPerGame gameHeader, DataListItem gameDetails) {
        this.gameWinner = gameHeader.winners;
        this.gameDetails = gameDetails;
        this.winnerScore = gameHeader.scoreWinner;
        ID = UUID.randomUUID();
    }

    public ArrayList<PlayerIdentity> getGameWinner() {
        return gameWinner;
    }

    public int getWinnerScore() {
        return winnerScore;
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
        return DataListItem.GAMES_HEADER;
    }

    @Override
    public List<DataListItem> getSubItems() {
        return new ArrayList(Arrays.asList(gameDetails));
    }

    @Override
    public int getSubItemCount() {
        return 1;
    }

    @Override
    public UUID getID() {
        return ID;
    }
}
