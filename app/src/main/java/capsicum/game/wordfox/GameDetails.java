package capsicum.game.wordfox;

import java.util.ArrayList;
import java.util.UUID;

public interface GameDetails {
    UUID getID();

    int getHighestPossibleScore();

    int getTotalScore();

    String getName();

    String getRoundWord(int i);

    ArrayList<String> getAllFinalWords();

    String getLetters(int i);
}
