package com.example.seamus.wordfox;

import android.content.Intent;
import android.os.Bundle;
import android.renderscript.Double2;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class DataScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationBurger navBurger = new NavigationBurger();
    private GameData myGameData;
    public static final String MONITOR_TAG = "myTag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        myGameData = new GameData(this.getApplicationContext(), 0);     // Only data for player 0

        updateTextView("Average word length: " + myGameData.getAverageWordLength(), "averge_word_length");    //
        updateTextView("Valid words submitted: " + myGameData.getSubmittedCorrectCount(), "count_submitted_correct");    //
        updateTextView("Invalid words submitted: " + myGameData.getSubmittedIncorrectCount(), "count_submitted_incorrect");    //
        updateTextView("Rounds where no word found: " + myGameData.getNoneFoundCount(), "count_none_found");    //
        updateTextView("Total shuffles: " + myGameData.getShuffleCount(), "count_shuffles");    //
        updateTextView("Average shuffles per round: " + myGameData.getShuffleAverage(), "average_shuffles");    //
        updateTextView("Highest score: " + myGameData.getHighestTotalScore(), "highest_total_score");    //
        updateTextView("Played " + myGameData.getGameCount() + " games", "gameCount_data_screen");    //
        updateTextView("Played " + myGameData.getRoundCount() + " rounds", "roundCount_data_screen");    //
        updateTextView("" + myGameData.getUsername(), "username_data_screen");    //
        updateTextView("Longest word: " + myGameData.findLongest(), "longestWord_data_screen");    //

        updateWordOccurences();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void updateTextView(String text, String id) {
        int resID = getResources().getIdentifier(id, "id", getPackageName());
        TextView currentCell = (TextView) findViewById(resID);
        currentCell.setText(text);
    }

    public void updateWordOccurences() {
        for (int i = 3; i <= 9; i++) {
            int frequencyOccured = myGameData.findOccurence(i);
            String frequencyOccuredStr = Integer.toString(frequencyOccured);
            String textToDisplay = "Found " + frequencyOccuredStr + " words of length " + i;

            updateTextView(textToDisplay, "word" + i + "_data_screen");
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
