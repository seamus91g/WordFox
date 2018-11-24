package capsicum.game.wordfox;

import android.support.test.rule.ActivityTestRule;

import capsicum.game.wordfox.screen_profile.ProfileActivity;
import capsicum.game.wordfox.test.ProfileRobot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Created by Gilroy
 */

public class ProfileActivityTest {
    @Rule
    public ActivityTestRule<ProfileActivity> activityRule = new ActivityTestRule<ProfileActivity>(
            ProfileActivity.class, true, true);
//    @Before
//    public void setUp() throws Exception {
//        new HomeRobot().startGame();
//    }
    @Test
    public void checkUsernameChange(){
        new ProfileRobot()
                .enterUsername("Joey")
                .saveName()
                .refresh()
                .checkName("Joey");
    }

}
