package com.example.seamus.wordfox;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class ImageHandler {
    private static final int MAX_RESOLUTION_IMAGE = 2048;   // Larger resolutions risk unsupported devices
    private static final int SCALE_BY_HEIGHT = 0;
    private static final int SCALE_BY_WIDTH = 1;
    private static final int SCALE_BY_LONGEST = 2;
    private static final int SCALE_BY_SHORTEST = 3;
    private static final String MONITOR_TAG = "ImageHandler_tag";

    private ImageHandler(){}

    // Load bitmap from asset contained within the project
    public static Bitmap loadAssetImage(String assetName, Activity activity) {
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
    private static Bitmap correctOrientation(Bitmap bitmap, Uri imgUri, Activity activity) {
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
        if (metrics.heightPixels <= 0 || metrics.widthPixels <= 0) {
            return image;
        }
        // get the ratio of the image dimensions to screen dimensions
        float widthRatio = (float) image.getWidth() / (float) metrics.widthPixels;
        float heightRatio = (float) image.getHeight() / (float) metrics.heightPixels;
        // check which ratio is larger to determine which dimension is more out of bounds
        float maxRatio = (widthRatio > heightRatio) ? widthRatio : heightRatio;
        // scale down both dimensions by the ratio that's most out of bounds
        int finalWidth = (int) Math.floor(image.getWidth() / maxRatio);
        int finalHeight = (int) Math.floor(image.getHeight() / maxRatio);

        image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
        return image;
    }

    public static int dp2px(Context context, final float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, float newWidth, float newHeight) {
        return Bitmap.createScaledBitmap(bm, (int) newWidth, (int) newHeight, false);
    }

    private static boolean isStoragePermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(WordfoxConstants.MONITOR_TAG, "Permission is granted");
                return true;
            } else {
                Log.v(WordfoxConstants.MONITOR_TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    // Load bitmap from its location on the device. Resize it if it exceeds screen dimensions.
    // Rotate the image if it was not taken in portrait view.
    @Deprecated
    public static Bitmap getBitmapFromUri(Activity activity, Uri imgUri) {
        return getBitmapFromUri(activity, imgUri, 0, SCALE_BY_HEIGHT);
    }

    @Deprecated
    public static Bitmap getBitmapFromUri(Activity activity, Uri imgUri, int scaleToDimension) {
        return getBitmapFromUri(activity, imgUri, scaleToDimension, SCALE_BY_HEIGHT);
    }

    public static Bitmap getBitmapFromUriScaleWidth(Activity activity, Uri imgUri, int scaleToDimension) {
        return getBitmapFromUri(activity, imgUri, scaleToDimension, SCALE_BY_WIDTH);
    }

    public static Bitmap getBitmapFromUriScaleHeight(Activity activity, Uri imgUri, int scaleToDimension) {
        return getBitmapFromUri(activity, imgUri, scaleToDimension, SCALE_BY_HEIGHT);
    }

    public static Bitmap getBitmapFromUriScaleLongestSide(Activity activity, Uri imgUri, int scaleToDimension) {
        return getBitmapFromUri(activity, imgUri, scaleToDimension, SCALE_BY_LONGEST);
    }

    public static Bitmap getBitmapFromUriScaleShortestSide(Activity activity, Uri imgUri, int scaleToDimension) {
        return getBitmapFromUri(activity, imgUri, scaleToDimension, SCALE_BY_SHORTEST);
    }

    private static Bitmap getBitmapFromUri(Activity activity, Uri imgUri, int scaleToDimension, int scaleType) {
        if (!isStoragePermissionGranted(activity)) {
            return null;
        }
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
                BitmapFactory.Options options;
                if (scaleToDimension > 0) {
                    options = getBounds(filePath, scaleToDimension, scaleType);
                } else {
                    options = new BitmapFactory.Options();
                }
                FileInputStream fis = new FileInputStream(filePath);
                myBitmap = BitmapFactory.decodeStream(fis, null, options);
                fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Resize bitmap if it exceeds allowed resolution
            assert myBitmap != null;
            if (myBitmap.getHeight() > MAX_RESOLUTION_IMAGE || myBitmap.getWidth() > MAX_RESOLUTION_IMAGE) {
                DisplayMetrics metrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                myBitmap = resize(myBitmap, metrics);
            }
            // Rotate image if necessary
            myBitmap = correctOrientation(myBitmap, imgUri, activity);
        }
        myCur.close();
        return myBitmap;
    }

    private static BitmapFactory.Options getBounds(String filePath, int desiredDimensionSize, int scaleType) throws IOException {
        if (desiredDimensionSize <= 0) {
            return null;
        }
        BitmapFactory.Options bmpopt = new BitmapFactory.Options();
        bmpopt.inJustDecodeBounds = true;
        FileInputStream fis = new FileInputStream(filePath);
        BitmapFactory.decodeStream(fis, null, bmpopt);
        fis.close();
        return applyScaleSettings(bmpopt, desiredDimensionSize, scaleType);
    }

    private static BitmapFactory.Options applyScaleSettings(BitmapFactory.Options bmpopt, int scaleToDimension, int scaleType) {
        float newDimensionSize = decideDimensionToScale(bmpopt, scaleType);
        int scale = scaleFromOptions(bmpopt, scaleToDimension);
        bmpopt.inDensity = (int) ((bmpopt.inTargetDensity * (newDimensionSize / scaleToDimension)) / scale);
        bmpopt.inJustDecodeBounds = false;
        bmpopt.inScaled = true;
        bmpopt.inSampleSize = scale;
        return bmpopt;
    }

    @Deprecated     // Use 'getScaledBitmapByShortestSide' instead
    public static Bitmap getScaledBitmap(int drawResource, int scaleToDimension, Resources resources) {
        return getScaledBitmapByShortestSide(drawResource, scaleToDimension, resources);
    }

    public static Bitmap getScaledBitmapByShortestSide(int drawResource, int shortSide, Resources resources) {
        return getScaledBitmapByType(drawResource, shortSide, resources, SCALE_BY_SHORTEST);
    }

    public static Bitmap getScaledBitmapByLongestSide(int drawResource, int longSide, Resources resources) {
        return getScaledBitmapByType(drawResource, longSide, resources, SCALE_BY_LONGEST);
    }

    public static Bitmap getScaledBitmapByHeight(int drawResource, int heightMax, Resources resources) {
        return getScaledBitmapByType(drawResource, heightMax, resources, SCALE_BY_HEIGHT);
    }

    public static Bitmap getScaledBitmapByWidth(int drawResource, int widthMax, Resources resources) {
        return getScaledBitmapByType(drawResource, widthMax, resources, SCALE_BY_WIDTH);
    }

    private static Bitmap getScaledBitmapByType(int drawResource, int desiredDimensionSize, Resources resources, int scaleType) {
        if (desiredDimensionSize <= 0) {
            return null;
        }
        BitmapFactory.Options bmpopt = getBounds(drawResource, desiredDimensionSize, resources, scaleType);
        return BitmapFactory.decodeResource(resources, drawResource, bmpopt);
    }

    private static BitmapFactory.Options getBounds(int drawResource, int desiredDimensionSize, Resources resources, int scaleType) {
        if (desiredDimensionSize <= 0) {
            return null;
        }
        BitmapFactory.Options bmpopt = new BitmapFactory.Options();
        bmpopt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, drawResource, bmpopt);
        return applyScaleSettings(bmpopt, desiredDimensionSize, scaleType);
    }

    private static float decideDimensionToScale(BitmapFactory.Options bmpopt, int scaleType) {
        switch (scaleType) {
            case SCALE_BY_HEIGHT:
                return bmpopt.outHeight;
            case SCALE_BY_WIDTH:
                return bmpopt.outWidth;
            case SCALE_BY_SHORTEST:
                return (bmpopt.outHeight < bmpopt.outWidth ? bmpopt.outHeight : bmpopt.outWidth);
            case SCALE_BY_LONGEST:
                return (bmpopt.outHeight > bmpopt.outWidth ? bmpopt.outHeight : bmpopt.outWidth);
            default:
                throw new IllegalArgumentException();
        }
    }

    private static void log(String msg) {
        Log.d(MONITOR_TAG, msg);
    }

    private static void logBmp(Bitmap bitmap, String id) {
        log(id + " == bitmap~ h, w, bytes, dens : "
                + bitmap.getHeight() + ", "
                + bitmap.getWidth() + ", "
                + bitmap.getByteCount() + ", "
                + bitmap.getDensity());
    }

    private static void logBmp(Bitmap bitmap, int id) {
        logBmp(bitmap, String.valueOf(id));
    }

    private static void logOpt(BitmapFactory.Options options, String id) {
        log(id + " >> options~ sample, h, w, dens, targetDens, exp size : "
                + options.inSampleSize + ", "
                + options.outHeight + ", "
                + options.outWidth + ", "
                + options.inDensity + ", "
                + options.inTargetDensity + ", "
                + (options.outWidth * options.outHeight * 4));
    }

    private static void logOpt(BitmapFactory.Options options, int id) {
        logOpt(options, String.valueOf(id));
    }

    private static Bitmap scaleHeightDownTo(Bitmap bitmap, int height) {
        float aspect = (((float) height) / bitmap.getHeight());
        return getResizedBitmap(bitmap, bitmap.getWidth() * aspect, height);
    }

    private static Bitmap scaleWidthDownTo(Bitmap bitmap, int width) {
        float aspect = (((float) width) / bitmap.getWidth());
        return getResizedBitmap(bitmap, width, bitmap.getHeight() * aspect);
    }

    private static Bitmap scaleLongestDownTo(Bitmap bmp, int scaleToDimension) {
        if (bmp.getWidth() < bmp.getHeight()) {
            return scaleHeightDownTo(bmp, scaleToDimension);
        } else {
            return scaleWidthDownTo(bmp, scaleToDimension);
        }
    }

    private static Bitmap scaleShortestDownTo(Bitmap bmp, int scaleToDimension) {
        if (bmp.getWidth() > bmp.getHeight()) {
            return scaleHeightDownTo(bmp, scaleToDimension);
        } else {
            return scaleWidthDownTo(bmp, scaleToDimension);
        }
    }

    // Calculate factor by which we should scale the image down by
    private static int scaleFromOptions(BitmapFactory.Options bmpopt, int maxDimension) {
        int scaleFactor = 0;
        if (bmpopt.outHeight < maxDimension && bmpopt.outWidth < maxDimension) {
            return scaleFactor;
        }
        float largerSide = (bmpopt.outHeight > bmpopt.outWidth) ? bmpopt.outHeight : bmpopt.outWidth;
        scaleFactor = (int) (largerSide / maxDimension);    // TODO: Doesnt need to be a float I believe? answer is rounded down either way.
        return lowerPowerOfTwo(scaleFactor);
    }

    // Round down to closest power of 2
    private static int lowerPowerOfTwo(int value) {
        int count = 0;
        while (value > 1) {
            value = value >> 1;
            ++count;
        }
        value = value << count;
        return value;
    }
}
