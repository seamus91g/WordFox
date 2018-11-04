package com.example.seamus.wordfox.RoundResults;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.GameInstance;
import com.example.seamus.wordfox.HomeScreen;
import com.example.seamus.wordfox.ImageHandler;
import com.example.seamus.wordfox.NavigationBurger;
import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.SwapActivity;
import com.example.seamus.wordfox.WifiService;
import com.example.seamus.wordfox.WifiServiceConnection;
import com.example.seamus.wordfox.WordfoxConstants;
import com.example.seamus.wordfox.game_screen.GameActivity;
import com.example.seamus.wordfox.profile.ProfileActivity;
import com.example.seamus.wordfox.results_screen.RoundnGameResults;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import de.hdodenhof.circleimageview.CircleImageView;


public class RoundEndScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RoundEndContract.View {

    public static final String MONITOR_TAG = "myTag";
    private RoundEndPresenter presenter;
    private int gameIndexNumber;
    private WifiServiceConnection netConnService;
    private boolean isOnline;
    private InterstitialAd mInterstitialAd;
    private boolean displayInterstitial;
    private boolean failedToLoadInterstitial = false;
    private LinearLayout container;
    private NavigationBurger navBurger = new NavigationBurger();
    private Point screenSize;
    private ConstraintLayout cl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_end_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gameIndexNumber = getIntent().getExtras().getInt(GameActivity.GAME_INDEX);

        int colorPrimary = getResources().getColor(R.color.game_font_color);
        int colorSecondary = getResources().getColor(R.color.colorLightAccent);
        // TODO: Separate presenter for round and game end
        screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        presenter = new RoundEndPresenter(this,
                screenSize.x,
                HomeScreen.allGameInstances.get(gameIndexNumber),
                colorPrimary,
                colorSecondary);

        FloatingActionButton fab = findViewById(R.id.fab_round_end);
        fab.setOnClickListener(view -> {
            if (displayInterstitial) {
                displayInterstitial();
            } else {
                startGame();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        cl = findViewById(R.id.round_end_root_layout);
        presenter.populatePlayerDetails();
        presenter.populatePossibleWords();

        boolean isFinalRound;
        isFinalRound = HomeScreen.allGameInstances.get(0).getRound() == WordfoxConstants.NUMBER_ROUNDS - 1;
        isOnline = HomeScreen.allGameInstances.get(0).isOnline();
        if (isOnline && isFinalRound) {
            Log.d(GameActivity.MONITOR_TAG, "RE: Game is online!");
            netConnService = new WifiServiceConnection();
            bindService();
            // Allow time for service to finish binding
            // TODO: Is service guaranteed to be bound in time for this??
            new Handler().post(() -> broadcastMyResults(HomeScreen.allGameInstances.get(0)));
        }
        if (isFinalRound) {
            Log.d(MONITOR_TAG, "Its final round !!!!");
            displayInterstitial = GameData.checkIfDisplayInterstitial(this);
            if (displayInterstitial) {
                loadInterstitial();
                mInterstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdClosed() {
                        Log.d(MONITOR_TAG, "Will start game when ad closes ..");
                        startGame();
                    }

                    @Override
                    public void onAdFailedToLoad(int errorCode) {
                        // Code to be executed when an ad request fails.
                        Log.d(MONITOR_TAG, "Interstitial failed to load!!");
                        failedToLoadInterstitial = true;
                    }
                });
            }
        }

        int width = screenSize.x;

        ImageView myIV = findViewById(R.id.round_end_banner);
        myIV.setImageBitmap(ImageHandler.getScaledBitmap(R.drawable.roundendwithspeech, (int) (0.35 * width), getResources()));

    }

    private void startGame() {
        presenter.startGame();
    }

    private void loadInterstitial() {
        AdRequest adRequestTest = new AdRequest.Builder()
                .addTestDevice("16930B084D136C6BEFB468B4D1F2919C")
                .build();
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(adRequestTest);
    }

    private void displayInterstitial() {
        int waitmax = 30;
        int wait = 0;
        while (!mInterstitialAd.isLoaded()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (failedToLoadInterstitial || ++wait > waitmax) {
                startGame();
                return;
            }
        }
        mInterstitialAd.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unBindService();
    }

    private void unBindService() {
        if (!isOnline || netConnService == null || !netConnService.isBound) {
            return;
        }
        Log.d(MONITOR_TAG, "Unbinding service in " + this.toString());
        unbindService(netConnService);
        netConnService.isBound = false;
    }

    private void bindService() {
        if (isOnline) {
            Log.d(MONITOR_TAG, "Binding " + this.toString());
            bindService(new Intent(this, WifiService.class), netConnService,
                    Context.BIND_AUTO_CREATE);
        }
    }

