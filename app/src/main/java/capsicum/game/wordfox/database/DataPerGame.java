package capsicum.game.wordfox.database;

import capsicum.game.wordfox.PlayerIdentity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Gilroy
 * Basic Struct-like class to hold information relating to one specific game which was played.
 * Primary purpose is to gather all info required for the 'Game Details' panel on the Data page.
 */

public class DataPerGame {
    public final ArrayList<PlayerIdentity> winners;
    public final int scoreWinner;
    public final List<String> letters;
    public final List<String> bestPossible;
    public final ArrayList<PlayerIdentity> players;
    public final Map<UUID, ArrayList<String>> wordsPerPlayer;

    public DataPerGame(ArrayList<PlayerIdentity> winners, int scoreWinner, List<String> letters, List<String> bestPossible, ArrayList<PlayerIdentity> players, Map<UUID, ArrayList<String>> wordsPerPlayer) {
        this.winners = winners;
        this.scoreWinner = scoreWinner;
        this.letters = letters;
        this.bestPossible = bestPossible;
        this.players = players;
        this.wordsPerPlayer = wordsPerPlayer;
    }

    // Swap the name of a player for a new name
//    public void swapName(String oldName, String newName) {    TODO: Remove this. Not necessary when tracking by ID
//        winner = winner.replaceAll(oldName, newName);
//        players.remove(oldName);
//        players.add(newName);
//        wordsPerPlayer.put(newName, wordsPerPlayer.get(oldName));
//        wordsPerPlayer.remove(oldName);
//    }
}

