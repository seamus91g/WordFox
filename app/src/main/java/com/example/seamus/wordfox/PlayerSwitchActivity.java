package com.example.seamus.wordfox;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seamus.wordfox.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlayerSwitchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private int gameIndexNumber;
    private boolean backButtonPressedOnce = false;
    private NavigationBurger navBurger = new NavigationBurger();
    private final String MONITOR_TAG = "myTag";
    EditText et;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(MONITOR_TAG, "OnCreate player switch!");
        setContentView(R.layout.activity_player_switch);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // What game number are we on
        gameIndexNumber = getIntent().getExtras().getInt("game_index");

        String nextPlayerMessage = "Pass the game to player " + (gameIndexNumber + 1);
        TextView nextPlayerTextView = (TextView) findViewById(R.id.playerSwitchTV);
        nextPlayerTextView.setText(nextPlayerMessage);

        Button setProfileNameButton = (Button) findViewById(R.id.nextPlayerButton);
        setProfileNameButton.setOnClickListener(nextPlayerButtonListener);

        //get the spinner from the xml.
        Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        //create a list of items for the spinner.
        ArrayList<String> items = new ArrayList<String>();
        items.add("Player " + (gameIndexNumber + 1));
        // track already chosen players so they can't be chosen again
        ArrayList<String> previousPlayers = new ArrayList<>();
        for (int i=0; i<gameIndexNumber; i++){
            previousPlayers.add(MainActivity.allGameInstances.get(i).getPlayerID());
        }
        // Show the user a list of available player names excluding already chosen ones
        Pattern p = Pattern.compile("Player\\s\\d");    // TODO     .. really??

        for(String playerID : GameData.getNamedPlayerList(this)){
            if(previousPlayers.contains(playerID)){
                continue;
            }
            Matcher m = p.matcher(playerID);
            if(m.matches()){
                Log.d(MONITOR_TAG, "Matched: " + playerID);
            }
            Log.d(MONITOR_TAG, "Didn't match: " + playerID);
            items.add(playerID);
        }
        // Create an adapter to describe how the items are displayed
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        // Set the spinners adapter to the previously created one.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        // If user chooses a name, assign the name to the current game instance
        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // The first option is not a name, just a player number
                String nextPlayerMessage;

                String choice = (String) parent.getItemAtPosition(position);
//              MainActivity.allGameInstances.get(gameIndexNumber).setPlayerID(choice);
                MainActivity.allGameInstances.set(gameIndexNumber, new GameInstance(choice, gameIndexNumber, MainActivity.allGameInstances.get(0).getRoundIDs()));
                nextPlayerMessage = "Pass the game to " + choice;

                TextView nextPlayerTextView = (TextView) findViewById(R.id.playerSwitchTV);
                nextPlayerTextView.setText(nextPlayerMessage);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // TODO Auto-generated method stub
            }
        });
        // User can type in a new name for their player if it doesn't exist
        et = (EditText) findViewById(R.id.create_player);
        Button setProfileName = (Button) findViewById(R.id.pswitch_create_player_button);
        setProfileName.setOnClickListener(usernameButtonListener);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private View.OnClickListener usernameButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username_prof = et.getText().toString();
//            new GameData(v.getContext(), username_prof);    // TODO Invoke static method to add player instead
            // Assign a username instead of just a player number
//            MainActivity.allGameInstances.get(gameIndexNumber).setPlayerID(username_prof);
            MainActivity.allGameInstances.set(gameIndexNumber, new GameInstance(username_prof, gameIndexNumber, MainActivity.allGameInstances.get(0).getRoundIDs()));
            String nextPlayerMessage = "Pass the game to " + username_prof;
            TextView nextPlayerTextView = (TextView) findViewById(R.id.playerSwitchTV);
            nextPlayerTextView.setText(nextPlayerMessage);
            clearEdittextFocus();
            Log.d(MONITOR_TAG, "User entered: " + username_prof + ", END");
        }
    };
    private void clearEdittextFocus(){
        FoxUtils.clearViewFocus(et, this);
    }

    // User can type into text field and click 'save' button to save their profile user name
    private View.OnClickListener nextPlayerButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startGame();
        }
    };

    private void startGame() {
        Log.d(MONITOR_TAG, "starting game instance from player switch: " + this.gameIndexNumber);
        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra("game_index", gameIndexNumber);
        this.startActivity(gameIntent);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (this.backButtonPressedOnce) {
                Intent homeScreenIntent = new Intent(this, MainActivity.class);
                startActivity(homeScreenIntent);
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

    @Override
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
                Intent profileScreenIntent = new Intent(PlayerSwitchActivity.this, ProfileActivity.class);
                startActivity(profileScreenIntent);
                return true;

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
        navBurger.navigateTo(item, PlayerSwitchActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
