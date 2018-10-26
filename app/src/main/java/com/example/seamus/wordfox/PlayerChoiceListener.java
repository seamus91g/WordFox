package com.example.seamus.wordfox;

public interface PlayerChoiceListener {
    interface FragmentView {
        void setChoice(PlayerIdentity name);

        PlayersAdapter getPlayersAdapter();
    }

    interface Listener {

    }
}
