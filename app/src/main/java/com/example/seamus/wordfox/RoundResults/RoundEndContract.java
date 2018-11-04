package com.example.seamus.wordfox.RoundResults;

import android.graphics.Bitmap;

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


        Bitmap getBlankScaledGrid(int shortestSide);

        void addRowPossibleWords();

        void addPossibleWord(Bitmap gridBmp, String word, int count);

        void hideResultGrid(int count, int width);

        Bitmap getPlayerProfPic(int profilePicScreenWidth);

        void setPlayerNameWithPercent(String nameAndPercent);

        void setPlayerScoreText(String scoreText);

        void setSpeechBubbleText(String playerBubbleText);

        void setMyGridResult(Bitmap bmp);

        void setPlayerProfilePic(Bitmap profPic);
    }

    interface Listener {
    }
}
