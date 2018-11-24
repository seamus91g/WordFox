package capsicum.game.wordfox;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import capsicum.game.wordfox.screen_results_round.WordPresenter;
import timber.log.Timber;

/**
 * Created by Desmond
 */

public class GameInstance implements GameDetails {

    public static final String PLAYER_NAME = "player_name_key";
    public static final String PLAYER_ID = "player_id_key";
    public static final String BEST_WORDS = "best_words_key";
    public final String MONITOR_TAG = "myTag";
    private int numberOfPlayers = 1; // Only different for a Pass & Play game
    private final boolean isOnline;
    private int totalScore;      // total Score tracks the accumulated score across rounds.
    private int score;   // score is just the score from the current round.
    private int round;   // round is a counter for the round of the game.
    private ArrayList<String> allLongestPossible = new ArrayList<>();
    private String longestWord;     // TODO: .. Changes every round. Does it belong in this class??
    private ArrayList<String> letters = new ArrayList<>();
    private ArrayList<ArrayList<String>> wordForEachLengthPerRound = new ArrayList<>();
    private String[] bestWordFoundEachRound = new String[WordfoxConstants.NUMBER_ROUNDS];
    private int highestPossibleScore = 0;
    private final int thisGameIndex;
    private PlayerIdentity player;
    private WordPresenter roundEndWordPresenter;

    private final ArrayList<UUID> roundIDs = new ArrayList<>();
    private boolean isGroupOwner;       // TODO: Not required to store this????

    public static int getNumberRounds() {
        return WordfoxConstants.NUMBER_ROUNDS;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public boolean isGroupOwner() {
        return isGroupOwner;
    }

    public JSONObject resultAsJson() {
        JSONObject gameJSON = new JSONObject();
        try {
            gameJSON.put(BEST_WORDS, new JSONArray(Arrays.asList(bestWordFoundEachRound)));
            gameJSON.put(PLAYER_ID, player.ID);
            gameJSON.put(PLAYER_NAME, player.username);
        } catch (JSONException e) {
            Timber.d("GameInstance : Failed to create json result");
            e.printStackTrace();
        }
        return gameJSON;
    }

    public enum GameState {ONGOING, FINISHED}

    private GameState myGameState;

    // TODO: Unit test to validate scores
    {
        totalScore = 0;         // TODO: These initialisations seem unnecessary since MainActivity clears scores
        score = 0;
        round = 0;
        longestWord = "";       // Longest of the current round
        myGameState = GameState.ONGOING;
    }

    public GameInstance(UUID playerId, String nm, int thisGameIndex, int playerCount) {
        this(playerId, nm, thisGameIndex);
        numberOfPlayers = playerCount;
    }

    public GameInstance(UUID playerId, String nm, int thisGameIndex) {
        this(playerId, nm, thisGameIndex, null, false, false);
    }

    public GameInstance(UUID playerId, String playerName, int thisGameIndex, ArrayList<UUID> roundIDs) {
        this(playerId, playerName, thisGameIndex, roundIDs, false, false);
    }

    public GameInstance(UUID playerId, String nm, int thisGameIndex, boolean isOnline, boolean isGO) {
        this(playerId, nm, thisGameIndex, null, isOnline, isGO);
    }

    public GameInstance(UUID playerId, String playerName, int thisGameIndex, ArrayList<UUID> roundIDs, boolean isOnline, boolean isGO) {
        this.isGroupOwner = isGO;
        if (roundIDs == null) {
            for (int i = 0; i < WordfoxConstants.NUMBER_ROUNDS; i++) {
                this.roundIDs.add(UUID.randomUUID());
            }
        } else {                              // Defensive copying
            this.roundIDs.addAll(roundIDs);
        }
        this.isOnline = isOnline;
        player = new PlayerIdentity(playerId, playerName);
        this.thisGameIndex = thisGameIndex;
    }

    @Override
    public UUID getID() {
        return player.ID;
    }

    public int getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public PlayerIdentity getPlayer() {
        return player;
    }

    public void gamestateFinished() {
        myGameState = GameState.FINISHED;
    }

    public boolean isGameOngoing() {
        return (myGameState.equals(GameState.ONGOING));
    }

    public UUID getRoundID() {
        return roundIDs.get(round);
    }

    public UUID getRoundID(int roundNum) {
        return roundIDs.get(roundNum);
    }

    public ArrayList<UUID> getRoundIDs() {
        return roundIDs;
    }

    @Override
    public String getName() {
        return player.username;
    }

    @Override
    public int getHighestPossibleScore() {
        return highestPossibleScore;
    }

    @Override
    public String getLetters(int roundIndex) {
        return letters.get(roundIndex);
    }

    public String getRoundLetters() {
        if (letters.size() > round) {
            return letters.get(round);
        }
        return null;
    }

    public ArrayList<String> getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters.add(letters);
    }

