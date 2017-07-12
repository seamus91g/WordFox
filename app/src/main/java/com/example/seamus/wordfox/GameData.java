package com.example.seamus.wordfox;

import android.content.SharedPreferences;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Seamus on 05/07/2017.
 */

// Class to store game data and statistics
public class GameData {
    private int countGames;
    private String longestWord;
    private String userName;
    private HashMap<Integer, Integer> occurenceByLength = new HashMap<Integer, Integer>();
//    private static final String PREF_FILE_NAME = "PREFERENCE_FILE_WORDFOX";
//    public static SharedPreferences foxPreferences;
//    public static SharedPreferences.Editor editor; // = MainActivity.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE).edit();

    GameData() {
//        editor = getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE).edit();
        userName = "Fox";
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
