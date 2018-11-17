package capsicum.game.wordfox;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import capsicum.game.wordfox.game_screen.GameActivity;
import capsicum.game.wordfox.profile.ProfileActivity;
import timber.log.Timber;

import java.util.ArrayList;
import java.util.ListIterator;

public class SwapChooseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        PlayerChoiceListener.FragmentView {
    public static final String NEW_PLAYER_TAG = "new_player";

    private PlayersAdapter playersAdapter;
    public static final String EXISTING_PLAYER_TAG = "existing_player";
    private static final String MONITOR_TAG = "myTag";
    private String CURRENT_FRAGMENT = "";
    private TextView currentPlayerTV;
    private PlayerIdentity currentPlayer;
    private boolean isAdapterLoaded = false;
    private NavigationBurger navBurger = new NavigationBurger();
    private int screenWidth;
    private int screenHeight;
    private int profPicWidth;
    private int buttonGridImageWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            startActivity(new Intent(this, HomeScreen.class));
            return;
        }
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



        calculateScreenDimensions();
        profPicWidth = (screenWidth/10);
        buttonGridImageWidth = (screenWidth/4);

        setup();

        LinearLayout foxContainer = findViewById(R.id.existing_player_cardview_images);
        foxContainer.post(() -> displayCardviewFoxes(foxContainer));
    }

    private void displayCardviewFoxes(LinearLayout foxContainer) {
        Display display = getWindowManager().getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        Bitmap newPlayerOutlineBMP = ImageHandler.getScaledBitmapByWidth(R.drawable.grayfox_outline_bggamecolor, metrics.widthPixels / 5, getResources());
        ImageView newPlayerOutline = findViewById(R.id.new_player_cardview_fox_image);
        newPlayerOutline.setImageBitmap(newPlayerOutlineBMP);

        Bitmap newPlayerPlusBMP = ImageHandler.getScaledBitmapByWidth(R.drawable.plus_icon_hd, metrics.widthPixels / 7, getResources());
        ImageView newPlayerPlusIV = findViewById(R.id.new_player_cardview_plus_image);
        newPlayerPlusIV.setImageBitmap(newPlayerPlusBMP);

        // give each fox slightly less than a quarter of the space
        int spacePerFox = (foxContainer.getWidth() * 20) / 100;

        Bitmap fox1 = ImageHandler.getScaledBitmapByWidth(R.drawable.arcticfoxsilcoloured, spacePerFox, getResources());
        Bitmap fox2 = ImageHandler.getScaledBitmapByWidth(R.drawable.kitfoxsilcoloured, spacePerFox, getResources());
        Bitmap fox3 = ImageHandler.getScaledBitmapByWidth(R.drawable.roundendsilcoloured, spacePerFox, getResources());
        Bitmap fox4 = ImageHandler.getScaledBitmapByWidth(R.drawable.silverfoxsilcoloured, spacePerFox, getResources());

        Timber.d( "container count: " + foxContainer.getChildCount());
        Timber.d( "container width: " + foxContainer.getWidth());
        Timber.d( "container space per fox: " + spacePerFox);

        ImageView fox1IV = foxContainer.findViewById(R.id.existing_player_cardview_fox1);
        fox1IV.setImageBitmap(fox1);
        ImageView fox2IV = foxContainer.findViewById(R.id.existing_player_cardview_fox2);
        fox2IV.setImageBitmap(fox2);
        ImageView fox3IV = foxContainer.findViewById(R.id.existing_player_cardview_fox3);
        fox3IV.setImageBitmap(fox3);
        ImageView fox4IV = foxContainer.findViewById(R.id.existing_player_cardview_fox4);
        fox4IV.setImageBitmap(fox4);

    }

    private void calculateScreenDimensions() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    private void setup() {
        CardView npCardView = findViewById(R.id.fragment_select_cardview_new_player);
        CardView epCardView = findViewById(R.id.fragment_select_cardview_existing_player);
//        Button npButton = findViewById(R.id.choice_new_player_button);
//        Button epButton = findViewById(R.id.choice_existing_player_button);
        npCardView.setOnClickListener(newOrExistingListener);
        epCardView.setOnClickListener(newOrExistingListener);

        Button startButton = findViewById(R.id.nextPlayerStart);
        startButton.setOnClickListener(startListener);

        currentPlayerTV = findViewById(R.id.current_playing_as_choice);

        new Thread(() -> {
            Looper.prepare();
            ArrayList<PlayerIdentity> players = GameData.getNamedPlayerList(SwapChooseActivity.this);
            for (GameInstance g : HomeScreen.allGameInstances) {
                ListIterator<PlayerIdentity> iter = players.listIterator();
                while (iter.hasNext()) {
                    if (iter.next().ID.equals(g.getID())) {
                        iter.remove();
                    }
                }
            }
            playersAdapter = new PlayersAdapter(players, loadPlayerProfilePics(players), SwapChooseActivity.this);
            isAdapterLoaded = true;
        }).start();
    }

    private void hideNoExistingPlayersMessage() {
//        int numExistingPlayers = GameData.getNamedPlayerList(SwapChooseActivity.this).size();


//        if(numExistingPlayers == 1){
//            TextView noExistingPlayersTV = findViewById(R.id.existing_player_fragment_noExistingPlayersTV);
//            noExistingPlayersTV.setWidth(screenWidth/2);
//            noExistingPlayersTV.setVisibility(View.INVISIBLE);
//        }
    }

    private ArrayList<Bitmap> loadPlayerProfilePics(ArrayList<PlayerIdentity> players) {
        ArrayList<Bitmap> profilePics = new ArrayList<>();
        Bitmap defaultPicture = null;
        for (PlayerIdentity p : players) {
            GameData plyrGd = new GameData(this, p.ID);
            Bitmap profPic;
            if (!plyrGd.getProfilePicture().equals("")) {
                Uri myFileUri = Uri.parse(plyrGd.getProfilePicture());
                profPic = ImageHandler.getBitmapFromUri(this, myFileUri, 120);
            } else {
                if (defaultPicture == null) {     // Only load if needed
                    defaultPicture = ImageHandler.getScaledBitmapByHeight(GameData.PROFILE_DEFAULT_IMG, 120, getResources());

                }
                profPic = defaultPicture;
            }
            profilePics.add(profPic);
        }
        return profilePics;
    }

    @Override
    public void setChoice(PlayerIdentity choosenPlayer) {
        if (currentPlayerTV == null) {
            return;
        }
        currentPlayer = choosenPlayer;
        String currentChoice = "Playing as " + choosenPlayer.username;
        currentPlayerTV.setText(currentChoice);
    }

    @Override
    public PlayersAdapter getPlayersAdapter() {
        // TODO: Add loading dialog while waiting.
        while (!isAdapterLoaded) {
            Timber.d( "Waiting for adapter to finish loading ...");
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return playersAdapter;
    }

    private View.OnClickListener newOrExistingListener = view -> decideFragment( (String) view.getTag() );
    private View.OnClickListener startListener = view -> startGame();

    private void startGame() {
        if (currentPlayer == null) {
            return;
        }
        if (!GameData.doesPlayerExist(currentPlayer.ID, this)) {
            new GameData(this, currentPlayer.ID, currentPlayer.username);
        }
        int currentIndex = HomeScreen.allGameInstances
                .get(HomeScreen.allGameInstances.size() - 1)
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
        Timber.d( "Creating the fragment: " + CURRENT_FRAGMENT);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.player_select_container, fragment, CURRENT_FRAGMENT)
                .commit();
//                showNoExistingPlayersMessage();
    }

    private void removeExistingFragment() {
        if (CURRENT_FRAGMENT.equals("")) {
            Timber.d( "No fragment already exists");
            return;
        }
        Timber.d( "Removing old fragment: " + CURRENT_FRAGMENT);
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(CURRENT_FRAGMENT);
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(fragment)
                    .commit();
        }
    }

    private void decideFragment(String choice) {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        Timber.d( "Tag is: " + choice);
        if (CURRENT_FRAGMENT.equals(choice)) {
            Timber.d( "Tag is already selected");
            return;
        }
        removeExistingFragment();
        Timber.d( "Adding new fragment");
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                // User chose the "Profile" item, jump to the profile page
                Intent profileScreenIntent = new Intent(SwapChooseActivity.this, ProfileActivity.class);
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
        navBurger.navigateTo(item, SwapChooseActivity.this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

}
