package capsicum.game.wordfox.data;

import java.util.ArrayList;

/**
 * Created by Gilroy
 */

public interface Diction {
    boolean checkWordExists(String checkWord);
    ArrayList<String> longestWordFromLetters(String givenLetters);
    ArrayList<String> getGivenLetters();
}
