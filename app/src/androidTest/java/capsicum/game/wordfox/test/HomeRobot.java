package capsicum.game.wordfox.test;

import capsicum.game.wordfox.R;

/**
 * Created by Gilroy
 */

public class HomeRobot extends ScreenRobot<HomeRobot> {
    public HomeRobot startGame() {
        return performClick(R.id.just_me_button);
    }

    public HomeRobot startWifiGame() {
        return performClick(R.id.with_friends_button);
    }

    public HomeRobot startPassPlayGame() {
        return performClick(R.id.pass_and_play_button);
    }

}
