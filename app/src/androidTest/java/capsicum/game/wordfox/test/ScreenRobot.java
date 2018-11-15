package capsicum.game.wordfox.test;

import android.support.annotation.IdRes;
import android.view.View;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.doubleClick;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isClickable;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withTagValue;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

/**
 * Created by Gilroy
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

    public T checkViewIndexHasText(@IdRes int viewId, String string, int index) {
        onView(withIndex(withId(viewId), index))
                .check(matches(withText(string)));
        return (T) this;
    }

    public T checkLinearLayoutHasItemText(@IdRes int viewId, String string) {
        onView(allOf(withId(viewId), withContentDescription("Player longest word")))
                .check(matches(withText(string)));
        return (T) this;
    }

    public T replaceEditText(@IdRes int edittextID, String text) {
        onView(withId(edittextID))
                .perform(clearText())
                .perform(typeText(text));
        return (T) this;
    }

    public static Matcher<View> withIndex(final Matcher<View> matcher, final int index) {
        return new TypeSafeMatcher<View>() {
            int currentIndex = 0;

            @Override
            public void describeTo(Description description) {
                description.appendText("with index: ");
                description.appendValue(index);
                matcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                return matcher.matches(view) && currentIndex++ == index;
            }
        };
    }
}
