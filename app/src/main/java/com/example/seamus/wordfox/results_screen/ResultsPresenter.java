package com.example.seamus.wordfox.results_screen;

import android.util.Log;

import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.GameInstance;
import com.example.seamus.wordfox.database.FoxSQLData;
import com.example.seamus.wordfox.database.PlayerStatsTable;
import com.example.seamus.wordfox.datamodels.GameItem;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Gilroy
 */

public class ResultsPresenter {
    private static final String TAG = "ResultsPresenter";
    private final FoxSQLData foxData;
    private ArrayList<GameInstance> gameInstances;
    private ResultsContract.View view;
    private boolean isGameOver;
    private int playerCount;
    private HashMap<Boolean, ArrayList<GameInstance>> playerGroups;
    private ArrayList<GameInstance> winners;

    ResultsPresenter(ResultsContract.View view, boolean isGameOver, int playerCount,
                     FoxSQLData foxData, ArrayList<GameInstance> gameInstances) {
        this.view = view;
        this.isGameOver = isGameOver;
        this.playerCount = playerCount;
        this.foxData = foxData;
        this.gameInstances = gameInstances;
        foxData.open();
        playerGroups = sortWinnersLosers(gameInstances);
        winners = playerGroups.get(Boolean.TRUE);
    }

    // 'GAME OVER' for the end of a game or 'TIME UP' for the end of a round
    public void populateHeaderMsg() {
        String gameOverMessage;
        if (!isGameOver) {
            gameOverMessage = "TIME UP!";
        } else {
            if (playerCount > 1) {
                gameOverMessage = "GAME OVER!";
            } else {
                // If one player, say 'GAME OVER' + name
                String playername = gameInstances.get(0).getPlayerID();
                gameOverMessage = "GAME OVER " + playername.toUpperCase();
            }
        }
        view.setGameOverMessage(gameOverMessage);
    }

    // Display who won the game and what their score was
    public void populateResultLL() {
        int steps;
        if (isGameOver) {
            steps = playerCount;
        } else {
            steps = 1;
        }
        //for each player add a line stating the score they got
        for (int k = 0; k < steps; k++) {
            String greeting;
            int totalScore;
            int maxScore;
            // One Player ->    "You scored"
            // Multiplayer ->   "#name scored"
            if (!isGameOver || (playerCount == 1)) {
                greeting = "You scored ";
            } else {
                //get the player name, form a greeting
                greeting = gameInstances.get(k).getPlayerID() + " scored ";
            }
            if (!isGameOver) {
                GameInstance myGameInstance = gameInstances.get(0);
                totalScore = myGameInstance.getRoundWord(myGameInstance.getRound()).length();
                maxScore = myGameInstance.getRoundLongestPossible(myGameInstance.getRound()).length();
            } else {
                GameInstance eachGameInstance = gameInstances.get(k);
                totalScore = eachGameInstance.getTotalScore();
                maxScore = eachGameInstance.getHighestPossibleScore();
            }
            //set the text of the resultsRatioTV on the end screen to show the users points
            String resultsRatio = String.valueOf(totalScore) + " out of " + String.valueOf(maxScore) + " = ";
            //declare and initialise a double for the percent success at the end of the game
            Double successPercent = ((double) totalScore / (double) maxScore) * 100;
            String resultsPercent = String.valueOf(successPercent.intValue()) + "%";
            //assemble the result to contain all the components and show in the TextView
            String result = greeting + resultsRatio + resultsPercent;
            view.addTVtoResults(result);
        }
    }

    // Populate header, results
    public void endOfRoundOrGameResults() {
        if (isGameOver) {
            // Declare the winner, or multiple winners if draw
            if (playerCount > 1) {
                StringBuilder victoryMessage;
                if (winners.size() == 1) {
                    String winnerName = winners.get(0).getPlayerID();
                    if (winnerName.equals(GameData.DEFAULT_P1_NAME)) {
                        winnerName = view.defaultP1Name();
                    }
                    victoryMessage = new StringBuilder("Winner is " + winnerName + "!");
                } else {
                    //if there's more than one name in the list of winners then it was a draw
                    victoryMessage = new StringBuilder("It was a draw between ");
                    for (int f = 0; f < winners.size(); f++) {
                        victoryMessage.append(winners.get(f).getPlayerID());
                        if (f < (winners.size() - 1)) {
                            victoryMessage.append(" and ");
                        } else {
                            victoryMessage.append("!");
                        }
                    }
                }
                view.setVictoryMessage(victoryMessage.toString());
            }
        }
    }

