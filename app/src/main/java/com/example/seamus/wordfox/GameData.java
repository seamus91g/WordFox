package com.example.seamus.wordfox;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.net.URI;
import java.util.HashMap;
import java.util.StringTokenizer;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Seamus on 05/07/2017.
 */

// Class to store game data and statistics
public class GameData extends AppCompatActivity {
    public static final String MONITOR_TAG = "myTag";
    private String GAME_COUNT_KEY;
    private String ROUND_COUNT_KEY;
    private String LONGEST_WORD_KEY;
    private String USERNAME_KEY;
    private String PREF_FILE_NAME;
    private String PROFILE_PIC_KEY;
    private String SUBMITTED_CORRECT_COUNT_KEY;
    private String SUBMITTED_INCORRECT_COUNT_KEY;
    private String COUNT_NONE_FOUND_KEY;
    private String SHUFFLE_COUNT_KEY;
    private String HIGHEST_SCORE_KEY;
    private int playerNumber;
    private SharedPreferences foxPreferences;
    private SharedPreferences.Editor editor;

    GameData(Context myContext, int playerNumber) {
        this.playerNumber = playerNumber;
        GAME_COUNT_KEY = "game_count_" + playerNumber;
        ROUND_COUNT_KEY = "round_count_" + playerNumber;
        LONGEST_WORD_KEY = "longest_word_" + playerNumber;
        USERNAME_KEY = "username_" + playerNumber;
        PREF_FILE_NAME = "word_fox_gamedata_" + playerNumber;
        PROFILE_PIC_KEY = "wordfox_profile_pic_" + playerNumber;
        SUBMITTED_CORRECT_COUNT_KEY = "submitted_correct_count_" + playerNumber;
        SUBMITTED_INCORRECT_COUNT_KEY = "submitted_incorrect_count_" + playerNumber;
        COUNT_NONE_FOUND_KEY = "count_none_found_" + playerNumber;
        SHUFFLE_COUNT_KEY = "shuffle_count_" + playerNumber;
        HIGHEST_SCORE_KEY = "highest_score_" + playerNumber;

        foxPreferences = myContext.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        editor = foxPreferences.edit();
        editor.apply();
    }

    public String getAverageWordLength() {
        int totalWordCount = 0;
        double result = 0.0;
        int rounds = getRoundCount();
        for (int i = 3; i < 10; i++) {
            totalWordCount += i * findOccurence(i);
        }
        if (rounds > 0) {
            result = (double) totalWordCount / rounds;
        }
        return String.format("%.2f", result);
    }

    public int getRoundCount() {
        return foxPreferences.getInt(ROUND_COUNT_KEY, 0);
    }

    public int getSubmittedCorrectCount() {
        return foxPreferences.getInt(SUBMITTED_CORRECT_COUNT_KEY, 0);
    }

    public int getSubmittedIncorrectCount() {
        return foxPreferences.getInt(SUBMITTED_INCORRECT_COUNT_KEY, 0);
    }

    public int getNoneFoundCount() {
        return foxPreferences.getInt(COUNT_NONE_FOUND_KEY, 0);
    }

    public int getShuffleCount() {
        return foxPreferences.getInt(SHUFFLE_COUNT_KEY, 0);
    }

    public String getShuffleAverage() {
        int shufCount = foxPreferences.getInt(SHUFFLE_COUNT_KEY, 0);
        int roundCount = foxPreferences.getInt(ROUND_COUNT_KEY, 0);
        double shufAverage = 0;
        if (roundCount > 0) {
            shufAverage = (double) shufCount / roundCount;
        }
        return String.format("%.2f", shufAverage);
    }

    public int getHighestTotalScore() {
        return foxPreferences.getInt(HIGHEST_SCORE_KEY, 0);
    }

    public void setHighestScore(int submittedScore) {
        int highScore = foxPreferences.getInt(HIGHEST_SCORE_KEY, 0);
        if (submittedScore > highScore) {
            editor.putInt(HIGHEST_SCORE_KEY, submittedScore);
            editor.apply();
        }
    }

    public void correctCountUp() {
        int countCorrect = foxPreferences.getInt(SUBMITTED_CORRECT_COUNT_KEY, 0);
        countCorrect += 1;
        editor.putInt(SUBMITTED_CORRECT_COUNT_KEY, countCorrect);
        editor.apply();
    }

    public void incorrectCountUp() {
        int countIncorrect = foxPreferences.getInt(SUBMITTED_INCORRECT_COUNT_KEY, 0);
        countIncorrect += 1;
        editor.putInt(SUBMITTED_INCORRECT_COUNT_KEY, countIncorrect);
        editor.apply();
    }

    public void noneFoundCountUp() {
        int countNoneFound = foxPreferences.getInt(COUNT_NONE_FOUND_KEY, 0);
        countNoneFound += 1;
        editor.putInt(COUNT_NONE_FOUND_KEY, countNoneFound);
        editor.apply();
    }

    public void setUsername(String nameEntered) {
        editor.putString(USERNAME_KEY, nameEntered);
        editor.apply();
    }

    public void setProfilePicture(Uri picture) {
        editor.putString(PROFILE_PIC_KEY, picture.toString());
        editor.apply();
    }

    public String getProfilePicture() {
        return foxPreferences.getString(PROFILE_PIC_KEY, "");
    }

    public String getUsername() {
        String defaultName = "Player " + (playerNumber+1);
        if (playerNumber == 0){
            defaultName = "Fox";
        }
        return foxPreferences.getString(USERNAME_KEY, defaultName);
    }

    public void gameCountUp() {
        int countGames = foxPreferences.getInt(GAME_COUNT_KEY, 0);
        countGames += 1;
        editor.putInt(GAME_COUNT_KEY, countGames);
        editor.apply();
    }

    public void roundCountUp() {
        int countRounds = foxPreferences.getInt(ROUND_COUNT_KEY, 0);
        countRounds += 1;
        editor.putInt(ROUND_COUNT_KEY, countRounds);
        editor.apply();
    }

    public void shuffleCountUp() {
        int countShuffles = foxPreferences.getInt(SHUFFLE_COUNT_KEY, 0);
        countShuffles += 1;
        editor.putInt(SHUFFLE_COUNT_KEY, countShuffles);
        editor.apply();
    }

    public int getGameCount() {
        return foxPreferences.getInt(GAME_COUNT_KEY, 0);
    }

    public void addWord(String newWord) {
        Log.d(MONITOR_TAG, "GameData: Adding " + newWord + ", END");
        // Check if longest word
        int len = newWord.length();
        String currentLongest = foxPreferences.getString(LONGEST_WORD_KEY, "");
        if (len >= currentLongest.length()) {
            editor.putString(LONGEST_WORD_KEY, newWord);
        } else if (len == 0) {
            noneFoundCountUp();
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
