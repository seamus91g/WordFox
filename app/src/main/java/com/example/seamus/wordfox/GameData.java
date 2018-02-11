package com.example.seamus.wordfox;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Seamus on 05/07/2017.
 */

// Class to store game data and statistics
public class GameData extends AppCompatActivity {
    public static final String MONITOR_TAG = "myTag";
    public static final String DEFAULT_P1_NAME = "Player 1";
    public static final String NONE_FOUND = "None Found!";
    private String GAME_COUNT_KEY;
    private String ROUND_COUNT_KEY;
    private String LONGEST_WORD_KEY;
    private String USERNAME_KEY;
    private String PREF_FILE_NAME;
    private static final String PREF_FILE_NAME_STATIC = "word_fox_gamedata_static";
    private String PROFILE_PIC_KEY;
    private String SUBMITTED_CORRECT_COUNT_KEY;
    private String SUBMITTED_INCORRECT_COUNT_KEY;
    private String COUNT_NONE_FOUND_KEY;
    private String SHUFFLE_COUNT_KEY;
    private String HIGHEST_SCORE_KEY;
    private String RECENT_WORDS_KEY;
    private String RECENT_GAME_ID_KEY;
    private String BEST_WORDS_KEY;
    //    private static String PLAYER_1_NAME_KEY = "player_1";
    private static String NAMED_PLAYER_COUNT_KEY = "named_player_count";
//    private int playerNumber;
    private static Set<String> playerIDs = new HashSet<String>();
    private SharedPreferences foxPreferences;
    private SharedPreferences.Editor editor;

//    GameData(Context myContext, int playerNumber) {
//        this.playerNumber = playerNumber;
//        GAME_COUNT_KEY = "game_count_" + playerNumber;
//        ROUND_COUNT_KEY = "round_count_" + playerNumber;
//        LONGEST_WORD_KEY = "longest_word_" + playerNumber;
//        USERNAME_KEY = "username_" + playerNumber;
//        PREF_FILE_NAME = "word_fox_gamedata_" + playerNumber;
//        PROFILE_PIC_KEY = "wordfox_profile_pic_" + playerNumber;
//        SUBMITTED_CORRECT_COUNT_KEY = "submitted_correct_count_" + playerNumber;
//        SUBMITTED_INCORRECT_COUNT_KEY = "submitted_incorrect_count_" + playerNumber;
//        COUNT_NONE_FOUND_KEY = "count_none_found_" + playerNumber;
//        SHUFFLE_COUNT_KEY = "shuffle_count_" + playerNumber;
//        HIGHEST_SCORE_KEY = "highest_score_" + playerNumber;
//
//        foxPreferences = myContext.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
//        editor = foxPreferences.edit();
//        editor.apply();
//    }
    // Instantiate using a name instead of a player number
    GameData(Context myContext, String playerID) {
//        this.playerNumber = 99;     // Why bother setting this?
        if (playerID.equals("")){
            Toast.makeText(myContext, "Player name is empty!", Toast.LENGTH_SHORT).show();
//            throw new RuntimeException("Empty player name!");
        }
        GAME_COUNT_KEY = "game_count_" + playerID;
        ROUND_COUNT_KEY = "round_count_" + playerID;
        LONGEST_WORD_KEY = "longest_word_" + playerID;
        USERNAME_KEY = "username_" + playerID;
        PREF_FILE_NAME = "word_fox_gamedata_" + playerID;
        PROFILE_PIC_KEY = "wordfox_profile_pic_" + playerID;
        SUBMITTED_CORRECT_COUNT_KEY = "submitted_correct_count_" + playerID;
        SUBMITTED_INCORRECT_COUNT_KEY = "submitted_incorrect_count_" + playerID;
        COUNT_NONE_FOUND_KEY = "count_none_found_" + playerID;
        SHUFFLE_COUNT_KEY = "shuffle_count_" + playerID;
        HIGHEST_SCORE_KEY = "highest_score_" + playerID;
        RECENT_WORDS_KEY = "recent_words_" + playerID;
        RECENT_GAME_ID_KEY = "recent_game_id_" + playerID;
        BEST_WORDS_KEY = "best_words_" + playerID;

        foxPreferences = myContext.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        editor = foxPreferences.edit();
        editor.apply();

        addPlayer(playerID, myContext);
        playerIDs.add(playerID);
        if (!playerID.equals(DEFAULT_P1_NAME)) {    // If p1, don't set username. Only done in profile
            setUsername(playerID);
        }
//        if (playerID.equals(DEFAULT_P1_NAME)){
//            playerID = getPlayer1Name(myContext);
//            Toast.makeText(myContext, "Default p1 name! Now: " + playerID, Toast.LENGTH_SHORT).show();
//        }
    }
    // Create a new player if the name is unique
    void addPlayer(String playerID, Context myContext){
        SharedPreferences foxPreferencesStatic = myContext.getSharedPreferences(PREF_FILE_NAME_STATIC, MODE_PRIVATE);
        SharedPreferences.Editor editor = foxPreferencesStatic.edit();
        int namedPlayerCount = getNamedPlayerCount(myContext);
        for(int x=0; x<namedPlayerCount; x++){
            String KEY = "name_" + x;
            String name = foxPreferencesStatic.getString(KEY, "Unknown");
            if(name.equals(playerID)){
                return;
            }
        }
        String KEY = "name_" + getNamedPlayerCount(myContext);
        editor.putString(KEY, playerID);
        editor.apply();
        namedPlayerCountUp(myContext);
    }
    // Increase count of number of named players
    private void namedPlayerCountUp(Context myContext){
        int countIncorrect = getNamedPlayerCount(myContext);
        countIncorrect += 1;
        setNamedPlayerCount(myContext, countIncorrect);
    }
    private void setNamedPlayerCount(Context myContext, int count){
        SharedPreferences foxPreferences = myContext.getSharedPreferences(PREF_FILE_NAME_STATIC, MODE_PRIVATE);
        SharedPreferences.Editor editor = foxPreferences.edit();
        editor.putInt(NAMED_PLAYER_COUNT_KEY, count);
        editor.apply();
    }
    static int getNamedPlayerCount(Context myContext){
        SharedPreferences foxPreferences = myContext.getSharedPreferences(PREF_FILE_NAME_STATIC, MODE_PRIVATE);
        return foxPreferences.getInt(NAMED_PLAYER_COUNT_KEY, 0);
    }
    static ArrayList<String> getNamedPlayerList(Context myContext){
        SharedPreferences foxPreferences = myContext.getSharedPreferences(PREF_FILE_NAME_STATIC, MODE_PRIVATE);
        ArrayList<String> namedPlayers = new ArrayList<String>();
        int namedPlayerCount = getNamedPlayerCount(myContext);
        for (int i=0; i<namedPlayerCount; ++i){
            String KEY = "name_" + i;
            String name = foxPreferences.getString(KEY, "Unknown");
            namedPlayers.add(name);
        }
        return namedPlayers;
    }

//    public static String getPlayer1Name(Context myContext) {
//        SharedPreferences foxPreferences = myContext.getSharedPreferences(PREF_FILE_NAME_STATIC, MODE_PRIVATE);
//        SharedPreferences.Editor editor = foxPreferences.edit();
//        return foxPreferences.getString(PLAYER_1_NAME_KEY, "Fox");
//    }
//    public String getPlayerName(int index) {
////        SharedPreferences foxPreferences = myContext.getSharedPreferences(PREF_FILE_NAME_STATIC, MODE_PRIVATE);
////        SharedPreferences.Editor editor = foxPreferences.edit();
//
//        String KEY = "name_" + index;
//        String name = foxPreferences.getString(KEY, "Unknown");
//        return foxPreferences.getString(PLAYER_1_NAME_KEY, "Fox");
//    }
//    static void setPlayer1Name(Context myContext, String name) {
//        SharedPreferences foxPreferences = myContext.getSharedPreferences(PREF_FILE_NAME_STATIC, MODE_PRIVATE);
//        SharedPreferences.Editor editor = foxPreferences.edit();
//        editor.putString(PLAYER_1_NAME_KEY, name);
//        editor.apply();
//    }
    public void setUsername(String nameEntered) {
        editor.putString(USERNAME_KEY, nameEntered);
        editor.apply();
    }
    public String getUsername() {
        String defaultName = "Fox";
//        String defaultName = "Player " + (playerNumber+1);
//        if (playerNumber == 0){
//            defaultName = "Fox";
//        }
        return foxPreferences.getString(USERNAME_KEY, defaultName);
    }
    // BEST_WORDS_KEY
    // Set of most picked words during the most recent game played
    public void setBestWords(ArrayList<String> bestWords){
        for (int i=0; i<bestWords.size(); ++i) {
            String BEST_WORDS_KEY_i = BEST_WORDS_KEY + "_" + i;
            editor.putString(BEST_WORDS_KEY_i, bestWords.get(i));
            editor.apply();
        }
    }
    public ArrayList<String> getBestWords(){
        ArrayList<String> bestWords = new ArrayList<>();
        for (int i=0; i<GameInstance.getMaxNumberOfRounds(); ++i) {
            String BEST_WORDS_KEY_i = BEST_WORDS_KEY + "_" + i;
            bestWords.add(foxPreferences.getString(BEST_WORDS_KEY_i, "None Found!"));
        }
        return bestWords;
    }
    // Set of most picked words during the most recent game played
    public void setRecentWords(ArrayList<String> words){
        for (int i=0; i<words.size(); ++i) {
            String RECENT_WORDS_KEY_i = RECENT_WORDS_KEY + "_" + i;
            editor.putString(RECENT_WORDS_KEY_i, words.get(i));
            editor.apply();
        }
    }
    public ArrayList<String> getRecentWords(){
        ArrayList<String> recentWords = new ArrayList<>();
        for (int i=0; i<GameInstance.getMaxNumberOfRounds(); ++i) {
            String RECENT_WORDS_KEY_i = RECENT_WORDS_KEY + "_" + i;
            recentWords.add(foxPreferences.getString(RECENT_WORDS_KEY_i, "None Found!"));
        }
        return recentWords;
    }
    // Get the ID of the most recently played game
    public void setRecentGame(String recentGameID) {
        editor.putString(RECENT_GAME_ID_KEY, recentGameID);
        editor.apply();
    }
    public String getRecentGame() {
        return foxPreferences.getString(RECENT_GAME_ID_KEY, "");
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

    public void setProfilePicture(Uri picture) {
        editor.putString(PROFILE_PIC_KEY, picture.toString());
        editor.apply();
    }

    public String getProfilePicture() {
        return foxPreferences.getString(PROFILE_PIC_KEY, "");
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
