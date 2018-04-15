package com.example.seamus.wordfox;

import android.os.CountDownTimer;
import android.support.annotation.IdRes;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Desmond on 12/07/2017.
 */

public class GameTimer {
    public static final String MONITOR_TAG = "myTag";
    public final GameActivity activity;
    private ArrayList<Integer> viewIds;
    CountDownTimer countDownTimer;

    public GameTimer(final GameActivity _activity, @IdRes final ArrayList<Integer> IDs) {
        this.activity = _activity;
        this.viewIds = IDs;
        _activity.setTimeUp(false);
        countDownTimer = new CountDownTimer(GameActivity.GAME_TIME_SECONDS*1000, 1000) {
            int blocksRemaining = GameActivity.GAME_TIME_SECONDS;
            public void onTick(long millisUntilFinished) {
                int time = (int) millisUntilFinished / 1000;
                while (time < blocksRemaining) {
                    TextView secondsLeft = (TextView) activity.findViewById(R.id.secondsRemaining);
                    secondsLeft.setText(String.valueOf(time));
                    TextView timeBlock = (TextView) activity.findViewById(viewIds.get(GameActivity.GAME_TIME_SECONDS - blocksRemaining));
                    timeBlock.setBackgroundColor(0);
                    --blocksRemaining;
                }
            }
            public void onFinish() {
                TextView timeBlock = (TextView) activity.findViewById(viewIds.get(GameActivity.GAME_TIME_SECONDS -1));
                timeBlock.setBackgroundColor(0);
                _activity.setTimeUp(true);
                if (_activity.isGameInFocus()) {
                    activity.completeGame();
                }
            }
        }.start();
    }

    public void killTimer() {
        Log.d(MONITOR_TAG, "Killing timer from killTimer");
        countDownTimer.cancel();
    }
}
