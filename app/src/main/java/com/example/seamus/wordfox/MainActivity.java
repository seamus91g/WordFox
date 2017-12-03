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
import android.widget.NumberPicker;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String MONITOR_TAG = "myTag";
    private NavigationBurger navBurger = new NavigationBurger();
    public static ArrayList<GameInstance> allGameInstances = new ArrayList<GameInstance>();
    private int numberOfPlayers;
    private final static int maxPlayerCount = 6;

    public static int getMaxPlayerCount() {
        return maxPlayerCount;
    }
//    public GameData myGameData = new GameData(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d(MONITOR_TAG, "Main activity, END");

        NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);
        np.setMinValue(1);
        np.setMaxValue(maxPlayerCount);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Total score is accumulated across game rounds. Returning to the main menu will clear it
//        GameActivity.myGameInstance.clearAllScores();
        numberOfPlayers = 1;
        Log.d(MONITOR_TAG, "Number of game instances: " + allGameInstances.size() + ", END");
    }


    public void startGameAct(View v) {
//        GameInstance myInstance = new GameInstance();
//        myInstance.startGame(this);
        NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);
        numberOfPlayers = np.getValue();
        Log.d(MONITOR_TAG, "Number of players: " + numberOfPlayers + ", END");
        allGameInstances.clear();
//        for (int i=0; i<maxNumberOfRounds; i++){
//            this.roundIDs.add(UUID.randomUUID().toString());
//        }

        GameInstance thisGame = new GameInstance(0);
        allGameInstances.add(thisGame);
        for (int i = 1; i < numberOfPlayers; i++) {
            thisGame = new GameInstance(i, thisGame.getRoundIDs());
            allGameInstances.add(thisGame);
        }

        int indexOfGameInstance = 0;
        allGameInstances.get(indexOfGameInstance).clearAllScores(); // Is this necessary??  :S
        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra("game_index", indexOfGameInstance);
//            Log.d(MONITOR_TAG, "In startGame 2");
        this.startActivity(gameIntent);
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
                Intent profileScreenIntent = new Intent(MainActivity.this, ProfileActivity.class);
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
        navBurger.navigateTo(item, MainActivity.this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
