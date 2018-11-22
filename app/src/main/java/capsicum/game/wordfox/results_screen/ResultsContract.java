package capsicum.game.wordfox.results_screen;

import android.graphics.Bitmap;
import capsicum.game.wordfox.GameData;
import capsicum.game.wordfox.GameGrid.PlayerResultPackage;
import java.util.List;
import java.util.UUID;

/**
 * Created by Gilroy
 */

public interface ResultsContract {
    interface View {
        void makeToast(String message);

        void displayTitle(String title);

        GameData getPlayerData(UUID playerID);

        Bitmap getGameEndFoxBmp(int width);

        Bitmap getGameEndFoxSpeechBmp(int width);

        void displayEndgameFox(Bitmap foxBitmap, Bitmap foxSpeechBitmap, String foxMessage);

        void displayBannerAd(String adUnit);

        void displayWordHeaders(String[] longestPossibleWords, int width);

        Bitmap loadDefaultProfilePic(int profilePicWidth);

        Bitmap getRankBmp(int imageResource, int width);

        void prepareResultAdapter(List<PlayerResultPackage> players, List<String[]> gameLetters, int highestPossibleScore, int gridWidth, int spacerSize);
    }

    interface Listener {
    }
}
