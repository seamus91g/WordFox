package com.example.seamus.wordfox.RoundResults;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.seamus.wordfox.GameGridAdapter;
import com.example.seamus.wordfox.GameInstance;
import com.example.seamus.wordfox.GridImage;
import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.WordfoxConstants;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;

/**
 * Created by Gilroy
 */

public class RoundEndPresenter {
    private static final String TAG = "RoundEndPresenter";
    private static int PROFILE_PIC_SCREEN_WIDTH_PERCENT = 20;
    private static int PLAYER_RESULT_GRID_SCREEN_WIDTH_PERCENT = 30;
    private static int SPEECH_BUBBLE_SCREEN_WIDTH_PERCENT = 35;
    private FirebaseAnalytics mFirebaseAnalytics;
    private RoundEndContract.View view;
    private GameInstance gameInstance;
    private int gridWidth;
    private int profilePicScreenWidth;
    private int resultGridWidth;
    private int speechBubbleWidth;
    private final int colorPrimary;
    private final int colorSecondary;
    private boolean displayInterstitial;
    private boolean isStarted = false;
    private InterstitialAd mInterstitialAd;
    private boolean failedToLoadInterstitial = false;
    private long START_TIMESTAMP;

    public RoundEndPresenter(RoundEndContract.View view, int screenWidth, GameInstance gameInstance, int colorPrimary, int colorSecondary, boolean displayInterstitial, FirebaseAnalytics instance) {
        this.mFirebaseAnalytics = instance;
        START_TIMESTAMP = System.currentTimeMillis();
        this.view = view;
        this.gameInstance = gameInstance;
        this.gridWidth = (screenWidth * WordfoxConstants.RESULT_GRID_SCREEN_WIDTH_PERCENT) / 100;
        this.profilePicScreenWidth = (screenWidth * PROFILE_PIC_SCREEN_WIDTH_PERCENT) / 100;
        this.resultGridWidth = (screenWidth * PLAYER_RESULT_GRID_SCREEN_WIDTH_PERCENT) / 100;
        this.speechBubbleWidth = (screenWidth * SPEECH_BUBBLE_SCREEN_WIDTH_PERCENT) / 100;
        this.colorPrimary = colorPrimary;
        this.colorSecondary = colorSecondary;
        this.displayInterstitial = displayInterstitial;
        Log.d(WordfoxConstants.MONITOR_TAG, ":::: Screen width, grid width, result width, pic, spee : " + screenWidth + ", " + gridWidth + ", " + resultGridWidth + ", " + profilePicScreenWidth + ", " + speechBubbleWidth);
    }

    public void startGame() {
        if (isStarted) {
            return;
        }
        isStarted = true;
        Log.d(WordfoxConstants.MONITOR_TAG, "Display interstitial? " + displayInterstitial);
        if (displayInterstitial) {
            displayInterstitial();
        } else {
            startGameAfterInterstitial();
        }
    }

