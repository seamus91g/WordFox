package capsicum.game.wordfox.screen_results_game;

import android.graphics.Bitmap;
import capsicum.game.wordfox.BuildConfig;
import capsicum.game.wordfox.GameData;
import capsicum.game.wordfox.GameDetails;
import capsicum.game.wordfox.recyclerview_game_results.PlayerResultPackage;
import capsicum.game.wordfox.GameInstance;
import capsicum.game.wordfox.WordfoxConstants;
import capsicum.game.wordfox.database.FoxSQLData;
import capsicum.game.wordfox.database.PlayerStatsTable;
import capsicum.game.wordfox.datamodels.GameItem;
import capsicum.game.wordfox.screen_profile.FoxRank;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Created by Gilroy
 */

public class ResultsPresenter {
    private static final float PROFILE_PIC_SCREEN_WIDTH_PERCENT = 0.2f;
    private static final float GAME_END_FOX_WIDTH_PERCENT = 0.4f;
    private static final float GAME_END_SPEECH_WIDTH_PERCENT = 0.59f;
    private static final float GRID_SPACER_PERCENT = 0.1f;
    private final FoxSQLData foxDatabase;
    private final ArrayList<GameInstance> gameInstances;
    private final ResultsContract.View view;
    private final HashMap<Boolean, ArrayList<GameInstance>> playerGroups;
    private Bitmap defaultProfilePic;
    private final int screenWidth;
    private final int playerCount;

    public ResultsPresenter(ResultsContract.View view, int playerCount,
                            FoxSQLData foxDatabase,
                            ArrayList<GameInstance> gameInstances,
                            int screenWidth) {
        this.view = view;
        this.playerCount = playerCount;
        this.foxDatabase = foxDatabase;
        this.gameInstances = gameInstances;
        this.screenWidth = screenWidth;
        foxDatabase.open();
        playerGroups = sortWinnersLosers(gameInstances);
    }

    public void setupEndgameFox() {
        Bitmap foxBitmap = view.getGameEndFoxBmp((int) (GAME_END_FOX_WIDTH_PERCENT * screenWidth));
        Bitmap foxSpeechBitmap = view.getGameEndFoxSpeechBmp((int) (GAME_END_SPEECH_WIDTH_PERCENT * screenWidth));
        String winner = "";
        if (gameInstances.size() > 1 && playerGroups.containsKey(true) && playerGroups.get(true).size() > 0) {
            int highScore = playerGroups.get(true).get(0).getTotalScore();
            winner = "Winner is " + playerGroups.get(true).get(0).getName() + "!" + "\n" + "They scored " + highScore + " out of " + gameInstances.get(0).getHighestPossibleScore();
        } else {
            winner = "GAME OVER!\n" + "You scored\n" + gameInstances.get(0).getTotalScore() + " out of " + gameInstances.get(0).getHighestPossibleScore();
        }
        view.displayEndgameFox(foxBitmap, foxSpeechBitmap, winner);
    }

    public void setupBannerAd() {
        String adUnit;
        if (BuildConfig.DEBUG) {
            adUnit = WordfoxConstants.TEST_AD_BANNER;
        } else {
            adUnit = WordfoxConstants.GAME_END_BANNER_AD_UNIT_ID;
        }
        view.displayBannerAd(adUnit);
    }

    public void setupBestwordHeader() {
        GameInstance gameInstance = gameInstances.get(0);
        String longestPossibleWords[] = new String[3];
        longestPossibleWords[0] = gameInstance.getRoundLongestPossible(0).toUpperCase() + "(" + gameInstance.getRoundLongestPossible(0).length() + ")";
        longestPossibleWords[1] = gameInstance.getRoundLongestPossible(1).toUpperCase() + "(" + gameInstance.getRoundLongestPossible(1).length() + ")";
        longestPossibleWords[2] = gameInstance.getRoundLongestPossible(2).toUpperCase() + "(" + gameInstance.getRoundLongestPossible(2).length() + ")";
        int wordTextviewWidth = screenWidth / 3;
        view.displayWordHeaders(longestPossibleWords, wordTextviewWidth);
    }

    public void setupResultSection() {
        List<PlayerResultPackage> players = new ArrayList<>();
        for (int i = 0; i < gameInstances.size(); ++i) {
            players.add(prepareResultDetail(gameInstances.get(i)));
        }
        int gridWidth = (int) (screenWidth * WordfoxConstants.RESULT_GRID_SCREEN_WIDTH_PERCENT);
        int spacerSize = (int) ((screenWidth - gridWidth * 3) / ((float) 6));
        List<String[]> gameLetters = new ArrayList<>();
        for (String roundLetters : gameInstances.get(0).getLetters()) {
            gameLetters.add(Arrays.copyOfRange(
                    roundLetters.split(""), 1, roundLetters.length() + 1));
        }
        view.prepareResultAdapter(players, gameLetters, gameInstances.get(0).getHighestPossibleScore(), gridWidth, spacerSize);
    }

    public PlayerResultPackage prepareResultDetail(GameDetails game) {
        // Convert game instances into Dataset for the adapter.
        FoxRank foxRank = GameData.determineRankValue(game.getTotalScore());
        Bitmap foxRankBmp = view.getRankBmp(foxRank.imageResource, (int) (PROFILE_PIC_SCREEN_WIDTH_PERCENT * screenWidth));
        PlayerResultPackage p = new PlayerResultPackage(game.getAllFinalWords(),
                game.getName(),
                game.getTotalScore(),
                getPlayerProfilePic(game.getID()),
                foxRankBmp,
                foxRank.foxRank);
        return p;
    }

