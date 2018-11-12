package capsicum.game.wordfox;

public interface PlayerChoiceListener {
    interface FragmentView {
        void setChoice(PlayerIdentity name);

        PlayersAdapter getPlayersAdapter();
    }

    interface Listener {

    }
}
