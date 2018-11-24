package capsicum.game.wordfox.dictionary;

import java.util.ArrayList;

/**
 * Created by Gilroy
 */

public interface Diction {
    boolean checkWordExists(String checkWord);
    ArrayList<String> longestWordFromLetters(String givenLetters);
    ArrayList<String> getGivenLetters();
}