    private void startGameAfterInterstitial() {
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

//    public void populatePossibleWords() {
//        List<String> possibleWords = gameInstance.getSuggestedWordsOfRound(gameInstance.getRound());
//        if (possibleWords.size() <= 0) {
//            return;
//        }
//        Bitmap gridBmp = view.getBlankScaledGrid(gridWidth);  // TODO: is 1/4 the appropriate amount?
//
//        int count = 0;
//        int requiredRows = ((possibleWords.size() - 1) / 3) + 1;
//        for (int i = 0; i < requiredRows; ++i) {
//            view.addRowPossibleWords();         // TODO: Model should add the row itself
//            for (int j = 0; j < WordfoxConstants.RESULT_GRIDS_PER_ROW; ++j) {
//                if (count >= possibleWords.size()) {
//                    view.hideResultGrid(count, gridBmp.getWidth());
//                    ++count;
//                    continue;
//                }
//                String word = possibleWords.get(count).toUpperCase() + " (" + possibleWords.get(count).length() + ")";
//                GridImage gridWithText = new GridImage(gridBmp, word, gameInstance.getRoundLetters(), colorPrimary, colorSecondary);
//                view.addPossibleWord(gridWithText.getBmp(), word, count);
//                ++count;
//            }
//        }
//    }

    public void populatePlayerDetails() {       // TODO:  Tidy this. Use MVP
        Bitmap profPic = view.getPlayerProfPic(profilePicScreenWidth);
        view.setPlayerProfilePic(profPic);

        allowWordSearchToFinish();

        int maxScore = gameInstance.getLongestPossible().length();
        int playerScore = gameInstance.getScore();
        int percentScore = (100 * playerScore) / (maxScore);

        String nameAndPercent = gameInstance.getName() + "\n (" + percentScore + "%)";
        view.setPlayerNameWithPercent(nameAndPercent);

//        String scoreText = gameInstance.getLongestWord() + " (" + gameInstance.getLongestWord().length() + ")";
//        view.setPlayerScoreText(scoreText);

//        Bitmap gridBmp = view.getBlankScaledGrid(resultGridWidth);
//        GridImage gridWithText = new GridImage(gridBmp, gameInstance.getLongestWord().toUpperCase(), gameInstance.getRoundLetters(), colorPrimary, colorSecondary);
//        view.setMyGridResult(gridWithText.getBmp());
    }


    // Race condition: if user ends game really quickly, longest possible words might not yet be calculated.
    private void allowWordSearchToFinish() {
        while (gameInstance.getLongestPossible() == null) {     // TODO: Infinite wait? Refactor -> Exit game
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private AdListener interstitialAdListener = new AdListener() {
        @Override
        public void onAdClosed() {
            Log.d(WordfoxConstants.MONITOR_TAG, "Will start game when ad closes ..");
            startGameAfterInterstitial();
        }

        @Override
        public void onAdLoaded() {
            long durationInMillis = System.currentTimeMillis() - START_TIMESTAMP;

            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_ID, WordfoxConstants.Analytics.INTERSTITIAL_LOAD_DURATION_ID);
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, WordfoxConstants.Analytics.INTERSTITIAL_LOAD_DURATION_NAME);
            bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "advert");
            bundle.putLong(WordfoxConstants.Analytics.INTERSTITIAL_LOAD_DURATION_TIME, durationInMillis);
            mFirebaseAnalytics.logEvent(WordfoxConstants.Analytics.Event.INTERSTITIAL_AD_LOAD_DURATION, bundle);
        }

        private String millisFormatted(long durationInMillis) {
            long millis = durationInMillis % 1000;
            long second = (durationInMillis / 1000) % 60;
            long minute = (durationInMillis / (1000 * 60)) % 60;
            long hour = (durationInMillis / (1000 * 60 * 60)) % 24;
            return String.format("%02d:%02d:%02d.%d", hour, minute, second, millis);
        }

        @Override
        public void onAdFailedToLoad(int errorCode) {
            // Code to be executed when an ad request fails.
            Log.d(WordfoxConstants.MONITOR_TAG, "Interstitial failed to load!!");
            failedToLoadInterstitial = true;
        }
    };

    public void prepareInterstitialAdvert() {
        if (!displayInterstitial) {
            return;
        }
        AdRequest adRequestTest = new AdRequest.Builder()
                .addTestDevice("16930B084D136C6BEFB468B4D1F2919C")
                .build();
        mInterstitialAd = view.getInterstitial();
        mInterstitialAd.setAdListener(interstitialAdListener);
        mInterstitialAd.setAdUnitId(WordfoxConstants.TEST_AD_INTERSTITIAL);
        mInterstitialAd.loadAd(adRequestTest);
    }

    private void displayInterstitial() {
        if (failedToLoadInterstitial || !mInterstitialAd.isLoaded()) {
            startGameAfterInterstitial();
        }
        mInterstitialAd.show();
    }

//    public void displayWelcomeFox() {
//        view.displaySpeechBubble(speechBubbleWidth);
//    }
}
