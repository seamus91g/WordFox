package capsicum.game.wordfox.screen_swap_choose;

import capsicum.game.wordfox.PlayerIdentity;
import capsicum.game.wordfox.screen_swap_choose.PlayersAdapter;

public interface PlayerChoiceListener {
    interface FragmentView {
        void setChoice(PlayerIdentity name);

        PlayersAdapter getPlayersAdapter();
    }

    interface Listener {

    }
}