    private Bitmap getPlayerProfilePic(UUID id) {
        Bitmap profPic = foxDatabase.getProfileIcon(id);
        if (profPic == null) {
            profPic = view.loadDefaultProfilePic((int) (WordfoxConstants.PROFILE_ICON_SCREEN_WIDTH_PERCENT * screenWidth));
        }
        return profPic;
    }

    public void updateData() {
        // Separate winners and losers into two lists
        ArrayList<GameInstance> winners = playerGroups.get(Boolean.TRUE);
        if (playerCount > 1) {
            // Increment individual win/draw counters
            if (winners.size() > 1) {
                // Game was a draw if the winner group contains more than one player
                for (GameInstance player : winners) {
                    foxDatabase.updatePlayerStats(player.getID(), PlayerStatsTable.COLUMN_DRAWS);
                }
            } else {
                foxDatabase.updatePlayerStats(winners.get(0).getID(), PlayerStatsTable.COLUMN_WINS);
            }
            // Increment lose counters
            for (GameInstance player : playerGroups.get(Boolean.FALSE)) {
                foxDatabase.updatePlayerStats(player.getID(), PlayerStatsTable.COLUMN_LOSES);
            }
            // Loop through player and register win/lose/draw respective to every other player
            for (GameInstance player : gameInstances) {
                for (GameInstance opponent : gameInstances) {
                    UUID winner = player.getID(), loser = opponent.getID();
                    if (winner.equals(loser)) {
                        continue;
                    }
                    boolean draw = false;
                    if (player.getTotalScore() == opponent.getTotalScore()) {
                        draw = true;
                    } else if (player.getTotalScore() < opponent.getTotalScore()) {
                        winner = opponent.getID();
                        loser = player.getID();
                    }
                    foxDatabase.updateOpponentItem(winner, loser, draw);
                }
            }
        }
        for (GameInstance pgi : gameInstances) {
            GameData plyrGd = view.getPlayerData(pgi.getID());
            plyrGd.setRecentGame(pgi.getRoundID(0));
            plyrGd.setRecentWords(pgi.getAllFinalWords());
            if (plyrGd.getHighestTotalScore() <= pgi.getTotalScore()) {
                plyrGd.setBestGame(pgi.getLetters(), pgi.getAllFinalWords());
                plyrGd.setHighestScore(pgi.getTotalScore());
            }
        }
        GameItem thisGameDetails = gameitemFromInstances(winners);
        foxDatabase.createGameItem(thisGameDetails);
    }

    // Create GameItem classes, to facilitate storing relevant data to the sql database
    private GameItem gameitemFromInstances(ArrayList<GameInstance> gInstances) {
        StringBuilder winWords1 = new StringBuilder();
        StringBuilder winWords2 = new StringBuilder();
        StringBuilder winWords3 = new StringBuilder();
        StringBuilder winNames = new StringBuilder();
        for (GameInstance g : gInstances) {
            winWords1.append(g.getRoundWord(0));
            winWords1.append(", ");
            winWords2.append(g.getRoundWord(1));
            winWords2.append(", ");
            winWords3.append(g.getRoundWord(2));
            winWords3.append(", ");
            winNames.append(g.getID());
            winNames.append(", ");
        }
        String ww1 = winWords1.length() > 0 ? winWords1.substring(0, winWords1.length() - 2) : "";
        String ww2 = winWords2.length() > 0 ? winWords2.substring(0, winWords2.length() - 2) : "";
        String ww3 = winWords3.length() > 0 ? winWords3.substring(0, winWords3.length() - 2) : "";
        String wn = winNames.length() > 0 ? winNames.substring(0, winNames.length() - 2) : "";

        GameInstance myGameInstance = gameInstances.get(0);
        UUID round1Id = myGameInstance.getRoundID(0);
        UUID round2Id = myGameInstance.getRoundID(1);
        UUID round3Id = myGameInstance.getRoundID(2);
        GameItem thisGame = new GameItem(
                round1Id, round2Id, round3Id, ww1, ww2, ww3, wn, playerCount
        );
        return thisGame;
    }

    private HashMap<Boolean, ArrayList<GameInstance>> sortWinnersLosers(ArrayList<GameInstance> allPlayers) {
        int maxScore = 0;   // Note: In single player, getting a score of zero will register as a lose
        // Sort players into two groups. Those with the highest score (winners) and those with a lower score (losers)
        // In the hashmap, 'true' is the key for the winners and 'false' is the key for the losers
        HashMap<Boolean, ArrayList<GameInstance>> playersByCategory = new HashMap<>();
        ArrayList<GameInstance> winners = new ArrayList<>();
        ArrayList<GameInstance> losers = new ArrayList<>();

        // Loop, add players to 'loser' group if higher scores are found
        for (GameInstance player : allPlayers) {
            int score = player.getTotalScore();
            if (score > maxScore) {
                maxScore = score;
                losers.addAll(winners);
                winners.clear();
                winners.add(player);
            } else if (score == maxScore) {
                winners.add(player);
            }
        }
        playersByCategory.put(true, winners);
        playersByCategory.put(false, losers);
        return playersByCategory;
    }
}
