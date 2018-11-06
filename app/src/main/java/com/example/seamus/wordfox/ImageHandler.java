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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ImageHandler {
    private static final int MAX_RESOLUTION_IMAGE = 2048;   // Max allowed picture resolution
    private Activity activity;
    private final static int SCALE_BY_HEIGHT = 0;
    private final static int SCALE_BY_WIDTH = 1;
    private final static int SCALE_BY_LONGEST = 2;
    private final static int SCALE_BY_SHORTEST = 3;

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
        // get the screenWidth and the screenHeight of the image to be resized
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

    private boolean isStoragePermissionGranted() {
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
    public Bitmap getBitmapFromUri(Uri imgUri) {
        return getBitmapFromUri(imgUri, 0);
    }

    @Deprecated
    public Bitmap getBitmapFromUri(Uri imgUri, int scaleToDimension) {
        return getBitmapFromUri(imgUri, scaleToDimension, SCALE_BY_HEIGHT);
    }

    public Bitmap getBitmapFromUriScaleWidth(Uri imgUri, int scaleToDimension) {
        return getBitmapFromUri(imgUri, scaleToDimension, SCALE_BY_WIDTH);
    }

    public Bitmap getBitmapFromUriScaleHeight(Uri imgUri, int scaleToDimension) {
        return getBitmapFromUri(imgUri, scaleToDimension, SCALE_BY_HEIGHT);
    }

    public Bitmap getBitmapFromUriScaleLongestSide(Uri imgUri, int scaleToDimension) {
        return getBitmapFromUri(imgUri, scaleToDimension, SCALE_BY_LONGEST);
    }

    public Bitmap getBitmapFromUriScaleShortestSide(Uri imgUri, int scaleToDimension) {
        return getBitmapFromUri(imgUri, scaleToDimension, SCALE_BY_SHORTEST);
    }
    
    private Bitmap getBitmapFromUri(Uri imgUri, int scaleToDimension, int scaleType) {
        if (!isStoragePermissionGranted()) {
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
                if (scaleToDimension > 0) {         // TODO: Refactor, code is duplicate of below
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
                    if (scaleType == SCALE_BY_HEIGHT) {
                        myBitmap = scaleHeightDownTo(myBitmap, scaleToDimension);
                    } else if (scaleType == SCALE_BY_WIDTH) {
                        myBitmap = scaleWidthDownTo(myBitmap, scaleToDimension);
                    } else if (scaleType == SCALE_BY_LONGEST) {
                        myBitmap = scaleLongestDownTo(myBitmap, scaleToDimension);
                    } else if (scaleType == SCALE_BY_SHORTEST) {
                        myBitmap = scaleShortestDownTo(myBitmap, scaleToDimension);
                    } else {
                        return null;
                    }
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

    private static BitmapFactory.Options getBounds(int drawResource, int scaleToDimension, Resources resources) {
        if (scaleToDimension <= 0) {
            return null;
        }
        BitmapFactory.Options bmpopt = new BitmapFactory.Options();
        bmpopt.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(resources, drawResource, bmpopt);

        int scale = scaleFromOptions(bmpopt, scaleToDimension);
        bmpopt.inJustDecodeBounds = false;
        bmpopt.inSampleSize = scale;
        BitmapFactory.decodeResource(resources, drawResource, bmpopt);
        return bmpopt;
    }

    @Deprecated     // Use 'getScaledBitmapByShortestSide' instead
    public static Bitmap getScaledBitmap(int drawResource, int scaleToDimension, Resources resources) {
        return getScaledBitmapByShortestSide(drawResource, scaleToDimension, resources);
    }

    public static Bitmap getScaledBitmapByShortestSide(int drawResource, int scaleToDimension, Resources resources) {
        if (scaleToDimension <= 0) {
            return null;
        }
        BitmapFactory.Options bmpopt = getBounds(drawResource, scaleToDimension, resources);
        Bitmap bm = BitmapFactory.decodeResource(resources, drawResource, bmpopt);
        return scaleShortestDownTo(bm, scaleToDimension);
    }

    public static Bitmap getScaledBitmapByLongestSide(int drawResource, int scaleToDimension, Resources resources) {
        if (scaleToDimension <= 0) {
            return null;
        }
        BitmapFactory.Options bmpopt = getBounds(drawResource, scaleToDimension, resources);
        Bitmap bm = BitmapFactory.decodeResource(resources, drawResource, bmpopt);
        return scaleLongestDownTo(bm, scaleToDimension);
    }

    public static Bitmap getScaledBitmapByWidth(int drawResource, int widthMax, Resources resources) {
        if (widthMax <= 0) {
            return null;
        }
        BitmapFactory.Options bmpopt = getBounds(drawResource, widthMax, resources);
        Bitmap bm = BitmapFactory.decodeResource(resources, drawResource, bmpopt);
        return scaleWidthDownTo(bm, widthMax);

    }

    public static Bitmap getScaledBitmapByHeight(int drawResource, int heightMax, Resources resources) {
        if (heightMax <= 0) {
            return null;
        }
        BitmapFactory.Options bmpopt = getBounds(drawResource, heightMax, resources);
        Bitmap bm = BitmapFactory.decodeResource(resources, drawResource, bmpopt);
        return scaleHeightDownTo(bm, heightMax);
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
    private static int scaleFromOptions(BitmapFactory.Options bmpopt, int minDimension) {
        int scaleFactor = 0;
        if (bmpopt.outHeight < minDimension || bmpopt.outWidth < minDimension) {
            return scaleFactor;
        }
        int smallerSide = (bmpopt.outHeight < bmpopt.outWidth) ? bmpopt.outHeight : bmpopt.outWidth;
        scaleFactor = smallerSide / minDimension;
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
