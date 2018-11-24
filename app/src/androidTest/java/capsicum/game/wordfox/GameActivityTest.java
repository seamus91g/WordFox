package capsicum.game.wordfox;

import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;

import capsicum.game.wordfox.screen_home.HomeScreen;
import capsicum.game.wordfox.test.GameRobot;
import capsicum.game.wordfox.test.HomeRobot;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * Created by Gilroy
 */
public class GameActivityTest {
    @Rule
    public ActivityTestRule<HomeScreen> activityRule = new ActivityTestRule<>(
            HomeScreen.class, true, true);
    @Before
    //// TODO, should load a mock GameInstance class instead of having to start from MainActivity
    public void setUp() throws Exception {
        new HomeRobot().startGame();
    }
    /////////////////////////////// Tests ///////////////////////////////
    // Reset clears lettersString
    // Submit doesn't accept invalid word
    // Submit clears lettersString, adds word to bottom
    // Submit shorter word, doesn't add
    // Submit longer word, replaces previous
    @Test
    public void checkGuessStartsEmpty(){
        new GameRobot().longestAttempt("");
    }
    @Test
    public void checkCantClickTwice(){
        new GameRobot()
                .typeShortWord()
                .isClickable(6)
                .isNotClickable(7)
                .isNotClickable(8)
                .isClickable(9);
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
    public void checkShuffle(){     // TODO: .. this is not extensive.
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
    public void checkEqualLengthReplaces(){
        new GameRobot()
                .typeShortWord()
                .submit()
                .longestAttempt("RUM")
                .typeShortWord2()
                .submit()
                .longestAttempt("CON")
                .typeShortWord()
                .submit()
                .longestAttempt("RUM");
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