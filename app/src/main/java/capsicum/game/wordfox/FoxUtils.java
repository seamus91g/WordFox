package capsicum.game.wordfox;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by Gilroy
 */

public class FoxUtils  extends AppCompatActivity {
    private static int id = 1;                                  //   Support for API 16
    // Returns a valid id that isn't in use
    public static int getUniqueId(Activity ac) {
        int newId;
        if (Build.VERSION.SDK_INT < 17) {
            View v = ac.findViewById(id);
            while (v != null){
                v = ac.findViewById(++id);
            }
            newId = id++;
        } else {
            newId = View.generateViewId();
        }
        return newId;
    }    // Convert from dp to pixels
    public static int dp2px(Context c, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, c.getResources().getDisplayMetrics());
    }

    public static void clearViewFocus(View viewWithFocus, Context context) {
        viewWithFocus.clearFocus();
        if (viewWithFocus != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(viewWithFocus.getWindowToken(), 0);
        }
    }
}
