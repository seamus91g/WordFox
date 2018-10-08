package com.example.seamus.wordfox.results_screen;

import com.example.seamus.wordfox.GameData;

import java.util.UUID;

/**
 * Created by Gilroy on 5/10/2018.
 */

public interface ResultsContract {
    interface View {
        void makeToast(String message);

        void displayTitle(String title);

        void nextRound(int gameIndex);

        void playerSwitch(int gameIndex);

        boolean playerSwitch();

        void proceedToFinalResults(int gameIndex);

        GameData getPlayerData(UUID playerID);
    }

    interface Listener {
    }
}
