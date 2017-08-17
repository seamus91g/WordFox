package com.example.seamus.wordfox;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.view.ViewGroup;
import android.widget.TextView;

public class DataScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationBurger navBurger = new NavigationBurger();
    private GameData myGameData;
    private int colorIndex = 0;
    public static final String MONITOR_TAG = "myTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myGameData = new GameData(this.getApplicationContext(), 0);     // Only data for player 0

        // Load the view group for each data section
        ViewGroup usernameSection = (ViewGroup) findViewById(R.id.username_section);
        ViewGroup gamesSection = (ViewGroup) findViewById(R.id.games_section);
        ViewGroup wordsSection = (ViewGroup) findViewById(R.id.words_section);
        ViewGroup dataSection = (ViewGroup) findViewById(R.id.data_section);
        // Username section
        createDataTextView("" + myGameData.getUsername(), usernameSection);
        // Games Section
        createDataTextView("Played " + myGameData.getGameCount() + " games", gamesSection);    //
        createDataTextView("Played " + myGameData.getRoundCount() + " rounds", gamesSection);    //
        // Words section
        createDataTextView("Longest word: " + myGameData.findLongest(), wordsSection);    //
        createDataTextView("Average word length: " + myGameData.getAverageWordLength(), wordsSection);    //
        createDataTextView("Rounds where no word found: " + myGameData.getNoneFoundCount(), wordsSection);    //
        updateWordOccurences(wordsSection);
        // Data section
        createDataTextView("Highest score: " + myGameData.getHighestTotalScore(), dataSection);    //
        createDataTextView("Valid words submitted: " + myGameData.getSubmittedCorrectCount(), dataSection);    //
        createDataTextView("Invalid words submitted: " + myGameData.getSubmittedIncorrectCount(), dataSection);    //
        createDataTextView("Total shuffles: " + myGameData.getShuffleCount(), dataSection);    //
        createDataTextView("Average shuffles per round: " + myGameData.getShuffleAverage(), dataSection);    //

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void createDataTextView(String dataText, ViewGroup viewSection) {
        TextView textView = new TextView(this);
        int dataStyle = R.style.dataTextStyle;
        int color;
        // Alternate the background color for every second data item
        if (++colorIndex % 2 == 0) {
            color = R.color.colorTextDataBG;
        } else {
            color = R.color.colorTextDataBGAlt;
        }
        // Load a style and a color for the text view
        if (Build.VERSION.SDK_INT < 23) {
            textView.setTextAppearance(this, dataStyle);
            color = getResources().getColor(color);
        } else {
            textView.setTextAppearance(dataStyle);
            color = ContextCompat.getColor(this, color);
        }
        // Find the width of the screen. Text view width should be equal to this
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
        int parentWidth = dm.widthPixels;
        // Set all desired text view attributes
        textView.setText(dataText);
        textView.setBackgroundColor(color);
        textView.setWidth(parentWidth);
        textView.setPadding(16, 2, 16, 2);

        viewSection.addView(textView);
    }

    public void updateWordOccurences(ViewGroup viewSection) {
        for (int i = 3; i <= 9; i++) {
            int frequencyOccured = myGameData.findOccurence(i);
            String frequencyOccuredStr = Integer.toString(frequencyOccured);
            String textToDisplay = "Found " + frequencyOccuredStr + " words of length " + i;

            createDataTextView(textToDisplay, viewSection);
        }
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
                Intent profileScreenIntent = new Intent(DataScreenActivity.this, ProfileActivity.class);
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

        navBurger.navigateTo(item, DataScreenActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
