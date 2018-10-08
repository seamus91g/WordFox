package com.example.seamus.wordfox.RoundResults;

import com.example.seamus.wordfox.GameData;

import java.util.UUID;

/**
 * Created by Gilroy on 5/10/2018.
 */

public interface RoundEndContract {
    interface View {
        void makeToast(String message);

        void displayTitle(String title);

        void nextRound(int gameIndex);

        void playerSwitch(int gameIndex);

        boolean playerSwitch();

        void proceedToFinalResults();

    }

    interface Listener {
    }
}
