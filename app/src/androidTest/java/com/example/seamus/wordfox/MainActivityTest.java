package com.example.seamus.wordfox;

import android.support.test.espresso.ViewInteraction;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;
import android.support.test.rule.ActivityTestRule;
import android.widget.NumberPicker;

import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;

import java.util.regex.Matcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeDown;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.swipeUp;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

/**
 * Created by Gilroy on 4/12/2018.
 */
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(
            MainActivity.class, true, true);

    @Test
    public void checkPlayerWheel(){
//        ViewInteraction num_Picker = onView(withClassName(Matchers.equalTo(NumberPicker.class.getName())));
        NumberPicker numberPicker = (NumberPicker) activityRule.getActivity().findViewById(R.id.numberPicker);
        assertEquals(1, numberPicker.getValue());
        onView(withId(R.id.numberPicker))
                .perform(new GeneralClickAction(Tap.SINGLE, GeneralLocation.BOTTOM_CENTER, Press.FINGER));
        assertEquals(2, numberPicker.getValue());
        onView(withId(R.id.numberPicker))
                .perform(new GeneralClickAction(Tap.DOUBLE, GeneralLocation.TOP_CENTER, Press.FINGER));
        assertEquals(6, numberPicker.getValue());
    }
}