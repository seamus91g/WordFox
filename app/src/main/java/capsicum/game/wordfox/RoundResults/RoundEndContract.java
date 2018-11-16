package capsicum.game.wordfox.RoundResults;

import android.graphics.Bitmap;

import com.google.android.gms.ads.InterstitialAd;

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

        Bitmap getPlayerProfPic(int profilePicScreenWidth);

        void setPlayerNameWithPercent(String nameAndPercent);

        void setPlayerProfilePic(Bitmap profPic);

        InterstitialAd getInterstitial();
    }

    interface Listener {
    }
}
