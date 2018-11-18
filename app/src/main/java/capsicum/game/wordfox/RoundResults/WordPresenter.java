package capsicum.game.wordfox.RoundResults;

import java.util.ArrayList;

public interface WordPresenter {
    void presentWord(String word);
    void presentLongestPossible(ArrayList<String> longestPossible);
}
