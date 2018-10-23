package com.example.seamus.wordfox;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.TextView;

import com.example.seamus.wordfox.data.FoxDictionary;
import com.example.seamus.wordfox.game_screen.GameActivity;

import java.util.ArrayList;


public class PassAndPlay extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String MONITOR_TAG = "myTag";
    private int numberOfPlayers = -1;
//    public static ArrayList<GameInstance> allGameInstances = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_and_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setup();
    }

    private void setup() {
        Button startButton = findViewById(R.id.bStartPAP);
        startButton.setOnClickListener(startListener);
    }
    View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startGame();
        }
    };

    public void startGame() {
        if (numberOfPlayers < 2 || numberOfPlayers > 6){
            Log.d(MONITOR_TAG, "Returning. Number of players is: " + numberOfPlayers);
            return;
        }
        HomeScreen.allGameInstances.clear();

        PlayerIdentity playerOne = GameData.getPlayer1Identity(this);
        GameInstance playerOneGame = new GameInstance(playerOne.ID, playerOne.username, 0, numberOfPlayers);
        HomeScreen.allGameInstances.add(playerOneGame);

//        ArrayList<PlayerIdentity> players = GameData.fetchSomeIdentities(numberOfPlayers - 1, this);    // TODO: Include p1 in this, seems pointless loading p1 separately
//        for (int i = 0; i < players.size(); i++) {
//            GameInstance thisGame = new GameInstance(players.get(i).ID, players.get(i).username, i + 1);
//            HomeScreen.allGameInstances.add(thisGame);
//        }

        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra(GameActivity.GAME_INDEX, 0);

        // Wait for dictionary to finish loading
        while (!FoxDictionary.isWordListLoaded) {
            Log.d(MONITOR_TAG, "Dictionary word list is not finished loading!");
            try {
                Thread.sleep(100);      // Wait for dictionary to finish loading
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(MONITOR_TAG, "Number of players is: " + numberOfPlayers + ". Game instances: " + HomeScreen.allGameInstances.size());
        this.startActivity(gameIntent);
    }

    public void setNumPlayers(View myView) {
        String Players = (String) myView.getTag();

        switch (Players) {
            case "2players":
                numberOfPlayers = 2;
                changeTextTV();
                break;
            case "3players":
                numberOfPlayers = 3;
                changeTextTV();
                break;
            case "4players":
                numberOfPlayers = 4;
                changeTextTV();
                break;
            case "5players":
                numberOfPlayers = 5;
                changeTextTV();
                break;
            case "6players":
                numberOfPlayers = 6;
                changeTextTV();
                break;
            default:
                numberOfPlayers = -1;
                changeTextTV();
                break;
        }

        Log.d(MONITOR_TAG, "Number of players: " + numberOfPlayers + ", END");
        HomeScreen.allGameInstances.clear();
    }

    private void changeTextTV() {   // TODO: Implement speech
//        TextView myTV = (TextView) findViewById(R.id.speechTV);
//        String numPlayersChosen;
//        if (numberOfPlayers == -1) {
//            numPlayersChosen = "Choose number of players!";
//        } else {
//            numPlayersChosen = "Press START to begin your " + numberOfPlayers + " player game!!";
//        }
//        myTV.setText(numPlayersChosen);
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
        getMenuInflater().inflate(R.menu.pass_and_play, menu);
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
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
