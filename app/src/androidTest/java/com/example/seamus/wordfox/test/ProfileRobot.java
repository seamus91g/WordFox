package com.example.seamus.wordfox.test;

import com.example.seamus.wordfox.R;
/**
 * Created by Gilroy
 */

public class ProfileRobot extends ScreenRobot<ProfileRobot> {

//    public GameRobot currentAttemptIs(String word) {
//        return checkViewHasText(com.example.seamus.wordfox.R.id.currentAttempt, word);
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
