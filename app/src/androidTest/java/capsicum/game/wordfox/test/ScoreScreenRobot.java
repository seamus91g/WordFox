package capsicum.game.wordfox.test;

import capsicum.game.wordfox.*;

/**
 * Created by Gilroy
 */
import capsicum.game.wordfox.R;


public class ScoreScreenRobot extends ScreenRobot<ScoreScreenRobot> {
    public ScoreScreenRobot bestWord(String word) {
        return checkLinearLayoutHasItemText(R.id.round_end_longest_word, word);
    }

    public ScoreScreenRobot nextRound() {
        return performClick(R.id.fab_round_end);
    }
}
