package com.example.seamus.wordfox.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Gilroy
 * Basic Struct-like class to hold information relating to one specific game which was played.
 * Primary purpose is to gather all info required for the 'Game Details' panel on the Data page.
 */

public class DataPerGame {
    public String winner;
    public int scoreWinner;
    public List<String> letters;
    public List<String> bestPossible;
    public Set<String> players;
    public Map<String, ArrayList<String>> wordsPerPlayer;

    // Swap the name of a player for a new name
    public void swapName(String oldName, String newName) {
        winner = winner.replaceAll(oldName, newName);
        players.remove(oldName);
        players.add(newName);
        wordsPerPlayer.put(newName, wordsPerPlayer.get(oldName));
        wordsPerPlayer.remove(oldName);
    }
}

