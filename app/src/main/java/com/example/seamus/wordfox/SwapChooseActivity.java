package com.example.seamus.wordfox;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import com.example.seamus.wordfox.game_screen.GameActivity;

public class SwapChooseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PlayerChoiceListener {
    public static final String NEW_PLAYER_TAG = "new_player";
    public static final String EXISTING_PLAYER_TAG = "existing_player";
    private static final String MONITOR_TAG = "myTag";
    private String CURRENT_FRAGMENT = "";
    private TextView currentPlayerTV;
    private PlayerIdentity currentPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swap_choose);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        Button npButton = findViewById(R.id.choice_new_player_button);
        Button epButton = findViewById(R.id.choice_existing_player_button);
        npButton.setOnClickListener(newOrExistingListener);
        epButton.setOnClickListener(newOrExistingListener);

        Button startButton = findViewById(R.id.nextPlayerStart);
        startButton.setOnClickListener(startListener);

        currentPlayerTV = findViewById(R.id.current_playing_as_choice);
    }

    @Override
    public void setChoice(PlayerIdentity choosenPlayer) {
        if(currentPlayerTV == null){
            return;
        }
        currentPlayer = choosenPlayer;
        String currentChoice = "Playing as " + choosenPlayer.username;
        currentPlayerTV.setText(currentChoice);
    }

    private View.OnClickListener newOrExistingListener = view -> decideFragment((String) view.getTag());
    private View.OnClickListener startListener = view -> startGame();

    private void startGame() {
        if(currentPlayer == null){
            return;
        }
        if(!GameData.doesPlayerExist(currentPlayer.ID, this)){
            new GameData(this, currentPlayer.ID, currentPlayer.username);
        }
        int currentIndex = HomeScreen.allGameInstances
                .get(HomeScreen.allGameInstances.size()-1)
                .getThisGameIndex();
        HomeScreen.allGameInstances.add(new GameInstance(
                currentPlayer.ID,
                currentPlayer.username,
                ++currentIndex,
                HomeScreen.allGameInstances.get(0).getRoundIDs()));
        Intent intent = new Intent(this, GameActivity.class);
        intent.putExtra(GameActivity.GAME_INDEX, currentIndex);
        this.startActivity(intent);
    }

    private void addFragment(Fragment fragment) {
        Log.d(MONITOR_TAG, "Creating the fragment: " + CURRENT_FRAGMENT);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.player_select_container, fragment, CURRENT_FRAGMENT)
                .commit();
    }

    private void removeExistingFragment() {
        if (CURRENT_FRAGMENT.equals("")) {
            Log.d(MONITOR_TAG, "No fragment already exists");
            return;
        }
        Log.d(MONITOR_TAG, "Removing old fragment: " + CURRENT_FRAGMENT);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CURRENT_FRAGMENT);
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    private void decideFragment(String choice) {
        Log.d(MONITOR_TAG, "Tag is: " + choice);
        if (CURRENT_FRAGMENT.equals(choice)) {
            Log.d(MONITOR_TAG, "Tag is already selected");
            return;
        }
        removeExistingFragment();
        Log.d(MONITOR_TAG, "Adding new fragment");
        switch (choice) {
            case NEW_PLAYER_TAG:
                CURRENT_FRAGMENT = NEW_PLAYER_TAG;
                addFragment(new NewPlayerFragment());
                break;
            case EXISTING_PLAYER_TAG:
                CURRENT_FRAGMENT = EXISTING_PLAYER_TAG;
                addFragment(new ExistingPlayerFragment());
                break;
            default:
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.swap_choose, menu);
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
