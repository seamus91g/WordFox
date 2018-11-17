package capsicum.game.wordfox.RoundResults;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;

import capsicum.game.wordfox.GameInstance;
import capsicum.game.wordfox.WordfoxConstants;
import timber.log.Timber;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

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
        Timber.d( ":::: Screen width, grid width, result width, pic, spee : " + screenWidth + ", " + gridWidth + ", " + resultGridWidth + ", " + profilePicScreenWidth + ", " + speechBubbleWidth);
    }

    public void displayTitle() {
        String title = "Round " + (gameInstance.getRound() + 1) + " results";
        view.displayTitle(title);
    }

    public void startGame() {
        if (isStarted) {
            return;
        }
        isStarted = true;
        Timber.d( "Display interstitial? " + displayInterstitial);
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

    public void populatePlayerDetails() {       // TODO:  Tidy this. Use MVP
        Bitmap profPic = view.getPlayerProfPic(profilePicScreenWidth);
        view.setPlayerProfilePic(profPic);
        allowWordSearchToFinish();
        int maxScore = gameInstance.getLongestPossible().length();
        int playerScore = gameInstance.getScore();
        int percentScore = (100 * playerScore) / (maxScore);

        String nameAndPercent = gameInstance.getName() + "\n (" + percentScore + "%)";
        view.setPlayerNameWithPercent(nameAndPercent);
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
            Timber.d( "Will start game when ad closes ..");
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
            Timber.d( "Interstitial failed to load!!");
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
}
