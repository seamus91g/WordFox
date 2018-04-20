package com.example.seamus.wordfox;

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
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seamus.wordfox.database.FoxSQLData;
import com.example.seamus.wordfox.datamodels.GameItem;
import com.example.seamus.wordfox.profile.ProfilePresenter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.GenericArrayType;
import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity
    implements ActivityCompat.OnRequestPermissionsResultCallback, NavigationView.OnNavigationItemSelectedListener {
    private static final int SELECT_PICTURE = 0;
    private static final String MONITOR_TAG = "myTag";
    private GameData myGameData;
    private NavigationBurger navBurger = new NavigationBurger();
    private Menu menu;
    private ProfilePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        presenter = new ProfilePresenter(this);
        updateLongestWord();
        // Display the user name. Do not display if still default "fox"
        EditText et = (EditText) findViewById(R.id.username_profile);
        String username_prof = myGameData.getUsername();
//        String username_prof = GameData.getPlayer1Name(this);
        if (!username_prof.equals(GameData.DEFAULT_P1_NAME)) {  // TODO .. This can never be true?? Default return is Fox
            et.setText(username_prof);
        }
        // Button to save user name to GameData class.
        Button setProfileNameButton = (Button) findViewById(R.id.button);
        setProfileNameButton.setOnClickListener(usernameButtonListener);

        // Display details of most recently played game
        showRecentGame();
        showBestWords();
        // Display the profile pic if one exists.
//        String profPicStr = myGameData.getProfilePicture();
        ImageView profileIB = (ImageView) findViewById(R.id.profileImageButton);
        Bitmap profBitmap = presenter.loadProfileImage();
//        if (!profPicStr.equals("") && isStoragePermissionGranted()) {
//            Uri myFileUri = Uri.parse(profPicStr);
//            profBitmap = getBitmapFromUri(myFileUri);
            // If failed to load, get default
//            if (profBitmap == null){
//                profBitmap = loadAssetImage(DEFAULT_PROFILE_IMAGE_ASSET);
//            }else{
//                profileIB.setAdjustViewBounds(true);
//            }
//        }else{
//            profBitmap = loadAssetImage(DEFAULT_PROFILE_IMAGE_ASSET);
//        }

        profileIB.setImageBitmap(profBitmap);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // if permission has been granted resume tasks needing this permission
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.v(MONITOR_TAG, "Permission granted: " + permissions[0] + " was: " + grantResults[0]);

            // permission granted so get the image
            String profPicStr = myGameData.getProfilePicture();
            ImageView profileIB = (ImageView) findViewById(R.id.profileImageButton);

            if (!profPicStr.equals("")) {
                Uri myFileUri = Uri.parse(profPicStr);
                Bitmap bitmap = getBitmapFromUri(myFileUri);

                // Check it exists. Could be null if user has deleted the image from gallery
                if (bitmap != null) {
                    profileIB.setImageBitmap(bitmap);
                } else {
                    Log.d(MONITOR_TAG, "Setting default profile image, END");
                    profileIB.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_action_name));
                }

            } else { // if there's no image already set choose the image
                choosePicture(profileIB);
            }

        } else { // permission not granted so can't do anything with the image
            Toast.makeText(this, "Default profile icon will remain", Toast.LENGTH_SHORT).show();
        }

    }



    public void showBestWords(){

        GameData plyrGd = new GameData(this, GameData.DEFAULT_P1_NAME);
        ArrayList<String> bestWords = plyrGd.getBestWords();
        // Exit if no best words exist
        if (bestWords.get(0).equals(GameData.NONE_FOUND)){
            return;
        }

        ArrayList<String> bestWordsStrings = new ArrayList<>();
        bestWordsStrings.add("== Best Words == ");
        bestWordsStrings.add(bestWords.get(0) + " (" + bestWords.get(0).length() + ")");
        bestWordsStrings.add(bestWords.get(1) + " (" + bestWords.get(1).length() + ")");
        bestWordsStrings.add(bestWords.get(2) + " (" + bestWords.get(2).length() + ")");


        for (String message : bestWordsStrings) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            textView.setText(message);
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.recent_game);
            linearLayout.addView(textView);
        }

    }
    public void showRecentGame(){
        GameData plyrGd = new GameData(this, GameData.DEFAULT_P1_NAME);
        String rgID = plyrGd.getRecentGame();
        // Exit if no recent game exists
        if (rgID.equals("")){
            return;
        }
        ArrayList<String> recentWords = plyrGd.getRecentWords();

        FoxSQLData foxData = new FoxSQLData(this);
        foxData.open();
        GameItem recentGame = foxData.getGame(rgID);
        // Load data for player. Use this to find recent words and Game ID. Use Game ID to load Game from SQL DB
        ArrayList<String> lastGameStrings = new ArrayList<>();


        // Declare winner
        ArrayList<String> winners = recentGame.getWinners();
        String winMessage;
        if (winners.size() > 1) {
            winMessage = "Draw: " + recentGame.getWinnerString();
        }else{
            winMessage = "Winner: " + winners.get(0);
        }
        lastGameStrings.add(winMessage);

        recentGame.getWinnerWords();
        // Winner words
        StringBuilder myRec = new StringBuilder();
        for (int i=0; i<recentGame.getWinnerWords().get(0).size(); ++i){
            myRec.append(recentGame.getWinnerWords().get(0).get(i));
            myRec.append(", ");
        }
        String myRecent = myRec.toString();
        lastGameStrings.add(myRecent);

        // Recent words
        winMessage = "Your words: ";
        lastGameStrings.add(winMessage);
        myRec = new StringBuilder();
        for (int i=0; i<recentWords.size(); ++i){
            myRec.append(recentWords.get(i));
            myRec.append(", ");
        }
        myRecent = myRec.toString();
        lastGameStrings.add(myRecent);

        FoxSQLData myDB = new FoxSQLData(this);
        ArrayList<String> recentLetter = recentGame.getLetters(myDB);
        ArrayList<String> recentBest = recentGame.getLongestWords(myDB);
        StringBuilder lettersAndWords = new StringBuilder();
        for (int i=0; i<recentLetter.size(); ++i){
            lettersAndWords.append(recentLetter.get(i));
            lettersAndWords.append("\t\t\t");
            lettersAndWords.append(recentBest.get(i));
            lettersAndWords.append("\n");
        }
        lastGameStrings.add(lettersAndWords.toString());

        for (String message : lastGameStrings) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            textView.setText(message);
            LinearLayout linearLayout = (LinearLayout) findViewById(R.id.recent_game);
            linearLayout.addView(textView);
        }
    }

    // Allow user to choose image from their phone when the profile image is clicked
    public void choosePicture(View v) {
        if (isStoragePermissionGranted()) {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PICTURE);
        }
    }

    @Override
    // When the user chooses an image, try to load it. Save their choice to GameData
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            Log.d(MONITOR_TAG, "Looking for the image, END");
            Uri selectedImage = data.getData();     // Path to the image
            Bitmap bitmap = getBitmapFromUri(selectedImage);
            ImageView profileIB = (ImageView) findViewById(R.id.profileImageButton);
            if (bitmap != null) {
                profileIB.setImageBitmap(bitmap);
            } else {
                Log.d(MONITOR_TAG, "Setting default profile image, END");
                profileIB.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_action_name));
            }
            myGameData.setProfilePicture(selectedImage);    // Save path to chosen pic for future loading
        }
    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        Log.d(MONITOR_TAG, "starting resize now END");

        if (maxHeight > 0 && maxWidth > 0) {

            // get the width and the height of the image to be resized
            int width = image.getWidth();
            int height = image.getHeight();

            // get the ratio of the image width to the screen (max) width
            float widthRatio = (float) width / (float) maxWidth;

            // get the ratio of the image height to the screen (max) height
            float heightRatio = (float) height / (float) maxHeight;

            // check which ratio is larger to determine which dimension is more out of bounds
            float maxRatio = (widthRatio > heightRatio) ? widthRatio : heightRatio;

            //scale down both dimensions by the ratio that's most out of bounds to bring whichever
            // was most out of bound down to the max while maintain aspect ratio
            int finalWidth = (int) Math.floor(width/maxRatio);
            int finalHeight = (int) Math.floor(height/maxRatio);
            Log.d(MONITOR_TAG, "finalWidth is " + finalWidth + " finalHeight is " + finalHeight + " END");

            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;

        } else {
            return image;
        }
    }

    // User can type into text field and click 'save' button to save their profile user name
    private View.OnClickListener usernameButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText et = (EditText) findViewById(R.id.username_profile);
            String username_prof = et.getText().toString();
//            GameData.setPlayer1Name(v.getContext(), username_prof);
            myGameData.setUsername(username_prof);
            et.clearFocus();
            Log.d(MONITOR_TAG, "User entered: " + username_prof + ", END");
        }
    };

    // Find longest word and display it on the profile screen
    public void updateLongestWord() {
        String longestWord = myGameData.findLongest();
        TextView longestWordProfilePage = (TextView) findViewById(R.id.profPicLongestWord);
        longestWordProfilePage.setText(longestWord);
        Log.d(MONITOR_TAG, "Printing longest word: " + longestWord + ", END");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                // User chose the "Profile" item, jump to the profile page
                Log.d(MONITOR_TAG, "Chose des's profile icon, END");
                Intent profileScreenIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(profileScreenIntent);
                return true;

            // Use this for other action bar items as necessary
//            case R.id.action_favorite:
//                // User chose the "Favorite" action, mark the current item
//                // as a favorite...
//                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Log.d(MONITOR_TAG, "Before_onNavigationItemSelected__ProfileActivity");
        navBurger.navigateTo(item, ProfileActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
