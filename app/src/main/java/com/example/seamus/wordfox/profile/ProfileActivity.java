package com.example.seamus.wordfox.profile;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
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
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seamus.wordfox.FoxUtils;
import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.NavigationBurger;
import com.example.seamus.wordfox.R;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity
        implements ActivityCompat.OnRequestPermissionsResultCallback,
        NavigationView.OnNavigationItemSelectedListener,
        ProfileContract.View {
    static final int SELECT_PICTURE = 0;
    private static final String TAG = "profile_tag";
    private NavigationBurger navBurger = new NavigationBurger();
    private ProfilePresenter presenter;
    private ImageView profileIB;
    private EditText nameEditText;
    private Button setProfileNameButton;

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
        // Keep soft keyboard hidden when activity opens
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // User can click the profile picture to allow them to change the picture
        profileIB = findViewById(R.id.profileImageButton);
        profileIB.setOnClickListener(profileImageListener);
        // User can type in a new user name
        nameEditText = findViewById(R.id.profile_username);
        nameEditText.setOnFocusChangeListener(edittextFocusChange);
        // Button to save user name to GameData class.
        setProfileNameButton = findViewById(R.id.profile_save_name);
        setProfileNameButton.setOnClickListener(usernameButtonListener);

        // The presenter handles preparing relevant data to display
        presenter = new ProfilePresenter(this, new GameData(this, GameData.DEFAULT_P1_NAME));
        presenter.displayLongestWord();
        presenter.displayProfileName();
        presenter.displayProfileImage();
        presenter.displayBestWords();
        presenter.displayRecentGame();
    }
    // Keep the 'Save Username' button in view when the soft keyboard appears
    private View.OnFocusChangeListener edittextFocusChange = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                // Get screen dimensions
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                // Scroll so that the Save button is 1/4 way from the top of the screen
                int scrollyY = setProfileNameButton.getBottom() - displayMetrics.heightPixels / 4;
                ScrollView scroll = findViewById(R.id.profile_scrollview);
                findViewById(R.id.profile_scrollview).post(
                        () -> scroll.smoothScrollTo(0, scrollyY)
                );
            }
        }
    };
    // User can click the existing profile image to load a different image
    private View.OnClickListener profileImageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presenter.choosePicture();
        }
    };
    // User can type into text field and click 'save' button to save their profile user name
    private View.OnClickListener usernameButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username_prof = nameEditText.getText().toString();
            presenter.updateProfileName(username_prof);
            clearViewFocus(nameEditText);
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // if permission has been granted resume tasks needing this permission
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            presenter.permissionGrantedDisplayImage();
        } else {
            // permission not granted so can't do anything with the image
            Toast.makeText(this, "Default profile icon will remain", Toast.LENGTH_SHORT).show();
        }
    }

    // When the user chooses an image, try to load it. Save their choice to GameData
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PICTURE && resultCode == Activity.RESULT_OK) {
            presenter.activityResult(data);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
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
                Intent profileScreenIntent = new Intent(ProfileActivity.this, ProfileActivity.class);
                startActivity(profileScreenIntent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navBurger.navigateTo(item, ProfileActivity.this);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void setAdjustViewBounds(Boolean bool) {
        profileIB.setAdjustViewBounds(bool);
    }

    @Override
    public void setProfileImage(Bitmap bitmap) {
        profileIB.setImageBitmap(bitmap);
    }

    @Override
    public void setLongestWord(String longestWord) {
        // Display longest word
        TextView longestWordProfilePage = findViewById(R.id.profPicLongestWord);
        longestWordProfilePage.setText(longestWord);
    }

    @Override
    public void setUsername(String name) {
        nameEditText.setText(name);
    }

    public void clearViewFocus(View viewWithFocus) {
        FoxUtils.clearViewFocus(viewWithFocus, this);
    }

    @Override
    public void setBestWords(ArrayList<String> words) {
        for (String message : words) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setText(message);

            LinearLayout linearLayout = findViewById(R.id.recent_game);
            linearLayout.addView(textView);
        }
    }

    @Override
    public void setDataPreviousGame(ArrayList<String> info) {
        for (String message : info) {
            TextView textView = new TextView(this);
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            textView.setText(message);

            LinearLayout linearLayout = findViewById(R.id.recent_game);
            linearLayout.addView(textView);
        }
    }
}
