package com.example.seamus.wordfox;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.DisplayMetrics;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageHandler {

    private static final String DEFAULT_PROFILE_IMAGE_ASSET = "default_profile_smiley.png";
    private static final int MAX_RESOLUTION_IMAGE = 2048;   // Max allowed picture resolution
    private Activity activity;

    public ImageHandler(Activity activity) {
        this.activity = activity;
    }

    // Load bitmap from asset contained within the project
    public Bitmap loadAssetImage(String assetName) {
        AssetManager assetmanager = activity.getAssets();
        InputStream inStr = null;
        try {
            inStr = assetmanager.open(assetName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(inStr);
    }

    // Rotate image to correct orientation. Example, picture may have be taken in landscape view
    private Bitmap correctOrientation(Bitmap bitmap, Uri imgUri) {
        Matrix rotateMatrix = new Matrix();
        String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
        Cursor cur = activity.getContentResolver().query(imgUri, orientationColumn, null, null, null);
        int orientation = -1;
        if (cur != null && cur.moveToFirst()) {
            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
            cur.close();
        }
        rotateMatrix.postRotate(orientation);
        if (!rotateMatrix.isIdentity() && bitmap != null) {
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, true);
        }
        return bitmap;
    }

    // Resize image to fit within screen metrics
    public static Bitmap resize(Bitmap image, DisplayMetrics metrics) {
        int maxWidth = metrics.widthPixels;
        int maxHeight = metrics.heightPixels;
        if (!(maxHeight > 0 && maxWidth > 0)) {
            return image;
        }
        // get the width and the height of the image to be resized
        int width = image.getWidth();
        int height = image.getHeight();
        // get the ratio of the image dimensions to screen dimensions
        float widthRatio = (float) width / (float) maxWidth;
        float heightRatio = (float) height / (float) maxHeight;
        // check which ratio is larger to determine which dimension is more out of bounds
        float maxRatio = (widthRatio > heightRatio) ? widthRatio : heightRatio;
        // scale down both dimensions by the ratio that's most out of bounds to bring whichever
        //  was most out of bound down to the max while maintain aspect ratio
        int finalWidth = (int) Math.floor(width / maxRatio);
        int finalHeight = (int) Math.floor(height / maxRatio);

        image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
        return image;
    }

    static public int dp2px(Context context, final float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, float newWidth, float newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = newWidth / width;
        float scaleHeight = newHeight / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }


    // Load bitmap from its location on the device. Resize it if it exceeds screen dimensions.
    // Rotate the image if it was not taken in portrait view.
    public Bitmap getBitmapFromUri(Uri imgUri) {
        return getBitmapFromUri(imgUri, 0);
    }

    public Bitmap getBitmapFromUri(Uri imgUri, int scaleToDimension) {
        Bitmap myBitmap = null;
        ContentResolver cr = activity.getContentResolver();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor myCur = cr.query(imgUri, projection, null, null, null);
        // Verify the file path returned a valid cursor
        if (myCur == null) {
            return null;
        }
        if (myCur.moveToFirst()) {
            String filePath = myCur.getString(0);
            if (!new File(filePath).exists()) {
                return null;
            }
            // Attempt to load bitmap from file path
            try {
                if (scaleToDimension > 0) {
                    BitmapFactory.Options o = new BitmapFactory.Options();
                    o.inJustDecodeBounds = true;
                    FileInputStream fis = new FileInputStream(filePath);
                    BitmapFactory.decodeStream(fis, null, o);
                    fis.close();
                    int scale = scaleFromOptions(o, scaleToDimension);
                    BitmapFactory.Options o2 = new BitmapFactory.Options();
                    o2.inSampleSize = scale;
                    fis = new FileInputStream(filePath);
                    myBitmap = BitmapFactory.decodeStream(fis, null, o2);
                    fis.close();
                    myBitmap = scaleDownTo(myBitmap, scaleToDimension);
                } else {
                    myBitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imgUri);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Resize bitmap if it exceeds screen resolution
            assert myBitmap != null;
            if (myBitmap.getHeight() > MAX_RESOLUTION_IMAGE || myBitmap.getWidth() > MAX_RESOLUTION_IMAGE) {
                DisplayMetrics metrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                myBitmap = resize(myBitmap, metrics);
            }
            // Rotate image if necessary
            myBitmap = correctOrientation(myBitmap, imgUri);
        }
        myCur.close();
        return myBitmap;
    }

//    public static Bitmap getScaledBitmap(int drawResource, int scaleFactor, Resources resources) {
//        float SCREEN_DENSITY = resources.getDisplayMetrics().density;
//        BitmapFactory.Options bmpopt = new BitmapFactory.Options();
//        bmpopt.inJustDecodeBounds = true;
//        BitmapFactory.decodeResource(resources, drawResource, bmpopt);
//        int srcWidth = bmpopt.outWidth;
//        bmpopt.inJustDecodeBounds = false;
//        bmpopt.inSampleSize = 8;
//        bmpopt.inScaled = true;
//        bmpopt.inDensity = srcWidth;
//        bmpopt.inTargetDensity = (int) ((45 * SCREEN_DENSITY) * (bmpopt.inSampleSize));
//        return BitmapFactory.decodeResource(resources, drawResource, bmpopt);
//    }
    public static Bitmap getScaledBitmap(int drawResource, int scaleToDimension, Resources resources){

        BitmapFactory.Options bmpopt = new BitmapFactory.Options();
        bmpopt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, drawResource, bmpopt);
        int scale = scaleFromOptions(bmpopt, scaleToDimension);
        bmpopt.inJustDecodeBounds = false;
        bmpopt.inSampleSize = scale;
        BitmapFactory.decodeResource(resources, drawResource, bmpopt);
        return scaleDownTo(BitmapFactory.decodeResource(resources, drawResource, bmpopt), scaleToDimension);
    }
    public static Bitmap scaleDownTo(Bitmap bmp, int scaleToDimension){
        if(bmp.getWidth() > bmp.getHeight()){
            float aspect = (float) bmp.getWidth()/bmp.getHeight();
            return getResizedBitmap(bmp, scaleToDimension*aspect, scaleToDimension);
        }else{
            float aspect = (float) bmp.getHeight()/bmp.getWidth();
            return getResizedBitmap(bmp, scaleToDimension, scaleToDimension*aspect);
        }
    }

    // Read resource file and find what factor we should scale down by
    public static int getScaleFactor(Resources resources, int drawResource, int minDimension) {
        BitmapFactory.Options bmpopt = new BitmapFactory.Options();
        bmpopt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, drawResource, bmpopt);
        return scaleFromOptions(bmpopt, minDimension);
    }
    // Calculate factor by which we should scale the image down by
    public static int scaleFromOptions(BitmapFactory.Options bmpopt, int minDimension){
        int scaleFactor = 0;
        if (bmpopt.outHeight < minDimension || bmpopt.outWidth < minDimension) {
            return scaleFactor;
        }
        int smallerSide = (bmpopt.outHeight < bmpopt.outWidth) ? bmpopt.outHeight : bmpopt.outWidth;
        scaleFactor = smallerSide / minDimension;
        return lowerPowerOfTwo(scaleFactor);
    }
    // Round down to closest power of 2
    public static int lowerPowerOfTwo(int value){
        int count = 0;
        while (value > 1){
            value = value >> 1;
            ++count;
        }
        value = value << count;
        return value;
    }
}
