package com.example.seamus.wordfox.RoundResults;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.GameInstance;
import com.example.seamus.wordfox.GridImage;
import com.example.seamus.wordfox.HomeScreen;
import com.example.seamus.wordfox.ImageHandler;
import com.example.seamus.wordfox.NavigationBurger;
import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.SwapActivity;
import com.example.seamus.wordfox.WifiService;
import com.example.seamus.wordfox.WifiServiceConnection;
import com.example.seamus.wordfox.game_screen.GameActivity;
import com.example.seamus.wordfox.profile.ProfileActivity;
import com.example.seamus.wordfox.results_screen.RoundnGameResults;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.List;

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
    private NavigationBurger navBurger = new NavigationBurger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_end_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gameIndexNumber = getIntent().getExtras().getInt(GameActivity.GAME_INDEX);

        // TODO: Separate presenter for round and game end
        presenter = new RoundEndPresenter(this);

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

        populatePlayerDetails(HomeScreen.allGameInstances.get(gameIndexNumber));
        populatePossibleWords(HomeScreen.allGameInstances.get(gameIndexNumber));

        boolean isFinalRound;
        isFinalRound = HomeScreen.allGameInstances.get(0).getRound() == GameInstance.NUMBER_ROUNDS - 1;
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

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        ImageView myIV = findViewById(R.id.round_end_banner);
        myIV.setImageBitmap(ImageHandler.getScaledBitmap(R.drawable.roundendwithspeech, (int) (0.35*width),getResources()));

