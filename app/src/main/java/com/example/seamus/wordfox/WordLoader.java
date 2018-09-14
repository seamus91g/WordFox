package com.example.seamus.wordfox;

import android.content.Context;

import com.example.seamus.wordfox.dataWordsRecycler.WordData;
import com.example.seamus.wordfox.dataWordsRecycler.WordDataHeader;
import com.example.seamus.wordfox.database.DataPerGame;
import com.example.seamus.wordfox.database.FoxSQLData;
import com.example.seamus.wordfox.datamodels.GameItem;
import com.example.seamus.wordfox.datamodels.RoundItem;
import com.example.seamus.wordfox.datamodels.WordItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Gilroy on 2/20/2018.
 */

public class WordLoader {
    // Load in all rounds
    // Load in all valid words
    // Generate all WordData items from rounds
    // Generate all WordDataHeader items from words
    // Match all rounds to a particular word

    public static ArrayList<WordDataHeader> getValid(Context context, UUID player) {
        HashMap<String, ArrayList<RoundItem>> roundsPerWord = new HashMap<>();
        FoxSQLData foxDB = new FoxSQLData(context);
        List<WordItem> validWords = foxDB.getValidWords(player);

        // Find duplicate words -> use hashmap to store arraylist of rounds for each word
        for (WordItem word : validWords) {
            RoundItem round = foxDB.getRound(word.getGameId());
            String wordFound = word.getWordSubmitted();
            if (roundsPerWord.containsKey(wordFound)) {
                roundsPerWord.get(wordFound).add(round);
            } else {
                roundsPerWord.put(wordFound, new ArrayList<RoundItem>(Arrays.asList(round)));
            }
        }
        // Create a WordDataHeader object for each key in the hashmap.
        // The hashmap values are used for the WordData objects in the WordDataHeader objects
        ArrayList<WordDataHeader> dataPerHeader = new ArrayList<>();
        for (String roundItemWord : roundsPerWord.keySet()) {
            ArrayList<RoundItem> roundItems = roundsPerWord.get(roundItemWord);
            ArrayList<WordData> dataPerWord = new ArrayList<>();
            for (RoundItem r : roundItems) {
                WordData wd = new WordData(r.getLetters(), r.getLongestPossible());
                dataPerWord.add(wd);
            }
            WordDataHeader wdh = new WordDataHeader(roundItemWord, dataPerWord.size(), dataPerWord);
            dataPerHeader.add(wdh);
        }

        return dataPerHeader;
    }

    public static List<DataPerGame> getGames(Context context) {

        FoxSQLData foxDB = new FoxSQLData(context);
        List<GameItem> allGs = foxDB.getAllGames();
        List<DataPerGame> allGameData = new ArrayList<>();

        // Identify unique list of player names by searching the round ids
        for (GameItem g : allGs) {
            Map<UUID, ArrayList<String>> wordsPerPlayer = new HashMap<>();
            Set<UUID> playerIDs = new HashSet<>();
            g.populateRoundData(foxDB);
            List<UUID> rIDs = g.getRoundIDs();
            List<String> letters = new ArrayList<>();
            List<String> longest = new ArrayList<>();
            // Find words for each round ID. Filter 'final', Then sort the words by which player found each.
            HashMap<String, List<WordItem>> allWordsByRound = new HashMap<>();
            // Form correct list of playerNames
            for (UUID rid : rIDs) {
                List<WordItem> words = foxDB.getWordsByRound(rid);
                for (WordItem w : words) {
                    if (w.isFinal()) {
                        if (wordsPerPlayer.containsKey(w.getPlayerID())) {
                            wordsPerPlayer.get(w.getPlayerID()).add(w.getWordSubmitted());
                        } else {
                            wordsPerPlayer.put(w.getPlayerID(), new ArrayList<String>(Arrays.asList(w.getWordSubmitted())));
                        }
                        playerIDs.add(w.getPlayerID());
                    }
                }
                RoundItem round = foxDB.getRound(rid);
                letters.add(round.getLetters());
                longest.add(round.getLongestPossible());
            }
            ArrayList<PlayerIdentity> players = new ArrayList<>();
            for (UUID player : playerIDs) {
                players.add(new PlayerIdentity(player, GameData.getUsername(player, context)));
            }
            int winScore = 0;
            if (!wordsPerPlayer.isEmpty()) {
                for (String word : wordsPerPlayer.get(g.getWinner(0))) {     // All 'winners' have same score
                    if (word.equals("<none>")) {
                        continue;
                    }
                    winScore += word.length();
                }
            }
            ArrayList<PlayerIdentity> winners = new ArrayList<>();
            for(PlayerIdentity player : players){
                if(g.getWinners().contains(player.ID)){
                    winners.add(player);
                }
            }
            DataPerGame dpg = new DataPerGame(winners, winScore, letters, longest, players, wordsPerPlayer);
            allGameData.add(dpg);
        }
        return allGameData;
    }

}
