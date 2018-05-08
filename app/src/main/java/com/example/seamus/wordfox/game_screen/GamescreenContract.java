package com.example.seamus.wordfox.game_screen;

import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gilroy on 4/17/2018.
 */

public interface GamescreenContract {
    interface View {
        void setTimeUp(boolean isTimeUp);

        int getUniqueId();

        void createTimer(ArrayList<TextView> textViews);

        void updateSecondsCounter(int time);

        void hideTimeSection(int sectionIndex);

        boolean isGameInFocus();

        void completeGame();

        void startScoreScreen1Act(int thisGameIndex);

//        void setGameInFocus(boolean isGameFocused);

        void updateHeaderLetters();

//        String getBestSoFar();

        void setLongest(String word);

        void setCellOldClicked(String previousID);

        void setCellNewlyClicked(String cellID);

//        String getCurrentAttempt();

        void setCellNotClicked(String id);

        void setCurrentAttempt(String currentGuess);

        void printGridCell(SingleCell cell);

        TextView createTimeCounterSegment(int segmentHeight);

        void makeToast(String toastMessage);
    }

    interface Listener {

    }
}
