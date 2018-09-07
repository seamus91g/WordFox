package com.example.seamus.wordfox.profile;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.constraint.Group;
import android.support.v4.app.ActivityCompat;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;

import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.GridImage;
import com.example.seamus.wordfox.ImageHandler;
import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.database.FoxSQLData;
import com.example.seamus.wordfox.datamodels.GameItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Gilroy on 4/19/2018.
 */

public class ProfilePresenter implements ProfileContract.Listener {
    private static final String DEFAULT_PROFILE_IMAGE_ASSET = "default_profile_smiley.png";
    private static final String MONITOR_TAG = "myTag";
    private static final int MAX_RESOLUTION_IMAGE = 2048;   // Max allowed picture resolution

    private final GameData myGameData;
    private final Activity activity;
    private final ProfileContract.View view;

    public ProfilePresenter(ProfileActivity activity, GameData data) {
        this.activity = activity;
        this.view = activity;
        this.myGameData = data;
    }

    // Find longest word and display it on the profile screen
    public void displayLongestWord() {
        view.setLongestWord(myGameData.findLongest());
    }

    // Save username to preference file. Update the displayed profile name.
    public void updateProfileName(String name) {
        myGameData.setUsername(name);
        displayProfileName();
    }

    // Display the user name. Do not display if still default
    public void displayProfileName() {
        String username_prof = myGameData.getUsername();
        if (!username_prof.equals(GameData.DEFAULT_P1_NAME)) {  // TODO .. This can never be true?? Default return is Fox
            view.setUsername(username_prof);
        }
    }

    // Attempt to load and display the profile image.
    public void displayProfileImage() {
        Bitmap bitmap;
        if (isStoragePermissionGranted()) {
            bitmap = permissionGrantedDisplayImage();
        }else{
            bitmap = defaultProfImg(new ImageHandler(activity));
        }
        view.setProfileImage(bitmap);
    }

    // permission granted so get the image
    public Bitmap permissionGrantedDisplayImage() {
        String profPicStr = myGameData.getProfilePicture();
        Bitmap bitmap = null;
        ImageHandler imageHandler = new ImageHandler(activity);
        if (!profPicStr.equals("")) {
            Uri myFileUri = Uri.parse(profPicStr);
            bitmap = imageHandler.getBitmapFromUri(myFileUri);
        }
        // Check exists even if string exists. Could be null if user has deleted the image
        if (bitmap != null) {
            view.setAdjustViewBounds(true);     // TODO: ... this should work even for default
        } else {
            bitmap = defaultProfImg(imageHandler);
        }
        return bitmap;
    }
    private Bitmap defaultProfImg(ImageHandler imageHandler){
        return imageHandler.loadAssetImage(DEFAULT_PROFILE_IMAGE_ASSET);
    }

    // When user is finished choosing a picture from the image gallery
    public void activityResult(Intent data) {
        Uri selectedImage = data.getData();
        assert selectedImage != null;
        myGameData.setProfilePicture(selectedImage.toString());     // Save path to chosen pic for future loading
        displayProfileImage();
    }

    // Load bitmap from asset contained within the project
//    public Bitmap loadAssetImage(String assetName) {
//        AssetManager assetmanager = activity.getAssets();
//        InputStream inStr = null;
//        try {
//            inStr = assetmanager.open(assetName);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return BitmapFactory.decodeStream(inStr);
//    }

    // Load bitmap from its location on the device. Resize it if it exceeds screen dimensions.
    // Rotate the image if it was not taken in portrait view.
//    private Bitmap getBitmapFromUri(Uri imgUri) {
//        Bitmap myBitmap = null;
//        ContentResolver cr = activity.getContentResolver();
//        String[] projection = {MediaStore.MediaColumns.DATA};
//        Cursor myCur = cr.query(imgUri, projection, null, null, null);
//        // Verify the file path returned a valid cursor
//        if (myCur == null) {
//            return null;
//        }
//        if (myCur.moveToFirst()) {
//            String filePath = myCur.getString(0);
//            if (!new File(filePath).exists()) {
//                return null;
//            }
//            // Attempt to load bitmap from file path
//            try {
//                myBitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imgUri);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//            // Resize bitmap if it exceeds screen resolution
//            assert myBitmap != null;
//            if (myBitmap.getHeight() > MAX_RESOLUTION_IMAGE || myBitmap.getWidth() > MAX_RESOLUTION_IMAGE) {
//                DisplayMetrics metrics = new DisplayMetrics();
//                activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                myBitmap = resize(myBitmap, metrics);
//            }
//            // Rotate image if necessary
//            myBitmap = correctOrientation(myBitmap, imgUri);
//        }
//        myCur.close();
//        return myBitmap;
//    }

