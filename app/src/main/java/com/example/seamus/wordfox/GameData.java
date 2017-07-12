package com.example.seamus.wordfox;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Seamus on 05/07/2017.
 */

// Class to store game data and statistics
public class GameData extends AppCompatActivity {
    public static final String MONITOR_TAG = "myTag";
    private String GAME_COUNT_KEY = "game_count";
    private String LONGEST_WORD_KEY = "longest_word";
    private String USERNAME_KEY = "username";
    private String PREF_FILE_NAME = "word_fox_gamedata";

    private SharedPreferences foxPreferences;
    private SharedPreferences.Editor editor;

    GameData(Context myContext) {
        foxPreferences = myContext.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        editor = foxPreferences.edit();
    }

    public void setUsername(String nameEntered) {
        editor.putString(USERNAME_KEY, nameEntered);
        editor.apply();
    }

    public String getUsername() {
        return foxPreferences.getString(USERNAME_KEY, "");
    }

    public void gameCountUp() {
        int countGames = foxPreferences.getInt(GAME_COUNT_KEY, 0);
        countGames += 1;
        editor.putInt(GAME_COUNT_KEY, countGames);
        editor.apply();
    }

    public int getGameCount() {
        return foxPreferences.getInt(GAME_COUNT_KEY, 0);
    }

    public void addWord(String newWord) {
        // Check if longest word
        int len = newWord.length();
        String currentLongest = foxPreferences.getString(LONGEST_WORD_KEY, "");
        Log.d(MONITOR_TAG, "GameData: Adding " + newWord + ", END");
        if (len >= currentLongest.length()) {
            editor.putString(LONGEST_WORD_KEY, newWord);
            Log.d(MONITOR_TAG, "GameData: Now longest " + newWord + ", END");
        }
        // Increase occurence of word length
        int numberOccurences = foxPreferences.getInt(Integer.toString(len), 0);
        numberOccurences += 1;
        editor.putInt(Integer.toString(len), numberOccurences);
        editor.apply();
    }

    public String findLongest() {
        return foxPreferences.getString(LONGEST_WORD_KEY, "");
    }

    public int findLengthLongest() {
        return (foxPreferences.getString(LONGEST_WORD_KEY, "")).length();
    }

    public int findOccurence(int requestLength) {
        return foxPreferences.getInt(Integer.toString(requestLength), 0); // Find number of occurences of a particular length
    }
}
