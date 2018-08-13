package com.example.seamus.wordfox;

import android.support.test.rule.ActivityTestRule;

import org.junit.Rule;

/**
 * Created by Gilroy on 4/12/2018.
 */
public class MainActivityTest {
    @Rule
    public ActivityTestRule<MainActivity> activityRule = new ActivityTestRule<MainActivity>(
            MainActivity.class, true, true);

//    @Test
//    public void checkPlayerWheel(){
////        ViewInteraction num_Picker = onView(withClassName(Matchers.equalTo(NumberPicker.class.getName())));
//        NumberPicker numberPicker = (NumberPicker) activityRule.getActivity().findViewById(R.id.numberPicker);
//        assertEquals(1, numberPicker.getValue());
//        onView(withId(R.id.numberPicker))
//                .perform(new GeneralClickAction(Tap.SINGLE, GeneralLocation.BOTTOM_CENTER, Press.FINGER));
//        assertEquals(2, numberPicker.getValue());
//        onView(withId(R.id.numberPicker))
//                .perform(new GeneralClickAction(Tap.DOUBLE, GeneralLocation.TOP_CENTER, Press.FINGER));
//        assertEquals(6, numberPicker.getValue());
//    }
}