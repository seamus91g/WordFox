package com.example.seamus.wordfox;

import android.app.Activity;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;

import static com.example.seamus.wordfox.R.id.timeblock1;

/**
 * Created by Desmond on 12/07/2017.
 */

public class gameTimer {
    public static final String MONITOR_TAG = "myTag";
    public Activity activity;

    public gameTimer(Activity _activity, final gameInstance myGame, final GameData myGameData) {
        this.activity = _activity;

        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                int time = (int) millisUntilFinished / 1000;
                switch (time) {
                    case 24:
                        final TextView box1 = (TextView) activity.findViewById(timeblock1);
                        box1.setBackgroundColor(0);
                        break;
                    case 18:
                        TextView box2 = (TextView) activity.findViewById(R.id.timeblock2);
                        box2.setBackgroundColor(0);
                        break;
                    case 12:
                        TextView box3 = (TextView) activity.findViewById(R.id.timeblock3);
                        box3.setBackgroundColor(0);
                        break;
                    case 6:
                        TextView box4 = (TextView) activity.findViewById(R.id.timeblock4);
                        box4.setBackgroundColor(0);
                        break;
                    case 1:
                        TextView box5 = (TextView) activity.findViewById(R.id.timeblock5);
                        box5.setBackgroundColor(0);
                        Log.d(MONITOR_TAG, "Adding word to prefs: " + myGame.getLongestWord() + ", END");
                        myGameData.addWord(myGame.getLongestWord());
                        Log.d(MONITOR_TAG, "Now longest: " + myGameData.findLongest() + ", END");
                        Intent ScoreScreen1Intent = new Intent(activity, ScoreScreen1Activity.class);
                        activity.startActivity(ScoreScreen1Intent);
                        break;
                }
            }
            public void onFinish() {
            }
        }.start();
    }
}
