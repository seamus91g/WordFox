package capsicum.game.wordfox.screen_results_round;

import java.util.ArrayList;

public interface WordPresenter {
    void presentWord(String word);
    void presentLongestPossible(ArrayList<String> longestPossible);
}
