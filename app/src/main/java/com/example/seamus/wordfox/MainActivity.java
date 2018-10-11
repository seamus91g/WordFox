package com.example.seamus.wordfox;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.NavigationView;
import android.support.transition.ChangeBounds;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
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
import android.widget.Button;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seamus.wordfox.data.FoxDictionary;
import com.example.seamus.wordfox.game_screen.GameActivity;
import com.example.seamus.wordfox.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String MONITOR_TAG = "myTag";
    private boolean backButtonPressedOnce = false;
    private NavigationBurger navBurger = new NavigationBurger();
    public static ArrayList<GameInstance> allGameInstances = new ArrayList<>();
    private int numberOfPlayers;
    private final static int maxPlayerCount = 6;

    private ConstraintLayout constraint;
    private ConstraintSet constraintSet = new ConstraintSet();

    public static int getMaxPlayerCount() {
        return maxPlayerCount;
    }
//    public GameData myGameData = new GameData(this);

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d(MONITOR_TAG, "Main activity, END");

        View parentView = findViewById(R.id.contentMainxml);
        parentView.post(() -> startAnimation());
        
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        numberOfPlayers = 1;
        loadDictionary();

        if (Build.VERSION.SDK_INT < 23) {
            int buttonHeight = getResources().getDimensionPixelSize(R.dimen.standard_button_height);
            Drawable drawable = getResources().getDrawable(R.drawable.play_pic_white_icon);
            drawable.setBounds(0, 0, (int) (buttonHeight), (int) (buttonHeight));
            ScaleDrawable sd = new ScaleDrawable(drawable, 0, 1, 1);    // TODO: .. second two parameters don't seem to change anything
            Button btn = findViewById(R.id.bStartGame);
            btn.setCompoundDrawables(sd.getDrawable(), null, null, null);
        }
        new Thread(() -> {
            if(isServiceRunning(WifiService.class)){
                stopService(new Intent(MainActivity.this, WifiService.class));
            }
        }).start();
    }

    public void loadDictionary() {
        if (FoxDictionary.isWordListLoaded) {
            return;
        }
        Thread thread = new Thread(() -> FoxDictionary.loadWords("validWords_alph.txt", "letterFrequency.txt", getAssets()));
        thread.start();
    }

    public void startWifi(View v) {
        Intent wifiIntent = new Intent(this, LocalWifiActivity.class);
        startActivity(wifiIntent);
    }

    private void startAnimation() {
        constraint = findViewById(R.id.contentMainxml);
        constraintSet.clone(MainActivity.this, R.layout.content_main_detail);

        Transition transition = new ChangeBounds();
        transition.setInterpolator(new AccelerateDecelerateInterpolator());
        transition.setDuration(1000);

        TransitionManager.beginDelayedTransition(constraint,transition);
        constraintSet.applyTo(constraint);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                ImageView talkingFoxIV = findViewById(R.id.talkingFoxIV);
                talkingFoxIV.setImageResource(R.drawable.foxwithspeech);

                TextView speechTV = findViewById(R.id.speechTV);
                speechTV.setText("Choose your number of players!");

            }
        }, 1300);

    }

    public void setNumPlayers(View myView){
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
        allGameInstances.clear();

        PlayerIdentity playerOne = GameData.getPlayer1Identity(this);
        GameInstance playerOneGame = new GameInstance(playerOne.ID, playerOne.username, 0);
        allGameInstances.add(playerOneGame);

        ArrayList<PlayerIdentity> players = GameData.fetchSomeIdentities(numberOfPlayers - 1, this);    // TODO: Include p1 in this, seems pointless loading p1 separately
        for (int i = 0; i < players.size(); i++) {
            GameInstance thisGame = new GameInstance(players.get(i).ID, players.get(i).username, i + 1);
            allGameInstances.add(thisGame);
        }

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
        this.startActivity(gameIntent);
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
