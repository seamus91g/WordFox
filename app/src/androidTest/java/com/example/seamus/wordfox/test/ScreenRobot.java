package com.example.seamus.wordfox.test;

import android.support.annotation.IdRes;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isSelected;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.not;

/**
 * Created by Gilroy on 4/14/2018.
 */

public abstract class ScreenRobot<T extends ScreenRobot> {
    public T performClick(@IdRes int viewId){
        onView(withId(viewId))
                .perform(click());
        return (T) this;
    }
    public T checkViewHasText(@IdRes int viewId, String string){
        onView(withId(viewId))
                .check(matches(withText(string)));
        return (T) this;
    }
}
