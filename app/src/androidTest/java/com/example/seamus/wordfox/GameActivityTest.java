package com.example.seamus.wordfox;

import android.os.SystemClock;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.widget.TextView;

import com.example.seamus.wordfox.injection.TestDictionaryApplication;
import com.example.seamus.wordfox.test.GameRobot;
import com.example.seamus.wordfox.test.HomeRobot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Gilroy on 4/12/2018.
 */
public class GameActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(
            MainActivity.class, true, true);
    @Before
    //// TODO, should load a mock GameInstance class instead of having to start from MainActivity
    public void setUp() throws Exception {
        new HomeRobot().startGame();
    }
    /////////////////////////////// Tests ///////////////////////////////
    // Reset clears letters
    // Submit doesn't accept invalid word
    // Submit clears letters, adds word to bottom
    // Submit shorter word, doesn't add
    // Submit longer word, replaces previous
    @Test
    public void checkGuessStartsEmpty(){
        new GameRobot().longestAttempt("");
    }
    @Test
    public void checkReset() {
        new GameRobot()
                .typeShortWord()
                .currentAttemptIs("RUM")
                .reset()
                .currentAttemptIs("");
    }
    @Test
    public void checkInvalidSubmit(){
        new GameRobot()
                .typeInvalidWord()
                .submit()
                .longestAttempt("")
                .lengthLongestIs("");
    }
    @Test
    public void checkSubmit() {
        new GameRobot()
                .typeShortWord()
                .submit()
                .longestAttempt("RUM")
                .lengthLongestIs("3");
    }
    @Test
    public void checkShuffle(){
        new GameRobot()
                .typeShortWord()
                .currentAttemptIs("RUM")
                .shuffle()
                .currentAttemptIs("RUM");
    }
    @Test
    public void checkLongerReplacesShorter(){
        new GameRobot()
                .typeShortWord()
                .submit()
                .longestAttempt("RUM")
                .typeLongWord()
                .submit()
                .longestAttempt("CONUNDRUM");
    }
    @Test
    public void shorterGuessDoesntReplaceLonger(){
        new GameRobot()
                .typeLongWord()
                .submit()
                .longestAttempt("CONUNDRUM")
                .typeShortWord()
                .submit()
                .longestAttempt("CONUNDRUM");
    }
}


//    @Test
//    public void checkSubmit() {
//        onView(withId(R.id.guessGridCell7))
//                .perform(click());
//        onView(withId(R.id.guessGridCell8))
//                .perform(click());
//        onView(withId(R.id.guessGridCell9))
//                .perform(click());
//        onView(withId(R.id.submitButton))
//                .perform(click());
//        onView(withId(R.id.longestAttempt))
//                .check(matches(withText("RUM")));
//        onView(withId(R.id.lengthLongestAttempt))
//                .check(matches(withText("3")));
//    }