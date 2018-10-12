package com.example.seamus.wordfox.RoundResults;

import android.util.Log;

import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.GameInstance;
import com.example.seamus.wordfox.database.FoxSQLData;
import com.example.seamus.wordfox.database.PlayerStatsTable;
import com.example.seamus.wordfox.datamodels.GameItem;
import com.example.seamus.wordfox.results_screen.ResultsContract;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by Gilroy
 */

public class RoundEndPresenter {
    private static final String TAG = "RoundEndPresenter";
    private RoundEndContract.View view;

    public RoundEndPresenter(RoundEndContract.View view) {
        this.view = view;
    }

    public void startGame(GameInstance gameInstance) {
        gameInstance.incrementRound();
        int index = gameInstance.getThisGameIndex();
        // Start new round if the current round is not the last round
        if (gameInstance.getRound() < GameInstance.NUMBER_ROUNDS) {
            view.nextRound(index);
        } else {
            // If Last Round but there are still players to play -> launch the PlayerSwitchActivity
            gameInstance.gamestateFinished();
            if (view.playerSwitch()) {
                return;
            }
            // Only start the end of game screen if the current player has played all their rounds
            // and there's no player still yet to play
            view.proceedToFinalResults();
        }
    }

}
