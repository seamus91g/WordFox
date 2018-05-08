package com.example.seamus.wordfox.test;

import android.support.annotation.IdRes;
import android.support.test.espresso.action.GeneralClickAction;
import android.support.test.espresso.action.GeneralLocation;
import android.support.test.espresso.action.Press;
import android.support.test.espresso.action.Tap;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withTagKey;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.StringEndsWith.endsWith;

/**
 * Created by Gilroy on 4/14/2018.
 */

public abstract class ScreenRobot<T extends ScreenRobot> {
    public T performClick(@IdRes int viewId) {
        onView(withId(viewId))
                .perform(click());
        return (T) this;
    }

    public T checkIsClickable(String tag) {
        onView(withTagValue(is((Object) tag)))
                .check(matches(isClickable()));
        return (T) this;
    }
    public T checkNotClickable(String tag) {
        onView(withTagValue(is((Object) tag)))
                .check(matches(not(isClickable())));
        return (T) this;
    }

    public T performDoubleClick(@IdRes int viewId) {
        onView(withId(viewId))
                .perform(doubleClick());
        return (T) this;
    }

    public T checkViewHasText(@IdRes int viewId, String string) {
        onView(withId(viewId))
                .check(matches(withText(string)));
        return (T) this;
    }

    public T checkLinearLayoutHasItemText(@IdRes int viewId, String string) {
        onView(allOf(withParent(withId(viewId)), withText(string), withContentDescription("Player longest word")))
                .check(matches(withText(string)));
        return (T) this;
    }

    public T advanceNumberPicker(@IdRes int pickerId) {
        onView(withId(pickerId))
                .perform(new GeneralClickAction(Tap.SINGLE, GeneralLocation.BOTTOM_CENTER, Press.FINGER));
        return (T) this;
    }

    public T replaceEditText(@IdRes int edittextID, String text) {
//        onView(allOf(withClassName(endsWith("EditText")), withText(is("Test")))).perform(replaceText("Another test"));
        onView(withId(edittextID))
                .perform(clearText())
                .perform(typeText(text));
        return (T) this;
    }
}
