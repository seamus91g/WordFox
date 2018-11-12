package capsicum.game.wordfox;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class IVmethods {

    public static void setTVwidthPercentOfIV(ImageView instructionFoxSpeechBubbleIV,
                                             TextView instructionFoxTV, double percent, int stringResId) {

        int[] xy1 = new int[3];
        Rect myRect = new Rect();

        instructionFoxSpeechBubbleIV.post(new Runnable() {
            @Override
            public void run() {
                instructionFoxSpeechBubbleIV.getGlobalVisibleRect(myRect);
                xy1[2] = myRect.width();

                if (stringResId != 0){
                    instructionFoxTV.setText(stringResId);
                }

                instructionFoxTV.requestLayout();
                instructionFoxTV.setWidth((int) (xy1[2] * percent));
            }
        });

    }


    public static void setTVwidthPercentOfIV(ImageView instructionFoxSpeechBubbleIV,
                                             TextView instructionFoxTV, double percent, String myString) {
        setTVwidthPercentOfIV(instructionFoxSpeechBubbleIV, instructionFoxTV, percent, 0);
        instructionFoxTV.setText(myString);
    }

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
