package com.example.seamus.wordfox;

import android.support.test.rule.ActivityTestRule;

import com.example.seamus.wordfox.test.GameRobot;
import com.example.seamus.wordfox.test.HomeRobot;
import com.example.seamus.wordfox.test.PlayerSwitchRobot;
import com.example.seamus.wordfox.test.ScoreScreenRobot;

import org.junit.Rule;
import org.junit.Test;

/**
 * Created by Gilroy on 4/16/2018.
 */

public class FullGameTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(
            MainActivity.class, true, true);

    // Don't find any word in any round
    @Test
    public void blankGame(){
        new HomeRobot()
                .startGame();
        skipAllRounds();
    }
    // Single player game, find word in each round
    @Test
    public void singlePlayerGame(){
        new HomeRobot()
                .startGame();
        playAllRounds();
    }
    @Test
    public void twoPlayerGame(){
        new HomeRobot()
                .setPlayerCount(2)
                .startGame();
        playAllRounds();
        new PlayerSwitchRobot()
                .startNextPlayer();
        playAllRounds();
    }
    @Test
    public void sixPlayerGame(){
        new HomeRobot()
                .setPlayerCount(6)
                .startGame();
        playAllRounds();
        for (int i=0; i<5; ++i) {
            new PlayerSwitchRobot()
                    .startNextPlayer();
            playAllRounds();
        }
    }
    private void playAllRounds() {
        for (int i = 0; i< GameInstance.NUMBER_ROUNDS; ++i){
            new GameRobot()
                    .typeShortWord()
                    .submit()
                    .endGame();
            new ScoreScreenRobot()
                    .bestWord("RUM (3)")
                    .nextRound();
        }
    }
    private void skipAllRounds() {
        for (int i = 0; i< GameInstance.NUMBER_ROUNDS; ++i){
            new GameRobot()
                    .endGame();
            new ScoreScreenRobot()
                    .bestWord(" (0)")
                    .nextRound();
        }
    }
}
