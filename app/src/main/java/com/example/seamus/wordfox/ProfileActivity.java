package com.example.seamus.wordfox;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int SELECT_PICTURE = 0;
    private static final String MONITOR_TAG = "myTag";
    private static final int PLAYER_NUMBER = 0;
    private GameData myGameData;
    private NavigationBurger navBurger = new NavigationBurger();
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myGameData = new GameData(this.getApplicationContext(), PLAYER_NUMBER);
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
        if (!username_prof.equals("fox")) {
            et.setText(username_prof);
        }
        // Button to save user name to GameData class.
        Button setProfileNameButton = (Button) findViewById(R.id.button);
        setProfileNameButton.setOnClickListener(usernameButtonListener);

        // Display the profile pic if one exists.
        String profPicStr = myGameData.getProfilePicture();
        if (!profPicStr.equals("")) {
            Uri myFileUri = Uri.parse(profPicStr);
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

    // Allow user to choose image from their phone when the profile image is clicked
    public void choosePicture(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_PICTURE);
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

    private Bitmap getBitmapFromUri(Uri imgUri) {
        Bitmap bitmap = null;
        ContentResolver cr = getContentResolver();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor myCur = cr.query(imgUri, projection, null, null, null);

        Log.d(MONITOR_TAG, "Checking if file exists: " + imgUri.toString() + ", END");
        if (myCur != null) {
            if (myCur.moveToFirst()) {
                String filePath = myCur.getString(0);
                if (new File(filePath).exists()) {
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imgUri);
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
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), rotateMatrix, true);
                    }
                }
            }
        }
        return bitmap;
    }

    // User can type into text field and click 'save' button to save their profile user name
    private View.OnClickListener usernameButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            EditText et = (EditText) findViewById(R.id.username_profile);
            String username_prof = et.getText().toString();
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
