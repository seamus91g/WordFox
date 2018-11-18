package capsicum.game.wordfox.RoundResults;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import capsicum.game.wordfox.BuildConfig;
import capsicum.game.wordfox.GameInstance;
import capsicum.game.wordfox.ImageHandler;
import capsicum.game.wordfox.WordfoxConstants;
import timber.log.Timber;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Arrays;

import static capsicum.game.wordfox.WordfoxConstants.GRID_LETTERS_PER_COLUMN;
import static capsicum.game.wordfox.WordfoxConstants.GRID_LETTERS_PER_ROW;
import static capsicum.game.wordfox.WordfoxConstants.GRID_WHITE_SPACE_PERCENT;
import static capsicum.game.wordfox.WordfoxConstants.RESULT_GRID_SCREEN_WIDTH_PERCENT;

/**
 * Created by Gilroy
 */

public class RoundEndPresenter implements WordPresenter {
    private static final float PROFILE_PIC_SCREEN_WIDTH_PERCENT = 0.2f;
    private static final float SPEECH_BUBBLE_SCREEN_WIDTH_PERCENT = 0.64f;
    private static final float FOX_SCREEN_WIDTH_PERCENT = 0.35f;
    private final FirebaseAnalytics mFirebaseAnalytics;
    private final RoundEndContract.View view;
    private final GameInstance gameInstance;
    private final String[] gameLetters;
    private final long START_TIMESTAMP;
    private final int screenWidth;
    private InterstitialAd mInterstitialAd;     // TODO: encapsulate ad handling
    private boolean displayInterstitial;
    private boolean isStarted = false;
    private boolean failedToLoadInterstitial = false;

    public RoundEndPresenter(RoundEndContract.View view, int screenWidth, GameInstance gameInstance, boolean displayInterstitial, FirebaseAnalytics instance) {
        this.mFirebaseAnalytics = instance;
        this.START_TIMESTAMP = System.currentTimeMillis();
        this.view = view;
        this.gameInstance = gameInstance;
        this.screenWidth = screenWidth;
        this.displayInterstitial = displayInterstitial;
        this.gameLetters = Arrays.copyOfRange(
                gameInstance.getRoundLetters().split(""), 1, gameInstance.getRoundLetters().length() + 1
        );
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
        if (displayInterstitial) {
            displayInterstitial();
        } else {
            startGameAfterInterstitial();
        }
    }

    public void createPlayerResultGrid() {
        // Create two bitmaps, one for each of pressed and not pressed.
        // They will be re-used to construct each cell in the grid.
        final int resultGridWidth = (int) (screenWidth * RESULT_GRID_SCREEN_WIDTH_PERCENT);
        final int oneCellWidth = (int) ((resultGridWidth * (1 - GRID_WHITE_SPACE_PERCENT)) / GRID_LETTERS_PER_ROW);
        final Bitmap pressedCell = view.getPressedCell(oneCellWidth);
        final Bitmap notPressedCell = view.getNotPressedCell(oneCellWidth);
        // The layout parameters are determind based on the created bitmaps
        final int oneCellHeight = pressedCell.getHeight();
        final int totalGridHeight = (int) ((oneCellHeight * GRID_LETTERS_PER_COLUMN) / (1 - GRID_WHITE_SPACE_PERCENT));
        // Using the same method as displaying a grid in the recycler view used for the results
        view.displayPlayerResultGrid(pressedCell, notPressedCell, resultGridWidth, totalGridHeight, gameLetters, gameInstance.getLongestWord());
    }

    private void setUpRoundEndFox() {
        int foxWidth = (int) (FOX_SCREEN_WIDTH_PERCENT * screenWidth);
        int speechWidth = (int) (SPEECH_BUBBLE_SCREEN_WIDTH_PERCENT * screenWidth);
        int maxScore = gameInstance.getLongestPossible().length();
        int playerScore = gameInstance.getScore();
        String playerResult = "You scored " + playerScore + " out of " + maxScore;
        view.displayRoundEndFox(foxWidth, speechWidth, playerResult);
    }

    private Bitmap getPlayerProfilePic() {
        String profPicStr = view.getProfilePicUriString(gameInstance.getID());
        int profilePicWidth = (int) (PROFILE_PIC_SCREEN_WIDTH_PERCENT * screenWidth);
        if (profPicStr.equals("")) {
            return view.loadDefaultProfilePic(profilePicWidth);
        } else {
            Uri myFileUri = Uri.parse(profPicStr);
            Bitmap profPic = view.profilePicFromUri(myFileUri, profilePicWidth);
            if (profPic == null) {
                return view.loadDefaultProfilePic(profilePicWidth);
            }
            return ImageHandler.cropToSquare(profPic);
        }
    }

    public void displayerPlayerProfileImage() {
        view.setPlayerProfilePic(getPlayerProfilePic());
    }

    public void populatePlayerResults() {
        presentWord(gameInstance.getLongestPossible(this));
    }

    @Override
    public void presentWord(String word) {
        if (word == null) {
            return;
        }
        int maxScore = word.length();
        int playerScore = gameInstance.getScore();
        int percentScore = (100 * playerScore) / (maxScore);
        String nameAndPercent = gameInstance.getName() + "\n (" + percentScore + "%)";
        view.runOnUI(() -> {
            setUpRoundEndFox();
            view.setPlayerNameWithPercent(nameAndPercent);
        });
    }

    public void setupPossibleWords() {
        presentLongestPossible(gameInstance.getSuggestedWordsOfRound(this));
    }

    @Override
    public void presentLongestPossible(ArrayList<String> longestPossible) {
        if (longestPossible == null) {
            return;
        }
        view.runOnUI(() -> {
            view.displayPossibleWordsAsGrids(longestPossible, gameLetters, (int) (screenWidth * RESULT_GRID_SCREEN_WIDTH_PERCENT), GRID_WHITE_SPACE_PERCENT);
        });
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

    private AdListener interstitialAdListener = new AdListener() {
        @Override
        public void onAdClosed() {
            Timber.d("Will start game when ad closes ..");
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

        @Override
        public void onAdFailedToLoad(int errorCode) {
            // Code to be executed when an ad request fails.
            Timber.d("Interstitial failed to load!!");
            failedToLoadInterstitial = true;
        }
    };

    public void prepareInterstitialAdvert() {
        if (!displayInterstitial) {
            return;
        }
        AdRequest adRequest;
        String adUnit;
        if (BuildConfig.DEBUG) {
            adRequest = new AdRequest.Builder()
                    .addTestDevice("16930B084D136C6BEFB468B4D1F2919C")
                    .build();
            adUnit = WordfoxConstants.TEST_AD_INTERSTITIAL;
        } else {
            adRequest = new AdRequest.Builder().build();
            adUnit = WordfoxConstants.END_OF_ROUND3_BANNER_AD_UNIT_ID;
        }
        mInterstitialAd = view.getInterstitial();
        mInterstitialAd.setAdListener(interstitialAdListener);
        mInterstitialAd.setAdUnitId(adUnit);
        mInterstitialAd.loadAd(adRequest);
    }

    private void displayInterstitial() {
        if (failedToLoadInterstitial || !mInterstitialAd.isLoaded()) {
            startGameAfterInterstitial();
        }
        mInterstitialAd.show();
    }

    public void broadcastMyResults() {
        view.broadcastString(gameInstance.resultAsJson().toString());
    }
}
