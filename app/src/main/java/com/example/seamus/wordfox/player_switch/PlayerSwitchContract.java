package com.example.seamus.wordfox.player_switch;

import java.util.ArrayList;

/**
 * Created by Gilroy
 */

public interface PlayerSwitchContract {
    interface View {
        void displayMessage(String message);
        void populateMenu(ArrayList<String> items);
    }
    interface Listener {

    }
}
