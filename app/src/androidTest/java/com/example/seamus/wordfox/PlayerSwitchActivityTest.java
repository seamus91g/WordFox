package com.example.seamus.wordfox;

import android.support.test.rule.ActivityTestRule;

import com.example.seamus.wordfox.player_switch.PlayerSwitchActivity;

import org.junit.Rule;

import static org.junit.Assert.*;

/**
 * Created by Gilroy
 */
public class PlayerSwitchActivityTest {
    @Rule
    public ActivityTestRule<PlayerSwitchActivity> activityRule = new ActivityTestRule<PlayerSwitchActivity>(
            PlayerSwitchActivity.class, true, false);

}