//
//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inTargetDensity = DisplayMetrics.DENSITY_DEFAULT;
//        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.roundendwithspeech, o);
//        int w = bmp.getWidth();
//        int h = bmp.getHeight();
//        Log.d("Warning", "onCreate: w: " + w);
//        Log.d("Warning", "onCreate: h: " + h);
//
//
//        TextView myTV = findViewById(R.id.round_end_longest_word);
//        myTV.measure(0, 0);
//        float halfTVWidth = myTV.getMeasuredWidth() / 2;
//
//        final int[] finalWidth = new int[1];
//
//        final ImageView iv = findViewById(R.id.round_end_banner);
//        ViewTreeObserver vto = iv.getViewTreeObserver();
//        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//            public boolean onPreDraw() {
//                iv.getViewTreeObserver().removeOnPreDrawListener(this);
//                finalWidth[0] = iv.getMeasuredWidth();
//
//                float extraWidth = halfTVWidth/finalWidth[0];
//
//
//                ConstraintSet set = new ConstraintSet();
//                ConstraintLayout constraintLayout = findViewById(R.id.round_end_root_layout);
//                set.clone(constraintLayout);
//                set.setHorizontalBias(R.id.round_end_longest_word,(float) (0.7413 - extraWidth));
//                set.applyTo(constraintLayout);
//
//                return true;
//            }
//        });

    }

    private void startGame() {
        presenter.startGame(HomeScreen.allGameInstances.get(gameIndexNumber));
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
        while (ws == null){
            Log.d(MONITOR_TAG, "|||||||||||||||||||||||||||||||");
            Log.d(MONITOR_TAG, "| Waiting for service to bind |");
            Log.d(MONITOR_TAG, "|||||||||||||||||||||||||||||||");
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(++count > 5){
                break;
            }
            ws = netConnService.getWifiService();
        }
        ws.sendData(jsString);
    }

    public void populatePlayerDetails(GameInstance gameInstance) {       // TODO:  Tidy this. Use MVP
        ConstraintLayout cl = findViewById(R.id.round_end_root_layout);
        GameData plyrGd = new GameData(this, gameInstance.getID());

        String profPicStr = plyrGd.getProfilePicture();
        Bitmap profPic = null;
        ImageHandler imageHandler = new ImageHandler(this);     // Handle this better
        if (!profPicStr.equals("")) {
            Uri myFileUri = Uri.parse(profPicStr);
            profPic = imageHandler.getBitmapFromUri(myFileUri, 120);
//            int scale = ImageHandler.getScaleFactor(getResources(), )
        }
        // Race condition if player ends game really quickly. Longest possible words might not yet be calculated.
        while (gameInstance.getLongestPossible() == null) {
            try {
                wait(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        int maxScore = gameInstance.getLongestPossible().length();
        int playerScore = gameInstance.getScore();
        int percentScore = (100 * playerScore) / (maxScore);

        String playerPercent = "  (" + percentScore + "%)";

        TextView resultPlayerNameView = cl.findViewById(R.id.round_end_result_player_name);
        String playerName = gameInstance.getName();
        resultPlayerNameView.setText(playerName + "\n" + playerPercent);

        TextView resultPlayerScoreView = cl.findViewById(R.id.round_end_result_best_word);
        resultPlayerScoreView.setText(gameInstance.getLongestWord() + " (" + gameInstance.getLongestWord().length() + ")");


        String playerResult = playerScore + " out of " + maxScore;
        TextView longestWordView = cl.findViewById(R.id.round_end_longest_word);
        String longestWordHeader = getResources().getString(R.string.you_scored) + "\n" + playerResult;
        longestWordView.setText(longestWordHeader);


        Bitmap gridBmp = BitmapFactory.decodeResource(getResources(), R.drawable.letter_grid_blank);
        gridBmp = ImageHandler.getResizedBitmap(gridBmp, ImageHandler.dp2px(this, 100), ImageHandler.dp2px(this, 100));  // TODO: Adjust to screen size

        GridImage gridWithText = new GridImage(gridBmp, gameInstance.getLongestWord().toUpperCase(), gameInstance.getRoundLetters(), getResources().getColor(R.color.game_font_color), getResources().getColor(R.color.colorLightAccent));
        ImageView roundEndGridBest = cl.findViewById(R.id.round_end_result_grid);
        roundEndGridBest.setImageBitmap(gridWithText.getBmp());

        CircleImageView profilePicView = cl.findViewById(R.id.round_end_profile_pic);
        if (profPic == null) {
            profPic = ImageHandler.getScaledBitmap(GameData.PROFILE_DEFAULT_IMG, 120, getResources());
//            profilePicView.setVisibility(View.GONE);
//            resultPlayerNameView.setPadding(ImageHandler.dp2px(this, 20), ImageHandler.dp2px(this, 10), 10, 10);
//            resultPlayerScoreView.setPadding(ImageHandler.dp2px(this, 20), 10, 10, ImageHandler.dp2px(this, 10));
        }
        profilePicView.setImageBitmap(profPic);
    }

    public void populatePossibleWords(GameInstance gameInstance) {
        List<String> possibleWords = gameInstance.getSuggestedWordsOfRound(gameInstance.getRound());
        LinearLayout container = findViewById(R.id.suggestions_container);
        Bitmap gridBmp = BitmapFactory.decodeResource(getResources(), R.drawable.letter_grid_blank);
        gridBmp = ImageHandler.getResizedBitmap(gridBmp, ImageHandler.dp2px(this, 100), ImageHandler.dp2px(this, 100));  // TODO: Adjust to screen size

        int count = 0;
        for (int i = 0; i < container.getChildCount(); ++i) {
            View row = container.getChildAt(i);
            for (int j = 0; j < GameInstance.NUMBER_ROUNDS; ++j) {
                String wordTag = "word_" + (j + 1);
                String gridTag = "grid_" + (j + 1);
                TextView wordTV = row.findViewWithTag(wordTag);
                ImageView grid = row.findViewWithTag(gridTag);
                if (count >= possibleWords.size()) {
                    wordTV.setVisibility(View.INVISIBLE);
                    grid.setVisibility(View.INVISIBLE);
                    continue;
                }
                String word = possibleWords.get(count).toUpperCase() + " (" + possibleWords.get(count).length() + ")";
                wordTV.setText(word);
                GridImage gridWithText = new GridImage(gridBmp, word, gameInstance.getRoundLetters(), getResources().getColor(R.color.game_font_color), getResources().getColor(R.color.colorLightAccent));
                grid.setImageBitmap(gridWithText.getBmp());
                ++count;
            }
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
}
