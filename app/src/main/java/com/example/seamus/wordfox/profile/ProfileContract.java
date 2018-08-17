package com.example.seamus.wordfox.profile;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by Gilroy on 4/19/2018.
 */

public interface ProfileContract {
    interface View {
        void setAdjustViewBounds(Boolean bool);
        void setProfileImage(Bitmap bitmap);
        void setLongestWord(String word);
        void setUsername(String name);

        void setBestWords(ArrayList<String> words);

        void setDataPreviousGame(ArrayList<String> info);

        Bitmap getButtonGridImage();
        int getNotPressedButtonColor();
        int getPressedButtonColorPrimary();
        int getPressedButtonColorSecondary();

        void setBestWord(Bitmap bmp, int index, String s);
    }
    interface Listener{

    }
}