    // Rotate image to correct orientation. Example, picture may have be taken in landscape view
//    private Bitmap correctOrientation(Bitmap bitmap, Uri imgUri) {
//        Matrix rotateMatrix = new Matrix();
//        String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
//        Cursor cur = activity.getContentResolver().query(imgUri, orientationColumn, null, null, null);
//        int orientation = -1;
//        if (cur != null && cur.moveToFirst()) {
//            orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
//            cur.close();
//        }
//        rotateMatrix.postRotate(orientation);
//        if (!rotateMatrix.isIdentity() && bitmap != null) {
//            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, true);
//        }
//        return bitmap;
//    }

    // Resize image to fit within screen metrics
//    private Bitmap resize(Bitmap image, DisplayMetrics metrics) {
//        int maxWidth = metrics.widthPixels;
//        int maxHeight = metrics.heightPixels;
//        if (!(maxHeight > 0 && maxWidth > 0)) {
//            return image;
//        }
//        // get the width and the height of the image to be resized
//        int width = image.getWidth();
//        int height = image.getHeight();
//        // get the ratio of the image dimensions to screen dimensions
//        float widthRatio = (float) width / (float) maxWidth;
//        float heightRatio = (float) height / (float) maxHeight;
//        // check which ratio is larger to determine which dimension is more out of bounds
//        float maxRatio = (widthRatio > heightRatio) ? widthRatio : heightRatio;
//        // scale down both dimensions by the ratio that's most out of bounds to bring whichever
//        //  was most out of bound down to the max while maintain aspect ratio
//        int finalWidth = (int) Math.floor(width / maxRatio);
//        int finalHeight = (int) Math.floor(height / maxRatio);
//        Log.d(MONITOR_TAG, "New width is " + finalWidth + ", New height is " + finalHeight + ", END");
//
//        image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
//        return image;
//    }

    // Allow user to choose image from their phone when the profile image is clicked
    public void choosePicture() {
        if (isStoragePermissionGranted()) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            intent.setType("image/*");
            activity.startActivityForResult(intent, ProfileActivity.SELECT_PICTURE);
        }
    }

    // Write storage permission must be granted before an image can be loaded
    private boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(MONITOR_TAG, "Permission is granted");
                return true;
            } else {
                Log.v(MONITOR_TAG, "Permission is revoked");
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            return true;
        }
    }

    public void recentGameWords() {
        String rgID = myGameData.getRecentGame();
        if(rgID.equals("")){        // TODO: Use a constant instead of ""?
            view.hideRecentGame();
            return;
        }
        FoxSQLData foxData = new FoxSQLData(activity);
        foxData.open();
        GameItem recentGame = foxData.getGame(rgID);
        // Load data for player. Use this to find recent words and Game ID. Use Game ID to load Game from SQL DB
        ArrayList<ArrayList<String>> words = recentGame.getWinnerWords();
        ArrayList<String> recentLetters = recentGame.getLetters(foxData);
        // Winner words
        ArrayList<String> yourWords = myGameData.getRecentWords();
        ArrayList<String> winnerWords;
        ArrayList<String> winners = recentGame.getWinners();
        String winnerMsg;

        if (winners.contains(GameData.DEFAULT_P1_NAME)) {
            // Hide 'you' section
            // Create message: 'You won'
            winnerMsg = "You won!";
            view.setRecentGameYourWordsInvisible();
            winnerWords = yourWords;
        } else {
            int score = 0;
            for (int i = 0; i < yourWords.size(); ++i) {
                Bitmap bmp = pressedKey(recentLetters.get(i), yourWords.get(i));
                view.setRecentWordYou(bmp, i, yourWords.get(i));
                score += yourWords.get(i).length();
            }
            winnerMsg = winners.get(0) + " won";
            String msg = "Your score: " + score;
            view.setRecentGameWinnerYourMessage(msg);
            winnerWords = recentGame.getWinnerWords().get(0);
        }

        int winnerScore = 0;
        for (int i = 0; i < winnerWords.size(); ++i) {
            Bitmap bmp = pressedKey(recentLetters.get(i), words.get(0).get(i));
            view.setRecentWord(bmp, i, words.get(0).get(i));
            winnerScore += words.get(0).get(i).length();
        }
        winnerMsg = winnerMsg + " (" + winnerScore + ")";
        view.setRecentGameWinnerMessage(winnerMsg);
    }

    public void bestGameWords() {
        ArrayList<String> bestWords = myGameData.getBestWords();
        if(bestWords.get(0).equals(GameData.NON_EXISTANT)){
            view.hideBestGame();
            return;
        }
        ArrayList<String> bestLetters = myGameData.getBestLetters();

        for (int i = 0; i < bestWords.size(); ++i) {
            Bitmap bmp = pressedKey(bestLetters.get(i), bestWords.get(i));
            view.setBestWord(bmp, i, bestWords.get(i));
        }
    }

    private Bitmap pressedKey(String letters, String word) {
        if (letters.equals(GameData.NONE_FOUND)) {    // TODO: Throw exception
            return null;
        }
        if (word.equals(GameData.NONE_FOUND)) {
            return null;
        }

        GridImage grid = new GridImage(view.getButtonGridImage(), word, letters, view.getNotPressedButtonColor(), view.getPressedButtonColorSecondary());
        return grid.getBmp();
    }

}
