package capsicum.game.wordfox.screen_profile;

import android.graphics.Bitmap;

/**
 * Created by Gilroy
 */

public interface ProfileContract {
    interface View {
        void setRankImage(Bitmap rankImage);

        void setRankText(String rank);

        void hideRecentGame();

        void hideBestGame();

        void setAdjustViewBounds(Boolean bool);

        void setProfileImage(Bitmap bitmap);

        void setLongestWord(String word);

        void setUsername(String name);

        Bitmap getButtonGridImage();

        int getNotPressedButtonColor();

        int getPressedButtonColorPrimary();

        void setRecentGameWinnerMessage(String msg);

        void setRecentGameWinnerYourMessage(String msg);

        int getPressedButtonColorSecondary();

        void setBestWord(Bitmap bmp, int index, String s);

        void setRecentWord(Bitmap bmp, int index, String s);

        void setRecentWordYou(Bitmap bmp, int index, String s);

        void setRecentGameYourWordsInvisible();
    }

    interface Listener {

    }
}
