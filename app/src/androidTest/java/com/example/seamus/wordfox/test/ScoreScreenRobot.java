package com.example.seamus.wordfox.test;

import com.example.seamus.wordfox.*;

/**
 * Created by Gilroy on 4/16/2018.
 */
import com.example.seamus.wordfox.R;


public class ScoreScreenRobot extends ScreenRobot<ScoreScreenRobot> {
    public ScoreScreenRobot bestWord(String word) {
        return checkLinearLayoutHasItemText(R.id.wordsEndScreenLL, word);
    }

    public ScoreScreenRobot nextRound() {
        return performClick(R.id.endOfRoundOrGameButton);
    }
}