    public void updateData() {
        if (!isGameOver) {    // Only update data at game end
            return;
        }
        // Separate winners and losers into two lists
        ArrayList<GameInstance> winners = playerGroups.get(Boolean.TRUE);
        if (playerCount > 1) {
            // Increment individual win/draw counters
            if (winners.size() > 1) {
                // Game was a draw if the winner group contains more than one player
                for (GameInstance player : winners) {
                    foxData.updatePlayerStats(player.getPlayerID(), PlayerStatsTable.COLUMN_DRAWS);
                }
            } else {
                foxData.updatePlayerStats(winners.get(0).getPlayerID(), PlayerStatsTable.COLUMN_WINS);
            }
            // Increment lose counters
            for (GameInstance player : playerGroups.get(Boolean.FALSE)) {
                foxData.updatePlayerStats(player.getPlayerID(), PlayerStatsTable.COLUMN_LOSES);
            }
            // Loop through player and register win/lose/draw respective to every other player
            for (GameInstance player : gameInstances) {
                for (GameInstance opponent : gameInstances) {
                    String winner = player.getPlayerID(), loser = opponent.getPlayerID();
                    if (winner.equals(loser)) {
                        continue;
                    }
                    boolean draw = false;
                    if (player.getTotalScore() == opponent.getTotalScore()) {
                        draw = true;
                    } else if (player.getTotalScore() < opponent.getTotalScore()) {
                        winner = opponent.getPlayerID();
                        loser = player.getPlayerID();
                    }
                    foxData.updateOpponentItem(winner, loser, draw);
                }
            }
        }
//        else {
//            winners.add(gameInstances.get(0)); // addAll();
//        }
        // Store most recent words for each player
        // Store most recent Game ID
        for (GameInstance pgi : gameInstances) {
            GameData plyrGd = view.getPlayerData(pgi.getPlayerID());
            plyrGd.setRecentGame(pgi.getRoundID(0));
            plyrGd.setRecentWords(pgi.getAllFinalWords());
            if (plyrGd.getHighestTotalScore() <= pgi.getTotalScore()) {
                plyrGd.setBestWords(pgi.getAllFinalWords());
                plyrGd.setHighestScore(pgi.getTotalScore());
            } else {
                Log.d(TAG, "Not best words found! This score: " + pgi.getTotalScore() + ", Highest: " + plyrGd.getHighestTotalScore());
            }
        }
        GameItem thisGameDetails = gameitemFromInstances(winners);
        foxData.createGameItem(thisGameDetails);
    }