    public int getThisGameIndex() {
        return thisGameIndex;
    }

    @Override
    public int getTotalScore() {
        return totalScore;
    }

    private void setTotalScore(int point) {
        totalScore += point;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int point) {
        setTotalScore(point - score);
        score = point;
    }

    public void setLongestWord(String word) {
        longestWord = word;
    }

    public String getLongestWord() {
        return longestWord;
    }

    public synchronized void setLongestPossible(String word) {
        allLongestPossible.add(word);
        highestPossibleScore += word.length();
        if (roundEndWordPresenter != null) {
            roundEndWordPresenter.presentWord(word);
        }
    }

    public ArrayList<String> getAllLongestPossible() {
        return allLongestPossible;
    }

    public String getLongestPossible() {
        if (allLongestPossible.size() > round) {
            return allLongestPossible.get(round);
        }
        return null;
    }

    public synchronized String getLongestPossible(WordPresenter presenter) {
        if (getLongestPossible() == null) {
            roundEndWordPresenter = presenter;
            return null;
        } else {
            return getLongestPossible();
        }
    }

    public ArrayList<String> getSuggestedWordsOfRound(int requestedRound) {
        return wordForEachLengthPerRound.get(requestedRound);
    }

    public synchronized void addListOfSuggestedWords(ArrayList<String> suggestedWords) {
        wordForEachLengthPerRound.add(suggestedWords);
        if (roundEndWordPresenter != null) {
            roundEndWordPresenter.presentLongestPossible(suggestedWords);
        }
    }

    public synchronized ArrayList<String> getSuggestedWordsOfRound(WordPresenter presenter) {
        if (getSuggestedWordsOfRound() == null) {
            roundEndWordPresenter = presenter;
            return null;
        } else {
            return getSuggestedWordsOfRound();
        }
    }

    public ArrayList<String> getSuggestedWordsOfRound() {
        if (wordForEachLengthPerRound.size() > round) {
            return wordForEachLengthPerRound.get(round);
        }
        return null;
    }

    public String getRoundLongestPossible(int roundIndex) {
        return allLongestPossible.get(roundIndex);
    }

    public int getRound() {
        return round;
    }

    public void clearAllScores() {
        totalScore = 0;
        score = 0;
        round = 0;
        longestWord = "";
    }

    public void clearRoundScores() {
        score = 0;
        longestWord = "";
    }

    public ArrayList<String> getAllFinalWords() {
        return new ArrayList<String>(Arrays.asList(bestWordFoundEachRound));
    }

    public void setRoundWord(int whichRound, String word) {
        bestWordFoundEachRound[whichRound] = word;
    }

    public void setRoundWord(String word) {
        bestWordFoundEachRound[round] = word;
    }

    @Override
    public String getRoundWord(int whichRound) {
        return bestWordFoundEachRound[whichRound];
    }

    public void incrementRound() {
        roundEndWordPresenter = null;
        round++;
    }

    public int getRoundScore(int whichRound) {
        return bestWordFoundEachRound[whichRound].length();
    }

}
