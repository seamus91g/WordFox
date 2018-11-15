package capsicum.game.wordfox;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;

import static org.junit.Assert.*;

public class HomeScreenTest {
    @Rule
    public ActivityTestRule<HomeScreen> activityRule = new ActivityTestRule<HomeScreen>(
            HomeScreen.class, true, true);

}