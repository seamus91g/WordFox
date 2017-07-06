package com.example.seamus.wordfox;

import java.util.HashMap;

/**
 * Created by Seamus on 05/07/2017.
 */

// Class to store game data and statistics
public class GameData {
    private int countGames;
    private String longestWord;
    private HashMap occurenceByLength = new HashMap();

    public void GameData() {
        occurenceByLength.put(3, 0);
        occurenceByLength.put(4, 0);
        occurenceByLength.put(5, 0);
        occurenceByLength.put(6, 0);
        occurenceByLength.put(7, 0);
        occurenceByLength.put(8, 0);
        occurenceByLength.put(9, 0);
    }

    public void gameCountUp() {
        countGames += 1;
    }

    public void addWord(String newWord) {
        int len = newWord.length();
        if (len >= longestWord.length()) {
            longestWord = newWord;
        }
        int numberOccurences = (int) occurenceByLength.get(len);
        occurenceByLength.put(len, (numberOccurences + 1));
    }

    public int findOccurence(int requestLength) {
        return (Integer) occurenceByLength.get(requestLength); // returnLength;
    }
}
