package com.example.seamus.wordfox;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Profile extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final int SELECT_PICTURE = 0;
    private static final String MONITOR_TAG = "myTag";
    private GameData myGameData;
    private NavigationBurger navBurger = new NavigationBurger();
    private ImageView myImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myGameData = new GameData(this.getApplicationContext());
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
        // Click profile image for option to choose a different one.
        myImageView = (ImageView) findViewById(R.id.profPicture);
        myImageView.setOnClickListener(profileImageListener);

        // Set profile pic if one exists. TODO improve checking to see if exists. Will crash app!!
        String profPicStr = myGameData.getProfilePicture();
        if (!profPicStr.equals("")) {   // Not enough. Will still crash if user deletes the image
            try {
                Uri myFileUri = Uri.parse(profPicStr);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), myFileUri);
                myImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.d(MONITOR_TAG, "Failed to load profile image, END");
                e.printStackTrace();
            }
        }
    }
    // Detect a click on the profile image. If true, allow user to choose image from their phone
    private View.OnClickListener profileImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d(MONITOR_TAG, "Choosing image, END");
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            intent.setType("image/*");
            startActivityForResult(intent, SELECT_PICTURE);
        }
    };
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

    @Override
    // When the user choose an image, try to load it. Save their choice to GameData
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            Log.d(MONITOR_TAG, "Looking for the image, END");
            Uri selectedImage = data.getData();     // Path to the image
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                Log.d(MONITOR_TAG, "Found the image!, END");
                myImageView.setImageBitmap(bitmap);             // Display on the screen
                myGameData.setProfilePicture(selectedImage);    // Save for future loading
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Log.d(MONITOR_TAG, "Before_onNavigationItemSelected__ProfileActivity");
        navBurger.navigateTo(item, Profile.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
