package capsicum.game.wordfox.results_screen;

import capsicum.game.wordfox.GameData;

import java.util.UUID;

/**
 * Created by Gilroy
 */

public interface ResultsContract {
    interface View {
        void makeToast(String message);

        void displayTitle(String title);

        void playerSwitch(int gameIndex);

        GameData getPlayerData(UUID playerID);
    }

    interface Listener {
    }
}
