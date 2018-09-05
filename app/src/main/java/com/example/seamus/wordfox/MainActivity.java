package com.example.seamus.wordfox;

import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.Toast;

import com.example.seamus.wordfox.data.FoxDictionary;
import com.example.seamus.wordfox.game_screen.GameActivity;
import com.example.seamus.wordfox.profile.ProfileActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String MONITOR_TAG = "myTag";
    private boolean backButtonPressedOnce = false;
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        numberOfPlayers = 1;
        loadDictionary();
    }

    public void loadDictionary(){
        if(FoxDictionary.isWordListLoaded){
            return;
        }
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                FoxDictionary.loadWords("validWords_alph.txt", "letterFrequency.txt", getAssets());
            }
        });
        thread.start();
    }

    public void setNumPlayers(View myView) {
        String Players = (String) myView.getTag();

        switch (Players) {
            case "1players":
                numberOfPlayers = 1;
                changeTextTV(R.id.speechTV);
                break;
            case "2players":
                numberOfPlayers = 2;
                changeTextTV(R.id.speechTV);
                break;
            case "3players":
                numberOfPlayers = 3;
                changeTextTV(R.id.speechTV);
                break;
            case "4players":
                numberOfPlayers = 4;
                changeTextTV(R.id.speechTV);
                break;
            case "5players":
                numberOfPlayers = 5;
                changeTextTV(R.id.speechTV);
                break;
            case "6players":
                numberOfPlayers = 6;
                changeTextTV(R.id.speechTV);
                break;
            default:
        }

        Log.d(MONITOR_TAG, "Number of players: " + numberOfPlayers + ", END");
        allGameInstances.clear();
    }

    private void changeTextTV(int speechTV) {
        TextView myTV = (TextView) findViewById(speechTV);
        String numPlayersChosen = "Press START to begin your " + numberOfPlayers + " player game!!";
        myTV.setText(numPlayersChosen);
    }

    public void startGameAct(View v) {
//        GameInstance myInstance = new GameInstance();
//        myInstance.startGame(this);
//        NumberPicker np = (NumberPicker) findViewById(R.id.numberPicker);
//        numberOfPlayers = np.getValue();
//        Log.d(MONITOR_TAG, "Number of players: " + numberOfPlayers + ", END");
        allGameInstances.clear();
//        for (int i=0; i<maxNumberOfRounds; i++){
//            this.roundIDs.add(UUID.randomUUID().toString());
//        }

        String p1ID;
        {
            GameData fox = new GameData(this, GameData.DEFAULT_P1_NAME);
            p1ID = fox.getUsername();
        }
        GameInstance thisGame = new GameInstance(p1ID, GameData.DEFAULT_P1_NAME, 0);
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
        while (!FoxDictionary.isWordListLoaded){
            Log.d(MONITOR_TAG, "Dictionary word list is not finished loading!");
            try {
                Thread.sleep(100);      // Wait for dictionary to finish loading
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.startActivity(gameIntent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (this.backButtonPressedOnce) {
                this.finishAffinity();
                return;
            }
            Toast.makeText(this, "Double tap BACK to exit!", Toast.LENGTH_SHORT).show();
            this.backButtonPressedOnce = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backButtonPressedOnce = false;
                }
            }, 1500);

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
