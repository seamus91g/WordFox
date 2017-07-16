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
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ScoreScreen1Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String MONITOR_TAG = "myTag";
    private NavigationBurger navBurger = new NavigationBurger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GameData myGameData = new GameData(this.getApplicationContext());
        super.onCreate(savedInstanceState);
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

        int score = GameActivity.myGameInstance.getScore();
        int totalScore = GameActivity.myGameInstance.getTotalScore();
        String longestAttempt = GameActivity.myGameInstance.getLongestWord();
        String userName = myGameData.getUsername();

        TextView scoreScreenGreetingTextView = (TextView) findViewById(R.id.scoreScreenGreetingTV);
        scoreScreenGreetingTextView.setText("Congratulations " + userName + "! You scored ");

        TextView scoreScoreScreenTextView = (TextView) findViewById(R.id.scoreScoreScreenTV);
        scoreScoreScreenTextView.setText(String.valueOf(score));

        TextView totalScoreScoreScreenTextView = (TextView) findViewById(R.id.totalScoreScoreScreenTV);
        totalScoreScoreScreenTextView.setText(String.valueOf(totalScore));

        TextView longestWordScoreScreenTextView = (TextView) findViewById(R.id.longestWordScoreScreenTV);
        longestWordScoreScreenTextView.setText(String.valueOf(longestAttempt));
    }

    public void proceed(View v) {
        Log.d(MONITOR_TAG, "In proceed");
        GameActivity.myGameInstance.startGame(this);
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

        Log.d(MONITOR_TAG, "Before_onNavigationItemSelected__MainActivity");
        navBurger.navigateTo(item, ScoreScreen1Activity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
