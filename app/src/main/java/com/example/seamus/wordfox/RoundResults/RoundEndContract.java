package com.example.seamus.wordfox.RoundResults;

import com.example.seamus.wordfox.GameData;

import java.util.UUID;

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

    }

    interface Listener {
    }
}
