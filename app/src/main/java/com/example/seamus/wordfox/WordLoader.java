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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Gilroy on 2/20/2018.
 */

public class WordLoader {
    // Load in all rounds
    // Load in all valid words
    // Generate all WordData items from rounds
    // Generate all WordDataHeader items from words
    // Match all rounds to a particular word

    public static ArrayList<WordDataHeader> getValid(Context context, String player){
        HashMap<String, ArrayList<RoundItem>> roundsPerWord = new HashMap<>();
        FoxSQLData foxDB = new FoxSQLData(context);
        ArrayList<WordItem> validWords = (ArrayList<WordItem>) foxDB.getValidWords(player);

        // Find duplicate words -> use hashmap to store arraylist of rounds for each word
        for (WordItem word : validWords){
            RoundItem round = foxDB.getRound(word.getGameId());
            String wordFound = word.getWordSubmitted();
            if(roundsPerWord.containsKey(wordFound)){
                roundsPerWord.get(wordFound).add(round);
            }else {
                roundsPerWord.put(wordFound, new ArrayList<RoundItem>(Arrays.asList(round)));
            }
        }
        // Create a WordDataHeader object for each key in the hashmap.
        // The hashmap values are used for the WordData objects in the WordDataHeader objects
        ArrayList<WordDataHeader> dataPerHeader = new ArrayList<>();
        Iterator it = roundsPerWord.entrySet().iterator();
        while (it.hasNext()){
            ArrayList<WordData> dataPerWord = new ArrayList<>();
            Map.Entry me = (Map.Entry) it.next();
            for (RoundItem r : (ArrayList<RoundItem>) me.getValue()){
                WordData wd = new WordData(r.getLetters(), r.getLongestPossible());
                dataPerWord.add(wd);
            }
            WordDataHeader wdh = new WordDataHeader((String) me.getKey(), dataPerWord.size(), dataPerWord);
            dataPerHeader.add(wdh);
        }

        return dataPerHeader;
    }

    public static List<DataPerGame> getGames(Context context, String player){

        FoxSQLData foxDB = new FoxSQLData(context);
        List<GameItem> allGs = foxDB.getAllGames();
        List<DataPerGame> allGameData = new ArrayList<>();

        // Identify unique list of player names by searching the round ids
        for (GameItem g : allGs){
            Map<String, ArrayList<String>> wordsPerPlayer = new HashMap<>();
            Set<String> playerNames = new HashSet<String>();
            g.populateRoundData(foxDB);
            List<String> rIDs = g.getRoundIDs();
            List<String> letters = new ArrayList<>();
            List<String> longest = new ArrayList<>();
            // Find words for each round ID. Filter 'final', Then sort the words by which player found each.
            HashMap<String, List<WordItem>> allWordsByRound = new HashMap<>();
            for (String rid : rIDs){
                List<WordItem> words = foxDB.getWordsByRound(rid);
                for (WordItem w : words){
                    if (w.isFinal()){
                        if(wordsPerPlayer.containsKey(w.getPlayerName())){
                            wordsPerPlayer.get(w.getPlayerName()).add(w.getWordSubmitted());
                        }else{
                            wordsPerPlayer.put(w.getPlayerName(), new ArrayList<String>(Arrays.asList(w.getWordSubmitted())));
                        }
                        playerNames.add(w.getPlayerName());
                    }
                }
                RoundItem round = foxDB.getRound(rid);
                letters.add(round.getLetters());
                longest.add(round.getLongestPossible());
            }
            int winScore = 0;
            if (!wordsPerPlayer.isEmpty()){
                for (String word : wordsPerPlayer.get(g.getWinner(0))){
                    winScore += word.length();
                }
            }
            DataPerGame dpg = new DataPerGame();
            dpg.winner = g.getWinnerString();
            dpg.scoreWinner = winScore;
            dpg.letters = letters;
            dpg.bestPossible = longest;
            dpg.players = playerNames;
            dpg.wordsPerPlayer = wordsPerPlayer;
            allGameData.add(dpg);
        }
        return allGameData;
    }

}
