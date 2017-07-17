package com.example.seamus.wordfox;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
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

        myGameData = new GameData(this.getApplicationContext());

        updateLongestWord();
        updateUsername();
        updateGameCount();
        updateWordOccurences();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    public void updateWordOccurences() {
        Log.d(MONITOR_TAG, "Starting, END");
        for (int i=3; i<=9; i++){
            int frequencyOccured = myGameData.findOccurence(i);
            Log.d(MONITOR_TAG, "Starting 2, END");
            String frequencyOccuredStr = Integer.toString(frequencyOccured);
            Log.d(MONITOR_TAG, "Starting 3, END");
            String textToDisplay = "Found " + frequencyOccuredStr + " words of length " + i;
            Log.d(MONITOR_TAG, "Starting 4, END");

            String textViewId = "word" + i + "_data_screen";
            Log.d(MONITOR_TAG, "Starting 5, END");
            int resID = getResources().getIdentifier(textViewId, "id", getPackageName());

            TextView currentWord = (TextView) findViewById(resID);
            currentWord.setText(textToDisplay);

        }

    }


    public void updateLongestWord() {
        String longestWord = myGameData.findLongest();
        TextView longestWordProfilePage = (TextView) findViewById(R.id.longestWord_data_screen);
        String longWordSentence = "Longest Word: " + longestWord;
        longestWordProfilePage.setText(longWordSentence);
        Log.d(MONITOR_TAG, "Printing longest word: " + longestWord + ", END");
    }

    public void updateUsername() {
        String user = myGameData.getUsername();
        TextView longestWordProfilePage = (TextView) findViewById(R.id.username_data_screen);
        longestWordProfilePage.setText(user);

    }

    public void updateGameCount() {
        int gameCount = myGameData.getGameCount();
        String gameCountStr = Integer.toString(gameCount);
        String textViewString = "Played " + gameCountStr + " games";
        TextView longestWordProfilePage = (TextView) findViewById(R.id.gameCount_data_screen);
        longestWordProfilePage.setText(textViewString);

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
//        getMenuInflater().inflate(R.menu.data_screen, menu);
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

        navBurger.navigateTo(item, DataScreenActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