    public void createRoundSummary() {
        int rounds;
        int players;
        int ongoingRound;
        if (isGameOver) {
            rounds = GameInstance.NUMBER_ROUNDS;
            players = playerCount;
            view.displayTitle("Final Results");
            view.prepareHomeButton();
            ongoingRound = 0;
        } else {
            rounds = 1;
            players = 1;
            ongoingRound = gameInstances.get(0).getRound();
            String titleMsg = "Round " + String.valueOf(ongoingRound + 1) + " Score";
            view.displayTitle(titleMsg);
            view.prepareContinueButton();
        }
        // if End -> print results of each round, Else -> print results previous round only
        for (int round = 0; round < rounds; round++) {      // TODO: round? ongoingRound? curRound? Tidy this
            String roundNo = "Round " + String.valueOf(ongoingRound + 1) + ": ";
            view.addResultSpacer();
            view.addResultHeading(roundNo);
            // Different suggestions if entire game or just round is over
            String suggestionTitle;
            if (rounds == 1) {
                suggestionTitle = "Possible words: ";
            } else {
                suggestionTitle = "Best word: ";
            }
            view.addResultHeading(suggestionTitle);
            // create a TextView displaying the letters for the round
            String letters = getRoundLetters(ongoingRound);
            view.addResultValue(letters);
            // If 'Round End' screen, Print list of suggested words for various lengths
            // If 'Game End' screen, Print just one longest word
            if (rounds == 1) {      // TODO: Abstract this
                ArrayList<String> suggestedWords;
                GameInstance thisGameInstance = gameInstances.get(0);
                int curRound = thisGameInstance.getRound();     // TODO: curRound same as round? Ed: same as ongoingRound
                suggestedWords = thisGameInstance.getSuggestedWordsOfRound(curRound);
                for (int i = 0; i < suggestedWords.size(); ++i) { // String w : suggestedWords){
                    String w = suggestedWords.get(i);
                    String suggestedToPlayer = w.toUpperCase() +
                            " (" + w.length() + ")";
                    view.addResultValue(suggestedToPlayer);
                    if(i > 0){
                        view.addResultHeading(" ");
                    }
                }
            } else {
                String wordSuggestion = getRoundOrGameBestPossibleWord(ongoingRound);
                view.addResultValue(wordSuggestion);
            }
            // Display best word found of each player
            for (int playerNum = 0; playerNum < players; playerNum++) {
                String name = gameInstances.get(playerNum).getPlayerID();
                view.addResultHeading(name + ": ");
                //get their best guess
                String bestGuess = getRoundOrGameBestGuess(playerNum, ongoingRound);
                view.addResultValue(bestGuess, "Player longest word");
            }
            ++ongoingRound;
        }
    }

    public void startGame(GameInstance gameInstance) {
        gameInstance.incrementRound();
        int index = gameInstance.getThisGameIndex();
        // Start new round if the current round is not the last round
        if (gameInstance.getRound() < GameInstance.NUMBER_ROUNDS) {
            view.nextRound(index);
        } else {
            // If Last Round but there are still players to play -> launch the PlayerSwitchActivity
            gameInstance.gamestateFinished();
            if (view.playerSwitch()) {
                return;
            }
            // Only start the end of game screen if the current player has played all their rounds
            // and there's no player still yet to play
            view.proceedToFinalResults(index);
        }
    }

    // Get the players best found word for the previous round. Also show the length
    private String getRoundOrGameBestGuess(int playerNumber, int round) {
        GameInstance currentPlayer = gameInstances.get(playerNumber);
        String Guess = currentPlayer.getRoundWord(round);
        return Guess + " (" + String.valueOf(Guess.length()) + ")";
    }

    // Get the best possible word for a particular round
    private String getRoundOrGameBestPossibleWord(int round) {
        String bestPossibleWord;
        GameInstance player1GameInstance = gameInstances.get(0);
        String bestWordPoss = player1GameInstance.getRoundLongestPossible(round);
        bestPossibleWord = bestWordPoss + " (" + String.valueOf(bestWordPoss.length()) + ")";
        return bestPossibleWord;
    }

    private String getRoundLetters(int round) {
        return gameInstances.get(0).getLetters(round);
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
            winNames.append(g.getPlayerID());
            winNames.append(", ");
        }
        String ww1 = winWords1.length() > 0 ? winWords1.substring(0, winWords1.length() - 2) : "";
        String ww2 = winWords2.length() > 0 ? winWords2.substring(0, winWords2.length() - 2) : "";
        String ww3 = winWords3.length() > 0 ? winWords3.substring(0, winWords3.length() - 2) : "";
        String wn = winNames.length() > 0 ? winNames.substring(0, winNames.length() - 2) : "";

        GameInstance myGameInstance = gameInstances.get(0);
        String round1Id = myGameInstance.getRoundID(0);
        String round2Id = myGameInstance.getRoundID(1);
        String round3Id = myGameInstance.getRoundID(2);
        GameItem thisGame = new GameItem(
                round1Id, round2Id, round3Id, ww1, ww2, ww3, wn, playerCount
        );
        return thisGame;
    }

    // Get a String of comma separated values from an Array List
    private <T> String getCsl(ArrayList<T> vals) {
        StringBuilder strBl = new StringBuilder();
        for (T string : vals) {
            strBl.append(String.valueOf(string));
            strBl.append(",");
        }
        return strBl.length() > 0 ? strBl.substring(0, strBl.length() - 1) : "";
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
