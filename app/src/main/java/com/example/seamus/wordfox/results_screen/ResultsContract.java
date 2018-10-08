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

        void playerSwitch(int gameIndex);

        GameData getPlayerData(UUID playerID);
    }

    interface Listener {
    }
}
