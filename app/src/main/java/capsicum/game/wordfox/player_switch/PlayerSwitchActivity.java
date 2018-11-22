package capsicum.game.wordfox.player_switch;

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

import capsicum.game.wordfox.FoxUtils;
import capsicum.game.wordfox.GameData;
import capsicum.game.wordfox.HomeScreen;
import capsicum.game.wordfox.NavigationBurger;
import capsicum.game.wordfox.R;
import capsicum.game.wordfox.game_screen.GameActivity;
import capsicum.game.wordfox.profile.ProfileActivity;
import timber.log.Timber;

import java.util.ArrayList;

@Deprecated
public class PlayerSwitchActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PlayerSwitchContract.View {
    private int gameIndexNumber;
    private boolean backButtonPressedOnce = false;
    private NavigationBurger navBurger = new NavigationBurger();
    private final String MONITOR_TAG = "myTag";
    private PlayerSwitchPresenter presenter;
    private EditText createNewPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_switch);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // What game number are we on
        gameIndexNumber = getIntent().getExtras().getInt(GameActivity.GAME_INDEX);

        // Button to continue to the next player
        Button setProfileNameButton = (Button) findViewById(R.id.nextPlayerButton);
        setProfileNameButton.setOnClickListener(nextPlayerButtonListener);

        // User can create a new player name
        createNewPlayer = (EditText) findViewById(R.id.create_player);
        Button setProfileName = (Button) findViewById(R.id.pswitch_create_player_button);
        setProfileName.setOnClickListener(usernameButtonListener);

        presenter = new PlayerSwitchPresenter(this, gameIndexNumber, HomeScreen.allGameInstances, GameData.getNamedPlayerList(this));
        presenter.setupMenu();
    }
    // After typing a username, the player can press 'Save' to keep the username
    private View.OnClickListener usernameButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String username_prof = createNewPlayer.getText().toString();
//            presenter.setupMenu();
//            presenter.setChoice(username_prof);
            presenter.newPlayer(username_prof);
            clearEdittextFocus();
        }
    };
    // Press 'Continue' button to proceed to the next player
    private View.OnClickListener nextPlayerButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            presenter.startGame();
        }
    };
    // Clear focus and close the keyboard
    private void clearEdittextFocus(){
        FoxUtils.clearViewFocus(createNewPlayer, this);
    }
    // User must press back button twice consecutively to exit the game
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (this.backButtonPressedOnce) {
                Intent homeScreenIntent = new Intent(this, HomeScreen.class);
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
    // Instruct the user of what to do. (Pass the game to the next player)
    @Override
    public void displayMessage(String message) {
        TextView nextPlayerTextView = (TextView) findViewById(R.id.playerSwitchTV);
        nextPlayerTextView.setText(message);
    }
    // User has a choice of existing player names to choose from.
    @Override
    public void populateMenu(ArrayList<String> items) {
        // Create an adapter to describe how the items are displayed
        Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dropdown.setAdapter(adapter);
        // If user chooses a name, assign the name to the current game instance
        dropdown.setOnItemSelectedListener(itemSelectedListener);
    }
    // When user has choosen a name from the menu, set up the game instance
    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            // The first option is not a name, just a player number
            String choice = (String) parent.getItemAtPosition(position);
            presenter.setChoice(choice);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub
        }
    };

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

        navBurger.navigateTo(item, PlayerSwitchActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
