package com.example.seamus.wordfox;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity
    implements ActivityCompat.OnRequestPermissionsResultCallback, NavigationView.OnNavigationItemSelectedListener {
    private static final int SELECT_PICTURE = 0;
    private static final String MONITOR_TAG = "myTag";
    private GameData myGameData;
    private NavigationBurger navBurger = new NavigationBurger();
    private Menu menu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        final String PLAYER = "Player 1";
        myGameData = new GameData(this.getApplicationContext(), GameData.DEFAULT_P1_NAME);
        updateLongestWord();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

            // Display the profile pic if one exists.
            String profPicStr = myGameData.getProfilePicture();

            if (!profPicStr.equals("")) {
                Uri myFileUri = Uri.parse(profPicStr);

                if (isStoragePermissionGranted()){
                    Bitmap bitmap = getBitmapFromUri(myFileUri);
                    ImageView profileIB = (ImageView) findViewById(R.id.profileImageButton);

                    // Check it exists. Could be null if user has deleted the image from gallery
                    if (bitmap != null) {
                        profileIB.setImageBitmap(bitmap);
                    } else {
                        Log.d(MONITOR_TAG, "Setting default profile image, END");
                        profileIB.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_action_name));
                    }
                }

            }
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

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v(MONITOR_TAG,"Permission is granted");
                return true;
            } else {
                Log.v(MONITOR_TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v(MONITOR_TAG,"Permission is granted");
            return true;
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

    private Bitmap getBitmapFromUri(Uri imgUri) {
        Bitmap myBitmap = null;
        ContentResolver cr = getContentResolver();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor myCur = cr.query(imgUri, projection, null, null, null);

        Log.d(MONITOR_TAG, "Checking if file exists: " + imgUri.toString() + ", END");
        if (myCur != null) {
            if (myCur.moveToFirst()) {
                String filePath = myCur.getString(0);

                if (new File(filePath).exists()) {
                    try {
                        myBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);

                        if(myBitmap.getHeight()>=2048||myBitmap.getWidth()>=2048){
                            Log.d(MONITOR_TAG, "Image is too large: width is " + myBitmap.getWidth() + " height is " + myBitmap.getHeight() + " END");
                            DisplayMetrics metrics = new DisplayMetrics();
                            getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            int width = metrics.widthPixels;
                            int height = metrics.heightPixels;
                            Log.d(MONITOR_TAG, "screen width is: " + width + " screen height is: " + height + "resizing image now END");
                            myBitmap = resize(myBitmap,width, height);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // Rotate image if necessary
                    Matrix rotateMatrix = new Matrix();
                    String[] orientationColumn = {MediaStore.Images.Media.ORIENTATION};
//                    Cursor cur = managedQuery(imgUri, orientationColumn, null, null, null);
                    Cursor cur = getContentResolver().query(imgUri, orientationColumn, null, null, null);
                    int orientation = -1;
                    if (cur != null && cur.moveToFirst()) {
                        orientation = cur.getInt(cur.getColumnIndex(orientationColumn[0]));
                    }
                    rotateMatrix.postRotate(orientation);
                    if (!rotateMatrix.isIdentity()) {
                        Log.d(MONITOR_TAG, "Image needs rotation: " + orientation);
                        myBitmap = Bitmap.createBitmap(myBitmap, 0, 0, myBitmap.getWidth(), myBitmap.getHeight(), rotateMatrix, true);
                    }
                }
            }
        }
        return myBitmap;
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
