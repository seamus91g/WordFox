package com.example.seamus.wordfox.results_screen;

import com.example.seamus.wordfox.GameData;

/**
 * Created by Gilroy on 5/10/2018.
 */

public interface ResultsContract {
    interface View {
        void setGameOverMessage(String gameOverMessage);
        void makeToast(String message);

        void addTVtoResults(String result);

        void setVictoryMessage(String victoryMessage);

        void prepareHomeButton();
        void prepareContinueButton();

        void displayTitle(String title);

        void addResultName(String resultTitle);

        void addResultValue(String resultContent);
        void addResultValue(String resultContent, String description);

        void addResultSpacer();

        void nextRound(int gameIndex);

        void playerSwitch(int gameIndex);
        boolean playerSwitch();

        void proceedToFinalResults(int gameIndex);

        String defaultP1Name();

        GameData getPlayerData(String playerID);
    }

    interface Listener{
    }
}
