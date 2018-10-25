package com.example.seamus.wordfox.RoundResults;

import com.example.seamus.wordfox.GameInstance;

/**
 * Created by Gilroy
 */

public class RoundEndPresenter {
    private static final String TAG = "RoundEndPresenter";
    private RoundEndContract.View view;
    private boolean isStarted = false;

    public RoundEndPresenter(RoundEndContract.View view) {
        this.view = view;
    }

    public void startGame(GameInstance gameInstance) {
        if(isStarted){
            return;
        }
        isStarted = true;
        gameInstance.incrementRound();
        // Start new round if the current round is not the last round
        if (gameInstance.getRound() < GameInstance.NUMBER_ROUNDS) {
            view.nextRound();
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
