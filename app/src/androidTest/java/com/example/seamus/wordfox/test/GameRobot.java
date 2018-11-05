package com.example.seamus.wordfox.test;

/**
 * Created by Gilroy
 */

import com.example.seamus.wordfox.R;

import java.util.ArrayList;
import java.util.Arrays;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

public class GameRobot extends ScreenRobot<GameRobot> {
    public GameRobot longestAttempt(String string) {
        return checkViewHasText(R.id.longestAttempt, string);
    }
    public GameRobot currentAttemptIs(String word) {
        return checkViewHasText(R.id.currentAttempt, word);
    }
    public GameRobot reset() {
        performClick(R.id.resetButton);
        return this;
    }
    public GameRobot isClickable(int cellNumber){
        String tag = "guessGridCell" + cellNumber;
        checkIsClickable(tag);
        return this;
    }
    public GameRobot isNotClickable(int cellNumber){
        String tag = "guessGridCell" + cellNumber;

        checkNotClickable(tag);
        return this;
    }

    public GameRobot submit() {
        performClick(R.id.submitButton);
        return this;
    }
    public GameRobot lengthLongestIs(String length) {
        checkViewHasText(R.id.lengthLongestAttempt, length);
        return this;
    }
    public GameRobot typeInvalidWord() {
        performClick(R.id.guessGridCell1);
        performClick(R.id.guessGridCell2);
        return this;
    }
    public GameRobot typeShortWord() {
        performClick(R.id.guessGridCell7);
        performClick(R.id.guessGridCell8);
        performClick(R.id.guessGridCell9);
        return this;
    }
    public GameRobot typeShortWord2() {
        performClick(R.id.guessGridCell1);
        performClick(R.id.guessGridCell2);
        performClick(R.id.guessGridCell3);
        return this;
    }
    public GameRobot typeMediumWord() {
        performClick(R.id.guessGridCell7);
        performClick(R.id.guessGridCell2);
        performClick(R.id.guessGridCell4);
        performClick(R.id.guessGridCell5);
        performClick(R.id.guessGridCell6);
        return this;
    }
    public GameRobot typeLongWord() {
        performClick(R.id.guessGridCell1);
        performClick(R.id.guessGridCell2);
        performClick(R.id.guessGridCell3);
        performClick(R.id.guessGridCell4);
        performClick(R.id.guessGridCell5);
        performClick(R.id.guessGridCell6);
        performClick(R.id.guessGridCell7);
        performClick(R.id.guessGridCell8);
        performClick(R.id.guessGridCell9);
        return this;
    }

    public GameRobot shuffle() {
        performClick(R.id.shuffleButton);
        return this;
    }
    public GameRobot endGame() {
        performDoubleClick(R.id.resetButton);
        return this;
    }
}
