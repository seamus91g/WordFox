package com.example.seamus.wordfox;

import android.os.SystemClock;
import android.support.test.rule.ActivityTestRule;

import com.example.seamus.wordfox.profile.ProfileActivity;
import com.example.seamus.wordfox.test.ProfileRobot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Created by Gilroy on 4/21/2018.
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
