package capsicum.game.wordfox.results_screen;

import android.util.Log;

import capsicum.game.wordfox.GameData;
import capsicum.game.wordfox.GameInstance;
import capsicum.game.wordfox.database.FoxSQLData;
import capsicum.game.wordfox.database.PlayerStatsTable;
import capsicum.game.wordfox.datamodels.GameItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Gilroy
 */

public class ResultsPresenter {
    private static final String TAG = "ResultsPresenter";
    private final FoxSQLData foxData;
    private ArrayList<GameInstance> gameInstances;
    private ResultsContract.View view;
    private int playerCount;
    private HashMap<Boolean, ArrayList<GameInstance>> playerGroups;

    public ResultsPresenter(ResultsContract.View view, int playerCount,
                     FoxSQLData foxData, ArrayList<GameInstance> gameInstances) {
        this.view = view;
        this.playerCount = playerCount;
        this.foxData = foxData;
        this.gameInstances = gameInstances;
        foxData.open();
        playerGroups = sortWinnersLosers(gameInstances);
    }

    public void updateData() {
        // Separate winners and losers into two lists
        ArrayList<GameInstance> winners = playerGroups.get(Boolean.TRUE);
        if (playerCount > 1) {
            // Increment individual win/draw counters
            if (winners.size() > 1) {
                // Game was a draw if the winner group contains more than one player
                for (GameInstance player : winners) {
                    foxData.updatePlayerStats(player.getID(), PlayerStatsTable.COLUMN_DRAWS);
                }
            } else {
                foxData.updatePlayerStats(winners.get(0).getID(), PlayerStatsTable.COLUMN_WINS);
            }
            // Increment lose counters
            for (GameInstance player : playerGroups.get(Boolean.FALSE)) {
                foxData.updatePlayerStats(player.getID(), PlayerStatsTable.COLUMN_LOSES);
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
                    foxData.updateOpponentItem(winner, loser, draw);
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
            } else {
// Log
            }
        }
        GameItem thisGameDetails = gameitemFromInstances(winners);
        foxData.createGameItem(thisGameDetails);
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
