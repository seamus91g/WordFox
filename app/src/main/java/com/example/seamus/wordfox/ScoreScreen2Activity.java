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
import java.util.Collections;

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


        int numberOfGames = MainActivity.allGameInstances.size();
        int numberOfRounds = 3;

        String gameOverScreenRoundResult = "";
        ListView listViewWord = (ListView) findViewById(R.id.gameOverScreenResultWord);
        ArrayList<String> gameResults = new ArrayList<>();

        GameInstance firstGame = MainActivity.allGameInstances.get(0);
        String highestPossible = "Highest Possible score:  " + firstGame.getHighestPossibleScore();
        gameResults.add(highestPossible);       // List of items to print to the ListView

        for (int i = 0; i < numberOfRounds; i++) {
            String longestWordPossible = firstGame.getRoundLongestPossible(i).toUpperCase();

            String gameOverScreenRound = "ROUND " + (i + 1) + " - " + firstGame.getLetters(i) + "          " + longestWordPossible + "  (" + longestWordPossible.length() + ")"; // + letters
//            String gameOverScreenRoundDetails = "Longest possible: " + longestWordPossible + "  (" + longestWordPossible.length() + ")";
            gameResults.add(gameOverScreenRound);
//            gameResults.add(gameOverScreenRoundDetails);
        }

        ArrayList<Integer> playerTotalScores = new ArrayList<>();
        for (int i = 0; i < numberOfGames; i++) {
            GameInstance thisGameInstance = MainActivity.allGameInstances.get(i);
            int totalScore = thisGameInstance.getTotalScore();
            playerTotalScores.add(totalScore);
            String playerString = "PLAYER " + (i + 1) + " TOTAL: " + totalScore;
            gameResults.add(playerString);
            for (int j = 0; j < 1; j++) {

                gameOverScreenRoundResult = "Round 1 word: " + thisGameInstance.getRound1Word() + "    Score: " + thisGameInstance.getRound1Length();
                gameResults.add(gameOverScreenRoundResult);
                gameOverScreenRoundResult = "Round 2 word: " + thisGameInstance.getRound2Word() + "    Score: " + thisGameInstance.getRound2Length();
                gameResults.add(gameOverScreenRoundResult);
                gameOverScreenRoundResult = "Round 3 word: " + thisGameInstance.getRound3Word() + "    Score: " + thisGameInstance.getRound3Length();
                gameResults.add(gameOverScreenRoundResult);

            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, gameResults);
        listViewWord.setAdapter(adapter);

        int maxScore = Collections.max(playerTotalScores);
        TextView scoreScreen2TextView = (TextView) findViewById(R.id.scoreScreen2TV);
        scoreScreen2TextView.setText(String.valueOf(maxScore));

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
