package capsicum.game.wordfox.RoundResults;

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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import capsicum.game.wordfox.GameData;
import capsicum.game.wordfox.GameGridAdapter;
import capsicum.game.wordfox.GameInstance;
import capsicum.game.wordfox.GridItemDecoration;
import capsicum.game.wordfox.HomeScreen;
import capsicum.game.wordfox.IVmethods;
import capsicum.game.wordfox.ImageHandler;
import capsicum.game.wordfox.NavigationBurger;
import capsicum.game.wordfox.R;
import capsicum.game.wordfox.SwapActivity;
import capsicum.game.wordfox.WifiActivityContract;
import capsicum.game.wordfox.WifiService;
import capsicum.game.wordfox.WifiServiceConnection;
import capsicum.game.wordfox.WordfoxConstants;
import capsicum.game.wordfox.game_screen.GameActivity;
import capsicum.game.wordfox.profile.ProfileActivity;
import capsicum.game.wordfox.results_screen.RoundnGameResults;

import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;
import timber.log.Timber;


public class RoundEndScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        WifiActivityContract,
        RoundEndContract.View {

    public static final String MONITOR_TAG = "myTag";
    private RoundEndPresenter presenter;
    private int gameIndexNumber;
    private WifiServiceConnection netConnService;
    private boolean isOnline;
    private NavigationBurger navBurger = new NavigationBurger();
    private boolean backButtonPressedOnce = false;
    private ConstraintLayout cl;
    private int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            startActivity(new Intent(this, HomeScreen.class));
            return;
        }
        setContentView(R.layout.activity_round_end_screen);

        ///////////////////////  Initialisations
        Timber.tag("tag_RoundEnd");
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        boolean isFinalRound = HomeScreen.allGameInstances.get(0).getRound() == WordfoxConstants.NUMBER_ROUNDS - 1;
        gameIndexNumber = getIntent().getExtras().getInt(GameActivity.GAME_INDEX);
        cl = findViewById(R.id.round_end_root_layout);
        isOnline = HomeScreen.allGameInstances.get(0).isOnline();

        ///////////////////////  Prepare the presenter
        setUpPresenter(screenSize.x, isFinalRound);

        ///////////////////////  Navigation items
        setUpNavigationItems();

        ///////////////////////  Broadcast Wifi-Direct game results if applicable
        if (isOnline && isFinalRound) {
            bindWifiService();
        }
        setUpRoundEndFox();

        GameInstance gameInstance = HomeScreen.allGameInstances.get(gameIndexNumber);

        int maxScore = gameInstance.getLongestPossible().length();
        int playerScore = gameInstance.getScore();

        String playerResult = playerScore + "/" + maxScore;
        adjustSpeechBubble(playerResult);

        int round = gameInstance.getRound();
        calculateScreenWidth();
        String lets = gameInstance.getRoundLetters();
        setupResults(screenSize.x / 4, Arrays.copyOfRange(lets.split(""), 1, lets.length() + 1), gameInstance.getSuggestedWordsOfRound(round));

        createPlayerResultGrid(screenSize.x / 4, Arrays.copyOfRange(lets.split(""), 1, lets.length() + 1), gameInstance.getLongestWord());
    }


    private void createPlayerResultGrid(int oneGridWidth, String[] letters, String word) {

        int oneCellWidth = ((oneGridWidth * (100 - 10)) / 100) / 3;
        Bitmap pressedCell = ImageHandler.getScaledBitmapByWidth(R.drawable.single_grid_cell_purple, oneCellWidth, getResources());
        Bitmap notPressedCell = ImageHandler.getScaledBitmapByWidth(R.drawable.single_grid_cell_green, oneCellWidth, getResources());
        int oneCellHeight = pressedCell.getHeight();
        int containerHeight = (((oneCellHeight * 3) * 100) / (100 - 10));

        View v = LayoutInflater.from(this)
                .inflate(R.layout.game_grid_xml, null);
        ConstraintLayout cl = v.findViewWithTag(GameGridAdapter.GRID_CONTAINER_TAG);
        ViewGroup.LayoutParams clparams = cl.getLayoutParams();
        clparams.height = containerHeight;
        clparams.width = oneGridWidth;
        cl.setLayoutParams(clparams);
        log("cl h,w : " + clparams.height + ", " + clparams.width);

        GameGridAdapter.GridViewHolder gridView = new GameGridAdapter.GridViewHolder(v, letters, notPressedCell, pressedCell);
        gridView.onBind(GameGridAdapter.findClickIndices(word, letters), word);
        FrameLayout fl = findViewById(R.id.round_end_result_grid_container);
        fl.addView(v);

    }

    private void setupResults(int oneGridWidth, String[] gameLetters, ArrayList<String> suggestedWordsOfRound) {
        for (int j = 0; j < gameLetters.length; ++j) {
            log("Letter: " + gameLetters[j]);
        }
        log("Expected width : " + oneGridWidth);
        RecyclerView gridView = findViewById(R.id.results_scrollview_grids);
        gridView.setLayoutManager(new GridLayoutManager(this, 3));
        gridView.setAdapter(new GameGridAdapter(suggestedWordsOfRound, gameLetters, oneGridWidth, getResources()));
        gridView.addItemDecoration(new GridItemDecoration(oneGridWidth / 10));
    }

    private void calculateScreenWidth() {
        WindowManager myWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = myWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
    }

    private void bindWifiService() {
        Timber.d( "RE: Game is online!");
        netConnService = new WifiServiceConnection(this);
        bindService();
    }

    private void setUpNavigationItems() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Set up 'next' button
        FloatingActionButton fab = findViewById(R.id.fab_round_end);
        fab.setOnClickListener(view -> {
            startGame();
            finish();
        });
    }

    @Override
    public InterstitialAd getInterstitial() {
        return new InterstitialAd(this);
    }

    private void setUpPresenter(int screenWidth, boolean isFinalRound) {
        boolean displayInterstitial = (isFinalRound && GameData.checkIfDisplayInterstitial(this));
        presenter = new RoundEndPresenter(this,
                screenWidth,
                HomeScreen.allGameInstances.get(gameIndexNumber),
                getResources().getColor(R.color.game_font_color),
                getResources().getColor(R.color.colorLightAccent),
                displayInterstitial,
                FirebaseAnalytics.getInstance(this));
        presenter.prepareInterstitialAdvert();
        presenter.populatePlayerDetails();
        presenter.displayTitle();
    }

    private void startGame() {
        presenter.startGame();
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
        Timber.d( "Unbinding service in " + this.toString());
        unbindService(netConnService);
        netConnService.isBound = false;
    }

    private void bindService() {
        if (isOnline) {
            Timber.d( "Binding " + this.toString());
            bindService(new Intent(this, WifiService.class), netConnService,
                    Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onServiceBound() {
        broadcastMyResults(HomeScreen.allGameInstances.get(0));
    }

    private void broadcastMyResults(GameInstance myGameInstance) {
        WifiService ws = netConnService.getWifiService();
        String jsString = myGameInstance.resultAsJson().toString();
        if (ws == null) {
            // Should not be possible, can only be called after service bound.
            throw new IllegalStateException();
        }
        ws.sendData(jsString);
    }

    // Must press back button twice in quick succession to return to home
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // If pressed recently, proceed to home screen
            if (this.backButtonPressedOnce) {
                Intent homeScreenIntent = new Intent(this, HomeScreen.class);
                startActivity(homeScreenIntent);
                finish();
                return;
            }
            // Pressed once. Inform user a second click will exit the game.
            this.backButtonPressedOnce = true;
            Toast toastMessage = Toast.makeText(this, "Double tap BACK to exit the game", Toast.LENGTH_SHORT);
            toastMessage.setGravity(Gravity.TOP, 0, 40);
            toastMessage.show();
            // Listen for another click for a brief amount of time. If none, reset the flag
            new Handler().postDelayed(() -> backButtonPressedOnce = false, 1500);
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

    private void setUpRoundEndFox() {

        WindowManager myWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = myWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;

        int foxPercent = (int) (0.35 * screenWidth);
        int foxSpeechPercent = (int) (0.64 * screenWidth);
        ImageView instructionFoxIV = findViewById(R.id.content_round_end_screen_instructionFoxIV);
        instructionFoxIV.setImageBitmap(ImageHandler.getScaledBitmapByWidth(R.drawable.roundendsilcoloured, foxPercent, getResources()));

        ImageView instructionFoxSpeechBubbleIV = findViewById(R.id.content_round_end_screen_instructionFoxSpeechBubbleIV);
        instructionFoxSpeechBubbleIV.setImageBitmap(ImageHandler.getScaledBitmapByWidth(R.drawable.speechbubbleright, foxSpeechPercent, getResources()));
    }

    private void adjustSpeechBubble(String playerResult) {
        ConstraintLayout winnerBannerCL = findViewById(R.id.content_round_end_screen_foxWithSpeechCL);
        String longestWordHeader = getResources().getString(R.string.you_scored) + "\n" + playerResult;

        TextView instructionFoxTV = winnerBannerCL.findViewById(R.id.content_round_end_screen_instructionFoxTV);
        IVmethods.setTVwidthPercentOfIV(findViewById(R.id.content_round_end_screen_instructionFoxSpeechBubbleIV),
                instructionFoxTV, 0.8, longestWordHeader);
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
        this.setTitle(title);
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
    public Bitmap getPlayerProfPic(int profilePicScreenWidth) {
        String profPicStr = new GameData(this, HomeScreen.allGameInstances.get(gameIndexNumber).getID()).getProfilePicture();
        if (profPicStr.equals("")) {
            return loadDefaultProfilePic(profilePicScreenWidth);
        } else {
            Uri myFileUri = Uri.parse(profPicStr);
            // TODO: Not cropping this image means using more memory than required.
            Bitmap profPic = ImageHandler.getBitmapFromUriScaleShortestSide(this, myFileUri, profilePicScreenWidth);
            int shortSide;
            int startX, startY;
            if (profPic.getHeight() < profPic.getWidth()) {
                shortSide = profPic.getHeight();
                startX = (profPic.getWidth() - shortSide) / 2;
                startY = 0;
            } else {
                shortSide = profPic.getWidth();
                startY = (profPic.getHeight() - shortSide) / 2;
                startX = 0;
            }
            profPic = Bitmap.createBitmap(profPic, startX, startY, shortSide, shortSide);
            if (profPic == null) {
                return loadDefaultProfilePic(profilePicScreenWidth);
            }
            return profPic;
        }
    }

    private Bitmap loadDefaultProfilePic(int size) {
        return ImageHandler.getScaledBitmapByLongestSide(
                GameData.PROFILE_DEFAULT_IMG,
                size,
                getResources());
    }

    private static void logBmp(Bitmap bitmap, String id) {
        log(id + " == bitmap~ h, w, bytes, dens : "
                + bitmap.getHeight() + ", "
                + bitmap.getWidth() + ", "
                + bitmap.getByteCount() + ", "
                + bitmap.getDensity());
    }

    private static void logBmp(Bitmap bitmap, int id) {
        logBmp(bitmap, String.valueOf(id));
    }

    private static void log(String msg) {
        Timber.d( msg);
    }

    @Override
    public void setPlayerNameWithPercent(String nameAndPercent) {
        TextView resultPlayerNameView = cl.findViewById(R.id.round_end_result_player_name);
        resultPlayerNameView.setText(nameAndPercent);
    }

    @Override
    public void setPlayerProfilePic(Bitmap profPic) {
        CircleImageView profilePicView = cl.findViewById(R.id.round_end_profile_pic);
        if (profPic != null) {
            profilePicView.setImageBitmap(profPic);
        }
    }
}
