package capsicum.game.wordfox;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

/**
 * Created by Desmond
 */
public class IVmethods {

    // Observe one view and use it's laid out width to decide the width of a difference view.
    public static void setWidthAsPercentOfLaidOutView(View viewToObserve, TextView viewToModify, double percent) {
        viewToObserve.post(new Runnable() {
            @Override
            public void run() {
                viewToModify.setWidth((int) (viewToObserve.getWidth() * percent));
            }
        });
    }

    @Deprecated
    public static int getImageScaleToScreenWidthPercent(Context myContext, double percent, int drawableResId) {

        WindowManager myWindowManager = (WindowManager) myContext.getSystemService(Context.WINDOW_SERVICE);

        Display display = myWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int screenWidth = size.x;

        BitmapFactory.Options bmpopt = new BitmapFactory.Options();
        bmpopt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(myContext.getResources(), drawableResId, bmpopt);
        double imageHeight = bmpopt.outHeight;
        double imageWidth = bmpopt.outWidth;

        if (imageHeight < imageWidth) {
            return (int) ((imageHeight / imageWidth) * (screenWidth * percent));
        } else {
            return (int) (screenWidth * percent);
        }

    } // end of getImageScaleToScreenWidthPercent

}
