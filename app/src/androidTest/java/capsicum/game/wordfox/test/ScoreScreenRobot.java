package capsicum.game.wordfox.test;

/**
 * Created by Gilroy
 */
import capsicum.game.wordfox.R;


public class ScoreScreenRobot extends ScreenRobot<ScoreScreenRobot> {
    public ScoreScreenRobot bestWord(String word) {
        return checkViewIndexHasText(R.id.game_grid_word_header, word, 0);
    }

    public ScoreScreenRobot nextRound() {
        return performClick(R.id.fab_round_end);
    }
}
