package capsicum.game.wordfox;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import capsicum.game.wordfox.profile.FoxRank;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Seamus
 */

// Class to store game data and statistics
public class GameData extends AppCompatActivity {
    public static final String MONITOR_TAG = "myTag";
    public static final int PROFILE_DEFAULT_IMG = R.drawable.ppfox2_outline_small_noalpha;
    //    public static final String DEFAULT_P1_ID = "Player 1";
    public static final String NONE_FOUND = "None Found!";
    public static final String NON_EXISTANT = "non_existant";
    public static final String ID_KEY_PREFIX = "ID_";
    private static final String DEFAULT_NAME = "Fox";
    private static final String HIGHEST_SCORE_PREFIX = "highest_score_";
    private static final String INTERSTITIAL_COUNT_KEY = "interstitial_count";
    private String BEST_WORD_TOTAL_COUNT;
    private String GAME_COUNT_KEY;
    private String ROUND_COUNT_KEY;
    private String LONGEST_WORD_KEY;
    private String USERNAME_KEY;
    private static final String USERNAME_PREFIX = "username_";
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
    private String BEST_LETTERS_KEY;
    private static String NAMED_PLAYER_COUNT_KEY = "named_player_count";
    private SharedPreferences foxPreferences;
    private SharedPreferences.Editor editor;
    private String FOX_RANK_KEY;
    private UUID playerID;
    private static String PREF_FILE_PREFIX = "word_fox_gamedata_";

    // The stats for each player are stored separately
    public GameData(Context myContext) {
        this(myContext, "");
    }

    public GameData(Context myContext, String name) {
        this(myContext, UUID.randomUUID(), name);
    }

    public GameData(Context myContext, UUID playerID) {
        this(myContext, playerID, "");
    }

    public GameData(Context myContext, UUID playerID, String name) {
        this.playerID = playerID;
        GAME_COUNT_KEY = "game_count_" + playerID;
        ROUND_COUNT_KEY = "round_count_" + playerID;
        LONGEST_WORD_KEY = "longest_word_" + playerID;
        USERNAME_KEY = USERNAME_PREFIX + playerID;
        PREF_FILE_NAME = PREF_FILE_PREFIX + playerID;
        PROFILE_PIC_KEY = "wordfox_profile_pic_" + playerID;
        SUBMITTED_CORRECT_COUNT_KEY = "submitted_correct_count_" + playerID;
        SUBMITTED_INCORRECT_COUNT_KEY = "submitted_incorrect_count_" + playerID;
        COUNT_NONE_FOUND_KEY = "count_none_found_" + playerID;
        SHUFFLE_COUNT_KEY = "shuffle_count_" + playerID;
        HIGHEST_SCORE_KEY = HIGHEST_SCORE_PREFIX + playerID;
        RECENT_WORDS_KEY = "recent_words_" + playerID;
        RECENT_GAME_ID_KEY = "recent_game_id_" + playerID;
        BEST_WORDS_KEY = "best_words_" + playerID;
        FOX_RANK_KEY = "fox_rank_" + playerID;
        BEST_WORD_TOTAL_COUNT = "best_total_count_" + playerID;

        foxPreferences = myContext.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
        editor = foxPreferences.edit();
        editor.apply();

        // TODO: Get rid of default player names such as 'Player 2'. Not necessary.
        // ToDo: These constructors are messy.
        if (name.equals("") && !doesPlayerExist(playerID, myContext)) {
            setDefaultUsername(getPlayerList(myContext));
            addPlayer(playerID, myContext);
        } else if (!doesPlayerExist(playerID, myContext)) {
            setUsername(name);
            addPlayer(playerID, myContext);
        }
    }

    public static boolean checkIfDisplayInterstitial(Context context) {
        SharedPreferences foxPreferencesStatic = context.getSharedPreferences(PREF_FILE_NAME_STATIC, MODE_PRIVATE);
        SharedPreferences.Editor editor = foxPreferencesStatic.edit();
        int count = foxPreferencesStatic.getInt(INTERSTITIAL_COUNT_KEY, 0);
        ++count;
        editor.putInt(INTERSTITIAL_COUNT_KEY, count);
        editor.apply();
        if (count % 3 == 0) {
            return true;
        }
        return false;
    }

