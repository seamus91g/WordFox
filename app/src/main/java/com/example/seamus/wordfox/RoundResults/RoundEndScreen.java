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

import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.GameGridAdapter;
import com.example.seamus.wordfox.GameInstance;
import com.example.seamus.wordfox.GridItemDecoration;
import com.example.seamus.wordfox.HomeScreen;
import com.example.seamus.wordfox.IVmethods;
import com.example.seamus.wordfox.ImageHandler;
import com.example.seamus.wordfox.NavigationBurger;
import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.SwapActivity;
import com.example.seamus.wordfox.WifiActivityContract;
import com.example.seamus.wordfox.WifiService;
import com.example.seamus.wordfox.WifiServiceConnection;
import com.example.seamus.wordfox.WordfoxConstants;
import com.example.seamus.wordfox.game_screen.GameActivity;
import com.example.seamus.wordfox.profile.ProfileActivity;
import com.example.seamus.wordfox.results_screen.RoundnGameResults;
import com.google.android.gms.ads.InterstitialAd;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;


public class RoundEndScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        WifiActivityContract,
        RoundEndContract.View {

    public static final String MONITOR_TAG = "myTag";
    private RoundEndPresenter presenter;
    private int gameIndexNumber;
    private WifiServiceConnection netConnService;
    private boolean isOnline;
    private LinearLayout container;
    private NavigationBurger navBurger = new NavigationBurger();
    private boolean backButtonPressedOnce = false;
    private ConstraintLayout cl;
    private int screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_end_screen);

        ///////////////////////  Initialisations
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

//        GameData plyrGd = new GameData(this, gameInstance.getID());
        int maxScore = gameInstance.getLongestPossible().length();

        int playerScore = gameInstance.getScore();

        String playerResult = playerScore + "/" + maxScore;
        adjustSpeechBubble(playerResult);


