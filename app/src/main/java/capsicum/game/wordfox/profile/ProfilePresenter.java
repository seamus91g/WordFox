package capsicum.game.wordfox.profile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import capsicum.game.wordfox.GameData;
import capsicum.game.wordfox.GridImage;
import capsicum.game.wordfox.ImageHandler;
import capsicum.game.wordfox.R;
import capsicum.game.wordfox.database.FoxSQLData;
import capsicum.game.wordfox.datamodels.GameItem;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.UUID;

import static capsicum.game.wordfox.ImageHandler.resize;
import static capsicum.game.wordfox.WordfoxConstants.MAX_PLAYER_NAME_LENGTH;

/**
 * Created by Gilroy
 */

public class ProfilePresenter implements ProfileContract.Listener {
    private static final String DEFAULT_PROFILE_IMAGE_ASSET = "profile_pic_default.png";
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
        if (name.length() == 0) {
            displayProfileName();
            return;
        }
        if (name.length() > MAX_PLAYER_NAME_LENGTH) {
            name = name.substring(0, MAX_PLAYER_NAME_LENGTH);
        }
        myGameData.setUsername(name);
        displayProfileName();
    }

    // Display the user name. Do not display if still default
    public void displayProfileName() {
        String username_prof = myGameData.getUsername();
        view.setUsername(username_prof);
    }

    // Attempt to load and display the profile image.
    public void displayProfileImage(int screenHeight, Resources myResources) {
        Bitmap bitmap;
        if (isStoragePermissionGranted()) {
            bitmap = permissionGrantedDisplayImage(screenHeight, myResources);
        } else {
            bitmap = ImageHandler.getScaledBitmapByHeight(R.drawable.chooseprofilepicwhite, screenHeight/4, myResources);
    }
        view.setProfileImage(bitmap);
    }

    // permission granted so get the image
    public Bitmap permissionGrantedDisplayImage(int screenHeight, Resources myResources) {
        String profPicStr = myGameData.getProfilePicture();
        Bitmap bitmap = null;
        if (!profPicStr.equals("")) {
            Uri myFileUri = Uri.parse(profPicStr);
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            metrics.heightPixels = (screenHeight/4);
            bitmap = resize(ImageHandler.getBitmapFromUri(activity, myFileUri), metrics);
        }
        // Check exists even if string exists. Could be null if user has deleted the image
        if (bitmap != null) {
            view.setAdjustViewBounds(true);     // TODO: ... this should work even for default
        } else {
            bitmap = ImageHandler.getScaledBitmapByHeight(R.drawable.chooseprofilepicwhite, screenHeight/4, myResources);
        }
        return bitmap;
    }


    // When user is finished choosing a picture from the image gallery
    public void activityResult(Intent data, int screenHeight) {
        Uri selectedImage = data.getData();
        assert selectedImage != null;
        myGameData.setProfilePicture(selectedImage.toString());     // Save path to chosen pic for future loading
        displayProfileImage(screenHeight, activity.getResources());
    }

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
                Timber.d( "Permission is granted");
                return true;
            } else {
                Timber.d( "Permission is revoked");
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
        if (rgID.equals("")) {        // TODO: Use a constant instead of ""?
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
        ArrayList<UUID> winners = recentGame.getWinners();
        String winnerMsg;

        if (winners.contains(myGameData.getPlayerID())) {
            // Hide 'you' section
            // Create message: 'You won'
            winnerMsg = (recentGame.getPlayerCount() == 1)? "Just me!" :  "You won!";
            view.setRecentGameYourWordsInvisible();
            winnerWords = yourWords;
        } else {
            int score = 0;
            for (int i = 0; i < yourWords.size(); ++i) {
                Bitmap bmp = pressedKey(recentLetters.get(i), yourWords.get(i));
                view.setRecentWordYou(bmp, i, yourWords.get(i));
                score += yourWords.get(i).length();
            }
            String winnerName = "<b>" + GameData.getUsername(winners.get(0), activity) + "</b>";
            winnerMsg = winnerName + " won";
            String msg = "Your score: " + score;
            view.setRecentGameWinnerYourMessage(msg);
            winnerWords = recentGame.getWinnerWords().get(0);
        }

        int winnerScore = 0;
        for (int i = 0; i < winnerWords.size(); ++i) {
            Bitmap bmp = pressedKey(recentLetters.get(i), words.get(0).get(i));
            view.setRecentWord(bmp, i, words.get(0).get(i) + " (" + words.get(0).get(i).length() + ")");
            winnerScore += words.get(0).get(i).length();
        }
        winnerMsg = winnerMsg + " (" + winnerScore + ")";
        view.setRecentGameWinnerMessage(winnerMsg);
    }

    public void bestGameWords() {
        ArrayList<String> bestWords = myGameData.getBestWords();
        if (bestWords.get(0).equals(GameData.NON_EXISTANT)) {
            view.hideBestGame();
            return;
        }
        ArrayList<String> bestLetters = myGameData.getBestLetters();

        for (int i = 0; i < bestWords.size(); ++i) {
            Bitmap bmp = pressedKey(bestLetters.get(i), bestWords.get(i));
            view.setBestWord(bmp, i, bestWords.get(i) + " (" + bestWords.get(i).length() + ")" );
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

    public void displayRank() {
        FoxRank foxRank = myGameData.getHighRank();
        view.setRankText(foxRank.foxRank);
        view.setRankImage(ImageHandler.getScaledBitmap(foxRank.imageResource, 120, activity.getResources()));
    }

    public int getFoxRank(){
        FoxRank foxRank = myGameData.getHighRank();
        return foxRank.imageResource;
    }
}
