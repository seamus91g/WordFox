package com.example.seamus.wordfox.RoundResults;

import android.graphics.Bitmap;

import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.GameInstance;
import com.example.seamus.wordfox.GridImage;
import com.example.seamus.wordfox.ImageHandler;
import com.example.seamus.wordfox.WordfoxConstants;

import java.util.List;

/**
 * Created by Gilroy
 */

public class RoundEndPresenter {
    private static final String TAG = "RoundEndPresenter";
    private static int PROFILE_PIC_SCREEN_WIDTH_PERCENT = 30;
    private static int PLAYER_RESULT_GRID_SCREEN_WIDTH_PERCENT = 30;
    private RoundEndContract.View view;
    private GameInstance gameInstance;
    private int gridWidth;
    private int profilePicScreenWidth;
    private int resultGridWidth;
    private final int colorPrimary;
    private final int colorSecondary;
    private boolean isStarted = false;

    public RoundEndPresenter(RoundEndContract.View view, int screenWidth, GameInstance gameInstance, int colorPrimary, int colorSecondary) {
        this.view = view;
        this.gameInstance = gameInstance;
        this.gridWidth = (screenWidth * WordfoxConstants.RESULT_GRID_SCREEN_WIDTH_PERCENT) / 100;
        this.profilePicScreenWidth = (screenWidth * PROFILE_PIC_SCREEN_WIDTH_PERCENT) / 100;
        this.resultGridWidth = (screenWidth * PLAYER_RESULT_GRID_SCREEN_WIDTH_PERCENT) / 100;
        this.colorPrimary = colorPrimary;
        this.colorSecondary = colorSecondary;
    }

    public void startGame() {
        if (isStarted) {
            return;
        }
        isStarted = true;
        gameInstance.incrementRound();
        // Start new round if the current round is not the last round
        if (gameInstance.getRound() < WordfoxConstants.NUMBER_ROUNDS) {
            view.nextRound();
        } else {
            // If Last Round but there are still players to play -> launch the PlayerSwitchActivity
            gameInstance.gamestateFinished();
            if (view.playerSwitch()) {
                return;
            }
            // Only start the end of game screen if the current player has played all their rounds
            // and there's no player still yet to play
            view.proceedToFinalResults();
        }
    }

    public void populatePossibleWords() {
        List<String> possibleWords = gameInstance.getSuggestedWordsOfRound(gameInstance.getRound());
        if (possibleWords.size() <= 0) {
            return;
        }
        Bitmap gridBmp = view.getBlankScaledGrid(gridWidth / 4);  // TODO: is 1/4 the appropriate amount?

        int count = 0;
        int requiredRows = ((possibleWords.size() - 1) / 3) + 1;
        for (int i = 0; i < requiredRows; ++i) {
            view.addRowPossibleWords();         // TODO: Model should add the row itself
            for (int j = 0; j < WordfoxConstants.RESULT_GRIDS_PER_ROW; ++j) {
                if (count >= possibleWords.size()) {
                    view.hideResultGrid(count, gridBmp.getWidth());
                    ++count;
                    continue;
                }
                String word = possibleWords.get(count).toUpperCase() + " (" + possibleWords.get(count).length() + ")";
                GridImage gridWithText = new GridImage(gridBmp, word, gameInstance.getRoundLetters(), colorPrimary, colorSecondary);
                view.addPossibleWord(gridWithText.getBmp(), word, count);
                ++count;
            }
        }
    }

    public void populatePlayerDetails() {       // TODO:  Tidy this. Use MVP
        Bitmap profPic = view.getPlayerProfPic(profilePicScreenWidth);
        view.setPlayerProfilePic(profPic);

        allowWordSearchToFinish();

        int maxScore = gameInstance.getLongestPossible().length();
        int playerScore = gameInstance.getScore();
        int percentScore = (100 * playerScore) / (maxScore);

        String nameAndPercent = gameInstance.getName() + "\n (" + percentScore + "%)";
        view.setPlayerNameWithPercent(nameAndPercent);

        String scoreText = gameInstance.getLongestWord() + " (" + gameInstance.getLongestWord().length() + ")";
        view.setPlayerScoreText(scoreText);

        String playerBubbleText = "You scored " + playerScore + " out of " + maxScore;
        view.setSpeechBubbleText(playerBubbleText);

        Bitmap gridBmp = view.getBlankScaledGrid(resultGridWidth);
        GridImage gridWithText = new GridImage(gridBmp, gameInstance.getLongestWord().toUpperCase(), gameInstance.getRoundLetters(), colorPrimary, colorSecondary);

        view.setMyGridResult(gridWithText.getBmp());
    }

    // Race condition: if user ends game really quickly, longest possible words might not yet be calculated.
    private void allowWordSearchToFinish() {
        while (gameInstance.getLongestPossible() == null) {     // TODO: Infinite wait? Refactor -> Exit game
            try {
                wait(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}