//        WindowManager myWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
//        Display display = myWindowManager.getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        screenWidth = size.x;
        int round = gameInstance.getRound();
        calculateScreenWidth();
        String lets = gameInstance.getRoundLetters();
        setupResults(screenSize.x / 4, Arrays.copyOfRange(lets.split(""), 1, lets.length() + 1), gameInstance.getSuggestedWordsOfRound(round));

        createPlayerResultGrid(screenSize.x/4, Arrays.copyOfRange(lets.split(""), 1, lets.length() + 1), gameInstance.getLongestWord());
    }


    private void createPlayerResultGrid(int oneGridWidth, String[] letters, String word){

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
        gridView.addItemDecoration(new GridItemDecoration(oneGridWidth/10));

    }

    private void calculateScreenWidth() {
        WindowManager myWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = myWindowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        Log.d("roundendscreen", "1: expected screenwidth: " + 1440);
        Log.d("roundendscreen", "1: screenwidth: " + screenWidth);
    }

    private void bindWifiService() {
        Log.d(GameActivity.MONITOR_TAG, "RE: Game is online!");
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
//        presenter.populatePossibleWords();
//        presenter.displayWelcomeFox();

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

        Log.d("roundendscreen", "2: expected screenwidth: " + 1440);
        Log.d("roundendscreen", "2: screenwidth: " + screenWidth);

        int foxPercent = (int) (0.35 * screenWidth);
        int foxSpeechPercent = (int) (0.64 * screenWidth);
        Log.d("roundendscreen", "3: expected foxPercent: " + 504);
        Log.d("roundendscreen", "3: foxPercent: " + foxPercent);
        Log.d("roundendscreen", "4: expected foxSpeechPercent: " + 921);
        Log.d("roundendscreen", "4: foxSpeechPercent: " + foxSpeechPercent);

        ImageView instructionFoxIV = findViewById(R.id.content_round_end_screen_instructionFoxIV);
        instructionFoxIV.setImageBitmap(ImageHandler.getScaledBitmapByWidth(R.drawable.roundendsilcoloured, foxPercent, getResources()));
        Log.d("roundendscreen", "5: expected foxPercent: " + 504);
        Log.d("roundendscreen", "5: foxPercent: " + ImageHandler.getScaledBitmapByWidth(R.drawable.roundendsilcoloured, foxPercent, getResources()).getWidth());

        ImageView instructionFoxSpeechBubbleIV = findViewById(R.id.content_round_end_screen_instructionFoxSpeechBubbleIV);
        instructionFoxSpeechBubbleIV.setImageBitmap(ImageHandler.getScaledBitmapByWidth(R.drawable.speechbubbleright, foxSpeechPercent, getResources()));
        Log.d("roundendscreen", "52: expected foxPercent: " + 921);
        Log.d("roundendscreen", "52: foxPercent: " + ImageHandler.getScaledBitmapByWidth(R.drawable.speechbubbleright, foxSpeechPercent, getResources()).getWidth());

    }

    private void adjustSpeechBubble(String playerResult) {

        ConstraintLayout winnerBannerCL = findViewById(R.id.content_round_end_screen_foxWithSpeechCL);
        String longestWordHeader = getResources().getString(R.string.you_scored) + "\n" + playerResult;

        TextView instructionFoxTV = winnerBannerCL.findViewById(R.id.content_round_end_screen_instructionFoxTV);
        IVmethods.setTVwidthPercentOfIV(findViewById(R.id.content_round_end_screen_instructionFoxSpeechBubbleIV),
                instructionFoxTV, 0.8, longestWordHeader);
        Log.d("roundendscreen", "6: expected instructionFoxTV: " + 0.8 * ImageHandler.getScaledBitmapByWidth(R.drawable.speechbubbleright, (int) (0.64 * screenWidth), getResources()).getWidth());
        Log.d("roundendscreen", "6: instructionFoxTV: " + findViewById(R.id.content_round_end_screen_instructionFoxSpeechBubbleIV).getWidth());


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

//    @Override
//    public Bitmap getBlankScaledGrid(int shortestSide) {
//        return ImageHandler.getScaledBitmapByLongestSide(R.drawable.letter_grid_blank, shortestSide, getResources());
//
//    }

//    @Override
//    public void addRowPossibleWords() {
//        if (container == null) {
//            container = findViewById(R.id.suggestions_container);
//        }
//        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
//        View childLayout = inflater.inflate(R.layout.row_of_grids_with_word, null);
//        container.addView(childLayout);
//    }

//    @Override
//    public void addPossibleWord(Bitmap gridBmp, String word, int count) {
//        int tagSuffix = (count % 3) + 1;
//        ConstraintLayout ll = (ConstraintLayout) container.getChildAt(container.getChildCount() - 1);
//
//        ImageView grid = ll.findViewWithTag(WordfoxConstants.GRID_TAG_PREFIX + tagSuffix);
//        grid.setImageBitmap(gridBmp);
//        grid.setVisibility(View.VISIBLE);
//
//        TextView wordTV = ll.findViewWithTag(WordfoxConstants.WORD_TAG_PREFIX + tagSuffix);
//        wordTV.setText(word);
//    }

//    @Override
//    public void hideResultGrid(int count, int width) {
//        int tagSuffix = (count % 3) + 1;
//        ConstraintLayout ll = (ConstraintLayout) container.getChildAt(container.getChildCount() - 1);
//        ImageView grid = ll.findViewWithTag(WordfoxConstants.GRID_TAG_PREFIX + tagSuffix);
//        grid.getLayoutParams().width = width;
//    }

    @Override
    public Bitmap getPlayerProfPic(int profilePicScreenWidth) {
        String profPicStr = new GameData(this, HomeScreen.allGameInstances.get(gameIndexNumber).getID()).getProfilePicture();
        if (profPicStr.equals("")) {
            Log.d("roundendscreen", "7: expected profPic: " + profilePicScreenWidth);
            Log.d("roundendscreen", "7: expected profPic: w_h " + loadDefaultProfilePic(profilePicScreenWidth).getWidth() + " __ " + loadDefaultProfilePic(profilePicScreenWidth).getHeight());
            return loadDefaultProfilePic(profilePicScreenWidth);
        } else {
            Uri myFileUri = Uri.parse(profPicStr);
            // TODO: Not cropping this image means using more memory than required.
            Bitmap profPic = ImageHandler.getBitmapFromUriScaleShortestSide(this, myFileUri, profilePicScreenWidth);
            if (profPic == null) {
                Log.d("roundendscreen", "9: expected size: " + 288);
                Log.d("roundendscreen", "9: actual size: " + profilePicScreenWidth);
                return loadDefaultProfilePic(profilePicScreenWidth);
            }
            return profPic;
        }
    }

    private Bitmap loadDefaultProfilePic(int size) {
        return ImageHandler.getScaledBitmapByLongestSide(
                GameData.PROFILE_DEFAULT_IMG,
                size,          // TODO: Will be shortest side, not necessarily width
                getResources());

//        Bitmap bm2 = ImageHandler.getScaledBitmapByWidth(
//                GameData.PROFILE_DEFAULT_IMG_2,
//                size,          // TODO: Will be shortest side, not necessarily width
//                getResources());
//
//        Bitmap bm3 = ImageHandler.getScaledBitmapByWidth(
//                GameData.PROFILE_DEFAULT_IMG_3,
//                size,          // TODO: Will be shortest side, not necessarily width
//                getResources());
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
        Log.d(MONITOR_TAG, msg);
    }

    @Override
    public void setPlayerNameWithPercent(String nameAndPercent) {
        TextView resultPlayerNameView = cl.findViewById(R.id.round_end_result_player_name);
        resultPlayerNameView.setText(nameAndPercent);
    }

//    @Override
//    public void setPlayerScoreText(String scoreText) {
//        TextView resultPlayerScoreView = cl.findViewById(R.id.round_end_result_best_word);
//        resultPlayerScoreView.setText(scoreText);
//    }

//    @Override
//    public void setSpeechBubbleText(String playerBubbleText) {
//        TextView longestWordView = cl.findViewById(R.id.content_round_end_screen_instructionFoxTV);
//        longestWordView.setText(playerBubbleText);
//    }

//    @Override
//    public void setMyGridResult(Bitmap bmp) {
////        ImageView roundEndGridBest = cl.findViewById(R.id.round_end_result_grid);
////        roundEndGridBest.setImageBitmap(bmp);
//    }

    @Override
    public void setPlayerProfilePic(Bitmap profPic) {
        CircleImageView profilePicView = cl.findViewById(R.id.round_end_profile_pic);
        if (profPic != null) {
            profilePicView.setImageBitmap(profPic);
        }
    }

}
