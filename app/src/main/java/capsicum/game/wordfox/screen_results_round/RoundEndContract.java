package capsicum.game.wordfox.screen_results_round;

import android.graphics.Bitmap;
import com.google.android.gms.ads.InterstitialAd;
import java.util.ArrayList;

/**
 * Created by Gilroy
 */

public interface RoundEndContract {
    interface View {
        void makeToast(String message);

        void displayTitle(String title);

        void nextRound();

        boolean playerSwitch();

        void proceedToFinalResults();

        void setPlayerNameWithPercent(String nameAndPercent);

        void setPlayerProfilePic(Bitmap profPic);

        InterstitialAd getInterstitial();

        void log(String msg);

        android.view.View inflateGameGrid();

        Bitmap getPressedCell(int width);

        Bitmap getNotPressedCell(int width);

        void broadcastString(String result);

        void displayPossibleWordsAsGrids(ArrayList<String> possibleWordsOfRound, String[] gameLetters, int gridWidth, float whiteSpacePercent);

        void displayPlayerResultGrid(Bitmap pressedCell, Bitmap notPressedCell, int gridWidth, int gridHeight, String[] letters, String word);

        void displayRoundEndFox(int foxWidth, int speechWidth, String playerResult);

        Bitmap loadDefaultProfilePic(int profilePicWidth);

        void runOnUI(Runnable action);
    }

    interface Listener {
    }
}