    private void broadcastMyResults(GameInstance myGameInstance) {
        Log.d(MONITOR_TAG, "Json results: " + myGameInstance.resultAsJson().toString());
        WifiService ws = netConnService.getWifiService();
        String jsString = myGameInstance.resultAsJson().toString();
        Log.d(MONITOR_TAG, "Is WifiServe null? , bound? " + (ws == null) + ", " + netConnService.isBound);
        int count = 0;
        while (ws == null) {
            Log.d(MONITOR_TAG, "|||||||||||||||||||||||||||||||");
            Log.d(MONITOR_TAG, "| Waiting for service to bind |");
            Log.d(MONITOR_TAG, "|||||||||||||||||||||||||||||||");
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (++count > 5) {
                break;
            }
            ws = netConnService.getWifiService();
        }
        ws.sendData(jsString);
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                // User chose the "Profile" item, jump to the profile page
                Intent profileScreenIntent = new Intent(RoundEndScreen.this, ProfileActivity.class);
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
        navBurger.navigateTo(item, RoundEndScreen.this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    @Override
    public void makeToast(String message) {

    }

    @Override
    public void displayTitle(String title) {

    }

    @Override
    public void nextRound() {
        gameProceed(GameActivity.class, gameIndexNumber);
    }

    @Override
    public boolean playerSwitch() {
        if (HomeScreen.allGameInstances.size() < HomeScreen.allGameInstances.get(0).getNumberOfPlayers()) {
            gameProceed(SwapActivity.class, gameIndexNumber + 1);
            return true;
        }
        return false;
    }

    private void gameProceed(Class nextActivity, int index) {
        Intent gameIntent = new Intent(this, nextActivity);
        gameIntent.putExtra(GameActivity.GAME_INDEX, index);
        startActivity(gameIntent);
    }

    @Override
    public void proceedToFinalResults() {
        Intent endScreenIntent = new Intent(this, RoundnGameResults.class);
        startActivity(endScreenIntent);
    }

    @Override
    public Bitmap getBlankScaledGrid(int shortestSide) {
        return ImageHandler.getScaledBitmap(R.drawable.letter_grid_blank, shortestSide, getResources());

    }

    @Override
    public void addRowPossibleWords() {
        if (container == null) {
            container = findViewById(R.id.suggestions_container);
        }
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View childLayout = inflater.inflate(R.layout.row_of_grids_with_word, null);
        container.addView(childLayout);
    }

    @Override
    public void addPossibleWord(Bitmap gridBmp, String word, int count) {
        int tagSuffix = (count % 3) + 1;
        ConstraintLayout ll = (ConstraintLayout) container.getChildAt(container.getChildCount() - 1);

        ImageView grid = ll.findViewWithTag(WordfoxConstants.GRID_TAG_PREFIX + tagSuffix);
        grid.setImageBitmap(gridBmp);
        grid.setVisibility(View.VISIBLE);

        TextView wordTV = ll.findViewWithTag(WordfoxConstants.WORD_TAG_PREFIX + tagSuffix);
        wordTV.setText(word);
    }

    @Override
    public void hideResultGrid(int count, int width) {
        int tagSuffix = (count % 3) + 1;
        ConstraintLayout ll = (ConstraintLayout) container.getChildAt(container.getChildCount() - 1);
        ImageView grid = ll.findViewWithTag(WordfoxConstants.GRID_TAG_PREFIX + tagSuffix);
        Log.d(MONITOR_TAG, "Width is : " + width);
        grid.getLayoutParams().width = width;
    }

    @Override
    public Bitmap getPlayerProfPic(int profilePicScreenWidth) {
        String profPicStr = new GameData(this, HomeScreen.allGameInstances.get(gameIndexNumber).getID()).getProfilePicture();
        if (profPicStr.equals("")) {
            return ImageHandler.getScaledBitmap(
                    GameData.PROFILE_DEFAULT_IMG,
                    profilePicScreenWidth,          // TODO: Will be shortest side, not necessarily width
                    getResources());
        } else {
            Uri myFileUri = Uri.parse(profPicStr);
            ImageHandler imageHandler = new ImageHandler(this);     // Handle this better
            return imageHandler.getBitmapFromUri(myFileUri, profilePicScreenWidth);      // TODO: Why not static method?
        }
    }

    @Override
    public void setPlayerNameWithPercent(String nameAndPercent) {
        TextView resultPlayerNameView = cl.findViewById(R.id.round_end_result_player_name);
        resultPlayerNameView.setText(nameAndPercent);
    }

    @Override
    public void setPlayerScoreText(String scoreText) {
        TextView resultPlayerScoreView = cl.findViewById(R.id.round_end_result_best_word);
        resultPlayerScoreView.setText(scoreText);
    }

    @Override
    public void setSpeechBubbleText(String playerBubbleText) {
        TextView longestWordView = cl.findViewById(R.id.round_end_longest_word);
        longestWordView.setText(playerBubbleText);
    }

    @Override
    public void setMyGridResult(Bitmap bmp) {
        ImageView roundEndGridBest = cl.findViewById(R.id.round_end_result_grid);
        roundEndGridBest.setImageBitmap(bmp);
    }

    @Override
    public void setPlayerProfilePic(Bitmap profPic) {
        CircleImageView profilePicView = cl.findViewById(R.id.round_end_profile_pic);
        if (profPic != null) {
            profilePicView.setImageBitmap(profPic);
        }
    }
}
