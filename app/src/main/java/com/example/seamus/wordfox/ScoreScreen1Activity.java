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
import android.widget.TextView;

public class ScoreScreen1Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String MONITOR_TAG = "myTag";
    private NavigationBurger navBurger = new NavigationBurger();
    private int gameIndexNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        gameIndexNumber = getIntent().getExtras().getInt("game_index");
        GameData myGameData = new GameData(this.getApplicationContext(), gameIndexNumber);
        super.onCreate(savedInstanceState);

        int round = MainActivity.allGameInstances.get(gameIndexNumber).getRound();
        this.setTitle("Round " + (round+1) + " Score");

        setContentView(R.layout.activity_score_screen1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Log.d(MONITOR_TAG, "just want to see if this makes it");

        int score = MainActivity.allGameInstances.get(gameIndexNumber).getScore();
        int totalScore = MainActivity.allGameInstances.get(gameIndexNumber).getTotalScore();
        String longestAttempt = MainActivity.allGameInstances.get(gameIndexNumber).getLongestWord();
        String longestPossible = MainActivity.allGameInstances.get(gameIndexNumber).getLongestPossible().toUpperCase();
        String userName = myGameData.getUsername();

        TextView scoreScreenGreetingTextView = (TextView) findViewById(R.id.scoreScreenGreetingTV);
        scoreScreenGreetingTextView.setText("Congratulations " + userName + "! You scored ");

        TextView scoreScoreScreenTextView = (TextView) findViewById(R.id.scoreScoreScreenTV);
        scoreScoreScreenTextView.setText(String.valueOf(score));

        TextView totalScoreScoreScreenTextView = (TextView) findViewById(R.id.totalScoreScoreScreenTV);
        totalScoreScoreScreenTextView.setText(String.valueOf(totalScore));

        TextView longestWordScoreScreenTextView = (TextView) findViewById(R.id.longestWordScoreScreenTV);
        longestWordScoreScreenTextView.setText(String.valueOf(longestAttempt));

        String longestWordPossibleWithLength = longestPossible + " (" + longestPossible.length() + ")";
        TextView longestWordPossibleTextView = (TextView) findViewById(R.id.longestWordPossibleTV);
        longestWordPossibleTextView.setText(longestWordPossibleWithLength);


        if (gameIndexNumber == 0 && round == 2){
            myGameData.setHighestScore(totalScore);
            Log.d(MONITOR_TAG, "Setting high score: " + totalScore);
        }
    }

    public void proceed(View v) {
        Log.d(MONITOR_TAG, "In proceed");
        MainActivity.allGameInstances.get(gameIndexNumber).startGame(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            Intent mainIntent = new Intent(this, MainActivity.class);
            startActivity(mainIntent);
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
                Intent profileScreenIntent = new Intent(ScoreScreen1Activity.this, ProfileActivity.class);
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
        navBurger.navigateTo(item, ScoreScreen1Activity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
