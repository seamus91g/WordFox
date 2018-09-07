package com.example.seamus.wordfox.profile;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Gilroy on 4/19/2018.
 */

public interface ProfileContract {
    interface View {
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
