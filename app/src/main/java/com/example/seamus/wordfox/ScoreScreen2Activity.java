package com.example.seamus.wordfox;

import android.content.Intent;
import android.os.Bundle;
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
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ScoreScreen2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String MONITOR_TAG = "myTag";
    private NavigationBurger navBurger = new NavigationBurger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_screen2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TextView scoreScreen2TextView = (TextView) findViewById(R.id.scoreScreen2TV);
        scoreScreen2TextView.setText(String.valueOf(MainActivity.allGameInstances.get(0).getTotalScore()));

        int numberOfGames = MainActivity.allGameInstances.size();
        int numberOfRounds = 3;

        String gameOverScreenRoundResult = "";
        ListView listViewWord = (ListView) findViewById(R.id.gameOverScreenResultWord);
        ArrayList<String> gameResults = new ArrayList<>();

        String gameOverScreenRound = "";
        String gameOverScreenRoundDetails = "";

        for (int i = 0; i < numberOfRounds; i++) {
            gameOverScreenRound = "ROUND " + (i + 1) + " - AMTBESILW"; // + letters
            gameOverScreenRoundDetails = "Longest possible: " + MainActivity.allGameInstances.get(0).getLongestPossible().toUpperCase() + "  (" + MainActivity.allGameInstances.get(0).getLongestPossible().length() + ")";
            gameResults.add(gameOverScreenRound);
            gameResults.add(gameOverScreenRoundDetails);
        }

        for (int i = 0; i < numberOfGames; i++) {
            String playerString = "PLAYER " + (i + 1) + " TOTAL: " + MainActivity.allGameInstances.get(i).getTotalScore();
            gameResults.add(playerString);
            for (int j = 0; j < 1; j++) {

                gameOverScreenRoundResult = "Round 1 word: " + MainActivity.allGameInstances.get(i).getRound1Word() + "    Score: " + MainActivity.allGameInstances.get(i).getRound1Length();
                gameResults.add(gameOverScreenRoundResult);
                gameOverScreenRoundResult = "Round 2 word: " + MainActivity.allGameInstances.get(i).getRound2Word() + "    Score: " + MainActivity.allGameInstances.get(i).getRound2Length();
                gameResults.add(gameOverScreenRoundResult);
                gameOverScreenRoundResult = "Round 3 word: " + MainActivity.allGameInstances.get(i).getRound3Word() + "    Score: " + MainActivity.allGameInstances.get(i).getRound3Length();
                gameResults.add(gameOverScreenRoundResult);

            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, gameResults);
        listViewWord.setAdapter(adapter);

    }

    // When button pressed, reboot to main.
    public void startMainAct(View v) {
        Log.d("scoreScreen2Activity", "reboot to home screen");
        Intent MainIntent = new Intent(this, MainActivity.class);
        startActivity(MainIntent);
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
                Intent profileScreenIntent = new Intent(ScoreScreen2Activity.this, ProfileActivity.class);
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

        Log.d(MONITOR_TAG, "Before_onNavigationItemSelected__MainActivity");
        navBurger.navigateTo(item, ScoreScreen2Activity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