    public UUID getPlayerID() {
        return playerID;
    }

    public static boolean doesPlayerExist(UUID ID, Context context) {
        SharedPreferences foxPreferencesStatic = context.getSharedPreferences(PREF_FILE_NAME_STATIC, MODE_PRIVATE);
        int namedPlayerCount = getNamedPlayerCount(context);
        for (int x = 0; x < namedPlayerCount; x++) {
            String KEY = ID_KEY_PREFIX + x;
            String IDString = foxPreferencesStatic.getString(KEY, "Unknown");
            if (IDString.equals(ID.toString())) {
                return true;
            }
        }
        return false;
    }

    // Create a new player if the name is unique
    static void addPlayer(UUID playerID, Context myContext) {
        SharedPreferences foxPreferencesStatic = myContext.getSharedPreferences(PREF_FILE_NAME_STATIC, MODE_PRIVATE);
        SharedPreferences.Editor editor = foxPreferencesStatic.edit();
        String KEY = ID_KEY_PREFIX + getNamedPlayerCount(myContext);
        editor.putString(KEY, playerID.toString());
        editor.apply();
        namedPlayerCountUp(myContext);
    }

    // Increase count of number of named players
    private static void namedPlayerCountUp(Context myContext) {
        int countIncorrect = getNamedPlayerCount(myContext);
        countIncorrect += 1;
        setNamedPlayerCount(myContext, countIncorrect);
    }

    // TODO: Should just get size() of namedPlayers, instead of storing count separately. while(key_ID != NON_EXISTANT)
    private static void setNamedPlayerCount(Context myContext, int count) {
        SharedPreferences foxPreferences = myContext.getSharedPreferences(PREF_FILE_NAME_STATIC, MODE_PRIVATE);
        SharedPreferences.Editor editor = foxPreferences.edit();
        editor.putInt(NAMED_PLAYER_COUNT_KEY, count);
        editor.apply();
    }

    public static int getNamedPlayerCount(Context myContext) {
        SharedPreferences foxPreferences = myContext.getSharedPreferences(PREF_FILE_NAME_STATIC, MODE_PRIVATE);
        return foxPreferences.getInt(NAMED_PLAYER_COUNT_KEY, 0);
    }

    public static PlayerIdentity getPlayer1Identity(Context myContext) {
        String KEY = ID_KEY_PREFIX + 0;       // Player 1 is 0
        String PlayerIDString = myContext.getSharedPreferences(PREF_FILE_NAME_STATIC, MODE_PRIVATE).getString(KEY, NON_EXISTANT);
        UUID playerID;
        String name;
        if (PlayerIDString.equals(NON_EXISTANT)) {
            name = DEFAULT_NAME;
            GameData p1d = new GameData(myContext, name);
            p1d.setUsername(name);
            playerID = p1d.getPlayerID();
        } else {
            playerID = UUID.fromString(PlayerIDString);
            name = myContext.getSharedPreferences(PREF_FILE_PREFIX + PlayerIDString, MODE_PRIVATE).getString(USERNAME_PREFIX + PlayerIDString, NON_EXISTANT);
        }
        return new PlayerIdentity(playerID, name);
    }

    // Start the game with some generic identities
//    public static ArrayList<PlayerIdentity> fetchSomeIdentities(int amount, Context context) {
//        ArrayList<PlayerIdentity> identities = new ArrayList<>();
//        ArrayList<PlayerIdentity> existingPlayers = getPlayerList(context);
//        for (int i = 0; i < amount; ++i) {
//            String expectantName = "Player " + (i + 2);     // We start from player 2, player 1 is fox
//            PlayerIdentity expectantPlayer = null;
//            for (PlayerIdentity pi : existingPlayers) {
//                if (pi.username.equals(expectantName)) {
//                    expectantPlayer = pi;
//                    break;
//                }
//            }
//            // If player hasn't been found in existing players, create him.
//            if (expectantPlayer == null) {
//                GameData newPlayer = new GameData(context);
//                expectantPlayer = new PlayerIdentity(newPlayer.playerID, newPlayer.getUsername());
//            }
//            identities.add(expectantPlayer);
//        }
//
//        return identities;
//    }

