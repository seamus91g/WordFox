package capsicum.game.wordfox.test;

import capsicum.game.wordfox.R;
/**
 * Created by Gilroy
 */

public class ProfileRobot extends ScreenRobot<ProfileRobot> {

//    public GameRobot currentAttemptIs(String word) {
//        return checkViewHasText(capsicum.game.wordfox.R.id.currentAttempt, word);
//    }
    public ProfileRobot enterUsername(String name){
        replaceEditText(R.id.profile_usernameET, name);
        return this;
    }

    public ProfileRobot saveName() {
        performClick(R.id.profile_save_name);
        return this;
    }

    public ProfileRobot refresh() {
        performClick(R.id.action_profile);
        return this;
    }

    public ProfileRobot checkName(String name) {
        return checkViewHasText(R.id.profile_usernameET, name);
    }
}
