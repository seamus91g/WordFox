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
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.database.FoxSQLData;
import com.example.seamus.wordfox.datamodels.GameItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Gilroy on 4/19/2018.
 */

class ProfilePresenter implements ProfileContract.Listener {
    private static final String DEFAULT_PROFILE_IMAGE_ASSET = "default_profile_smiley.png";
    private static final String MONITOR_TAG = "myTag";
    private static final int MAX_SCREEN_DIMENSION = 2048;

    private final GameData myGameData;
    private final Activity activity;
    private final ProfileContract.View view;

    ProfilePresenter(ProfileActivity activity) {
        this.activity = activity;
        this.view = activity;
        myGameData = new GameData(activity.getApplicationContext(), GameData.DEFAULT_P1_NAME);
    }
    // Find longest word and display it on the profile screen
    void displayLongestWord() {
        String longestWord = myGameData.findLongest();
        view.setLongestWord(longestWord);
    }
    // Save username to preference file. Update the displayed profile name.
    void updateProfileName(String name) {
        myGameData.setUsername(name);
        displayProfileName();
    }
    // Display the user name. Do not display if still default
    void displayProfileName() {
        String username_prof = myGameData.getUsername();
        if (!username_prof.equals(GameData.DEFAULT_P1_NAME)) {  // TODO .. This can never be true?? Default return is Fox
            view.setUsername(username_prof);
        }
    }
    // Attempt to load and display the profile image.
    void displayProfileImage() {
        if (isStoragePermissionGranted()) {
            permissionGrantedDisplayImage();
        }
    }
    // permission granted so get the image
    void permissionGrantedDisplayImage() {
        String profPicStr = myGameData.getProfilePicture();
        Bitmap bitmap = null;
        if (!profPicStr.equals("")) {
            Uri myFileUri = Uri.parse(profPicStr);
            bitmap = getBitmapFromUri(myFileUri);
        }
        // Check exists even if string exists. Could be null if user has deleted the image
        if (bitmap != null) {
            view.setAdjustViewBounds(true);
        } else {
            bitmap = loadAssetImage(DEFAULT_PROFILE_IMAGE_ASSET);
        }
        view.setProfileImage(bitmap);
    }
    // When user is finished choosing a picture from the image gallery
    void activityResult(Intent data) {
        Uri selectedImage = data.getData();
        assert selectedImage != null;
        myGameData.setProfilePicture(selectedImage.toString());     // Save path to chosen pic for future loading
        displayProfileImage();
    }
    // Load bitmap from asset contained within the project
    private Bitmap loadAssetImage(String assetName) {
        AssetManager assetmanager = activity.getAssets();
        InputStream inStr = null;
        try {
            inStr = assetmanager.open(assetName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(inStr);
    }
    // Load bitmap from its location on the device. Resize it if it exceeds screen dimensions.
    // Rotate the image if it was not taken in portrait view.
    private Bitmap getBitmapFromUri(Uri imgUri) {
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
                myBitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), imgUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            // Resize bitmap if it exceeds screen resolution
            assert myBitmap != null;
            if (myBitmap.getHeight() > MAX_SCREEN_DIMENSION || myBitmap.getWidth() > MAX_SCREEN_DIMENSION) {
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
    // Rotate image to correct orientation. Example, picture may have be taken in landscape view
    private Bitmap correctOrientation(Bitmap bitmap, Uri imgUri){
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
    private Bitmap resize(Bitmap image, DisplayMetrics metrics) {
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
        Log.d(MONITOR_TAG, "New width is " + finalWidth + ", New height is " + finalHeight + ", END");

        image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
        return image;
    }
    // Allow user to choose image from their phone when the profile image is clicked
    void choosePicture() {
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
            Log.v(MONITOR_TAG, "Permission is granted");
            return true;
        }
    }
    // Display statistics from the most recent played game
    void displayRecentGame() {
        String rgID = myGameData.getRecentGame();
        // Exit if no recent game exists
        if (rgID.equals("")) {
            return;
        }
        ArrayList<String> recentWords = myGameData.getRecentWords();

        FoxSQLData foxData = new FoxSQLData(activity);
        foxData.open();
        GameItem recentGame = foxData.getGame(rgID);
        // Load data for player. Use this to find recent words and Game ID. Use Game ID to load Game from SQL DB
        ArrayList<String> lastGameStrings = new ArrayList<>();

        // Declare winner
        ArrayList<String> winners = recentGame.getWinners();
        String winMessage;
        if (winners.size() > 1) {
            winMessage = "Draw: " + recentGame.getWinnerString();
        } else {
            winMessage = "Winner: " + winners.get(0);
        }
        lastGameStrings.add(winMessage);

        ArrayList<ArrayList<String>> words = recentGame.getWinnerWords();
        // Winner words
        StringBuilder myRec = new StringBuilder();
        for (int i = 0; i < words.size(); ++i) {
            myRec.append(words.get(0).get(i));
            myRec.append(", ");
        }
        String myRecent = myRec.toString();
        lastGameStrings.add(myRecent);

        // Recent words
        winMessage = "Your words: ";
        lastGameStrings.add(winMessage);
        myRec = new StringBuilder();
        for (int i = 0; i < recentWords.size(); ++i) {
            myRec.append(recentWords.get(i));
            myRec.append(", ");
        }
        myRecent = myRec.toString();
        lastGameStrings.add(myRecent);

        ArrayList<String> recentLetter = recentGame.getLetters(foxData);
        ArrayList<String> recentBest = recentGame.getLongestWords(foxData);
        StringBuilder lettersAndWords = new StringBuilder();
        for (int i = 0; i < recentLetter.size(); ++i) {
            lettersAndWords.append(recentLetter.get(i));
            lettersAndWords.append("\t\t\t");
            lettersAndWords.append(recentBest.get(i));
            lettersAndWords.append("\n");
        }
        lastGameStrings.add(lettersAndWords.toString());

        view.setDataPreviousGame(lastGameStrings);
    }
    // Find best words found in a game
    void displayBestWords() {
        ArrayList<String> bestWords = myGameData.getBestWords();
        // Exit if no best words exist
        if (bestWords.get(0).equals(GameData.NONE_FOUND)) {
            return;
        }
        ArrayList<String> bestWordsStrings = new ArrayList<>();
        bestWordsStrings.add("== Best Words == ");
        for (String word : bestWords){
            bestWordsStrings.add(word + " (" + word.length() + ")");
        }
        view.setBestWords(bestWordsStrings);
    }
}