    public static ArrayList<PlayerIdentity> getPlayerList(Context myContext) {
        SharedPreferences foxPreferences = myContext.getSharedPreferences(PREF_FILE_NAME_STATIC, MODE_PRIVATE);
        ArrayList<PlayerIdentity> playerList = new ArrayList<>();
        int namedPlayerCount = getNamedPlayerCount(myContext);
        for (int i = 0; i < namedPlayerCount; ++i) {
            String KEY = ID_KEY_PREFIX + i;
            String stringID = foxPreferences.getString(KEY, NON_EXISTANT);
            UUID ID;
            if (!stringID.equals(NON_EXISTANT)) {
                ID = UUID.fromString(stringID);
            } else {
                throw new IllegalStateException();
            }
            String PREF_FILE_NAME = PREF_FILE_PREFIX + stringID;
            SharedPreferences userFoxPreferences = myContext.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
            String name = userFoxPreferences.getString(USERNAME_PREFIX + ID.toString(), NON_EXISTANT);
            FoxRank rank = determineRankValue(userFoxPreferences.getInt(HIGHEST_SCORE_PREFIX + ID, 0));
// Log
            playerList.add(new PlayerIdentity(ID, name, rank));
        }
        if (playerList.size() == 0) {
            playerList.add(getPlayer1Identity(myContext));
        }
        return playerList;
    }

    // TODO: This is unnecessary. No longer allow auto naming e.g Player 2, player 3, etc..
    public static ArrayList<PlayerIdentity> getNamedPlayerList(Context myContext) {
        ArrayList<PlayerIdentity> allPlayers = getPlayerList(myContext);
        ArrayList<PlayerIdentity> namedPlayers = new ArrayList<>();
        String pattern = "Player\\s\\d";
        for (PlayerIdentity player : allPlayers) {
            if (!player.username.matches(pattern)) {
                namedPlayers.add(player);
            }
        }
        return namedPlayers;
    }


    public void setUsername(String nameEntered) {
        editor.putString(USERNAME_KEY, nameEntered);
        editor.apply();
    }

    private void setDefaultUsername(ArrayList<PlayerIdentity> playerList) {
        String suggestedName = "Player 2";
        if (playerList != null && playerList.size() > 0) {
            Set<String> uniqueNames = new HashSet<>();
            for (PlayerIdentity p : playerList) {
                uniqueNames.add(p.username);
            }
            for (int i = 0; i <= playerList.size(); ++i) {
                suggestedName = "Player " + (i + 2);
                if (!uniqueNames.contains(suggestedName)) {
                    break;
                }
            }
        }
        setUsername(suggestedName);
    }

    public String getUsername() {
        String defaultName = DEFAULT_NAME;
        return foxPreferences.getString(USERNAME_KEY, defaultName);
    }

    public static String getUsername(UUID playerID, Context context) {
        return context.getSharedPreferences(PREF_FILE_PREFIX + playerID, MODE_PRIVATE).getString(USERNAME_PREFIX + playerID, DEFAULT_NAME);
    }

    public static PlayerIdentity getIdentity(UUID playerID, Context context) {
        return new PlayerIdentity(playerID, getUsername(playerID, context));
    }

    // BEST_WORDS_KEY
    // Set of most picked words during the most recent game played
    public void setBestGame(ArrayList<String> lettersBestGame, ArrayList<String> wordsBestGame) {
        setBestWords(wordsBestGame);
        setLettersBestGame(lettersBestGame);
    }

    private void setLettersBestGame(ArrayList<String> lettersBestGame) {
        for (int i = 0; i < lettersBestGame.size(); ++i) {
            String BEST_LETTERS_KEY_i = BEST_LETTERS_KEY + "_" + i;     // TODO: Incorporate underscore into KEY
            editor.putString(BEST_LETTERS_KEY_i, lettersBestGame.get(i));
            editor.apply();
        }
    }

    public void setBestWords(ArrayList<String> bestWords) {
        for (int i = 0; i < bestWords.size(); ++i) {
            String BEST_WORDS_KEY_i = BEST_WORDS_KEY + "_" + i;
            editor.putString(BEST_WORDS_KEY_i, bestWords.get(i));
            editor.apply();
        }
    }

