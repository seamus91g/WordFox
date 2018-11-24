package capsicum.game.wordfox;

import android.os.CountDownTimer;

import capsicum.game.wordfox.screen_game.GameActivity;
import capsicum.game.wordfox.screen_game.GamescreenContract;

/**
 * Created by Desmond
 */

public class GameTimer {
    public static final String MONITOR_TAG = "myTag";
    public final GamescreenContract.View view;
    private CountDownTimer countDownTimer;

    public GameTimer(GamescreenContract.View view) {
        this.view = view;
        countDownTimer = new CountDownTimer(GameActivity.GAME_TIME_SECONDS * 1000, 1000) {
            int blocksRemaining = GameActivity.GAME_TIME_SECONDS;
            // Hide one of the time segments for each second that passes
            public void onTick(long millisUntilFinished) {
                int time = (int) millisUntilFinished / 1000;
                while (time < blocksRemaining) {
                    view.updateSecondsCounter(time);
                    view.hideTimeSection(GameActivity.GAME_TIME_SECONDS - blocksRemaining);
                    --blocksRemaining;
                }
            }
            public void onFinish() {
                view.hideTimeSection(GameActivity.GAME_TIME_SECONDS - 1);
                // If game in focus, proceed to score screen
                if (view.isGameInFocus()) {
                    view.completeGame();
                }
                // Else set 'time up' flag. OnResume() method will check the flag
                else {
                    view.setTimeUp(true);
                }
            }
        }.start();
    }
    public void killTimer() {
        countDownTimer.cancel();
    }
}