    public ArrayList<String> getBestWords() {
        ArrayList<String> bestWords = new ArrayList<>();
        for (int i = 0; i < GameInstance.getNumberRounds(); ++i) {
            String BEST_WORDS_KEY_i = BEST_WORDS_KEY + "_" + i;
            bestWords.add(foxPreferences.getString(BEST_WORDS_KEY_i, NON_EXISTANT));
        }
        return bestWords;
    }

    public ArrayList<String> getBestLetters() {
        ArrayList<String> bestLetters = new ArrayList<>();
        for (int i = 0; i < GameInstance.getNumberRounds(); ++i) {
            String BEST_LETTERS_KEY_i = BEST_LETTERS_KEY + "_" + i;
            bestLetters.add(foxPreferences.getString(BEST_LETTERS_KEY_i, NON_EXISTANT));
        }
        return bestLetters;
    }

    // Set of most picked words during the most recent game played
    public void setRecentWords(ArrayList<String> words) {
        for (int i = 0; i < words.size(); ++i) {
            String RECENT_WORDS_KEY_i = RECENT_WORDS_KEY + "_" + i;
            editor.putString(RECENT_WORDS_KEY_i, words.get(i));
            editor.apply();
        }
    }

    public ArrayList<String> getRecentWords() {
        ArrayList<String> recentWords = new ArrayList<>();
        for (int i = 0; i < GameInstance.getNumberRounds(); ++i) {
            String RECENT_WORDS_KEY_i = RECENT_WORDS_KEY + "_" + i;
            recentWords.add(foxPreferences.getString(RECENT_WORDS_KEY_i, "None Found!"));
        }
        return recentWords;
    }

    // Get the ID of the most recently played game
    public void setRecentGame(UUID recentGameID) {
        editor.putString(RECENT_GAME_ID_KEY, recentGameID.toString());
        editor.apply();
    }

    public String getRecentGame() {
        return foxPreferences.getString(RECENT_GAME_ID_KEY, "");
    }

    public String getAverageWordLength() {
        int totalWordCount = 0;
        double result = 0.0;
        int timesSubmitted = getSubmittedCorrectCount();
        for (int i = 3; i < 10; i++) {
            totalWordCount += i * findOccurence(i);
        }
        if (timesSubmitted > 0) {
            result = (double) totalWordCount / timesSubmitted;
        }
        return String.format("%.2f", result);
    }

    public String getAverageFinalWordLength() {
        int rounds = getRoundCount();
        int totalSoFar = getTotalBestWordLength();
        float result = ((float) totalSoFar) / rounds;
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
        int highScore = getHighestTotalScore();
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

    public void setProfilePicture(String picture) {
        editor.putString(PROFILE_PIC_KEY, picture);
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
// Log
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

    public void addToBestWordLength(String word) {
        int count = getTotalBestWordLength();
        count += word.length();
        editor.putInt(BEST_WORD_TOTAL_COUNT, count);
    }

    public int getTotalBestWordLength() {
        return foxPreferences.getInt(BEST_WORD_TOTAL_COUNT, 0);
    }

    public String findLongest() {
        return foxPreferences.getString(LONGEST_WORD_KEY, NON_EXISTANT);
    }

    public int findOccurence(int requestLength) {
        return foxPreferences.getInt(Integer.toString(requestLength), 0); // Find number of occurences of a particular length
    }

    public static FoxRank determineRankValue(int score) {
        if (score < 12) {
            return new FoxRank(R.drawable.woodfoxcoloured, "Wood Fox");
        } else if (score < 15) {
            return new FoxRank(R.drawable.palefoxsilcoloured, "Pale Fox");
        } else if (score < 17) {
            return new FoxRank(R.drawable.kitfoxsilcoloured, "Kit Fox");
        } else if (score < 20) {
            return new FoxRank(R.drawable.grayfoxsilcoloured, "Gray Fox");
        } else if (score < 23) {
            return new FoxRank(R.drawable.arcticfoxsilcoloured, "Arctic Fox");
        } else if (score < 24) {
            return new FoxRank(R.drawable.silverfoxsilcoloured, "Silver Fox");
        } else {
            return new FoxRank(R.drawable.redfoxsilcoloured, "Red Fox");
        }
    }

    public FoxRank getHighRank() {
        return determineRankValue(getHighestTotalScore());
    }
}

