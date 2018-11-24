package capsicum.game.wordfox.screen_results_game;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import capsicum.game.wordfox.GameData;
import capsicum.game.wordfox.GameDetails;
import capsicum.game.wordfox.recyclerview_game_results.GameGridAdapter;
import capsicum.game.wordfox.recyclerview_game_results.PlayerDetailsItemDecoration;
import capsicum.game.wordfox.recyclerview_game_results.PlayerResultPackage;
import capsicum.game.wordfox.GameInstance;
import capsicum.game.wordfox.screen_home.HomeScreen;
import capsicum.game.wordfox.IVmethods;
import capsicum.game.wordfox.ImageHandler;
import capsicum.game.wordfox.NavigationBurger;
import capsicum.game.wordfox.R;
import capsicum.game.wordfox.screen_local_wifi.WifiActivityContract;
import capsicum.game.wordfox.screen_local_wifi.WifiGameInstance;
import capsicum.game.wordfox.screen_local_wifi.wifi_direct_service.WifiService;
import capsicum.game.wordfox.screen_local_wifi.wifi_direct_service.WifiServiceConnection;
import capsicum.game.wordfox.WordfoxConstants;
import capsicum.game.wordfox.database.FoxSQLData;
import capsicum.game.wordfox.screen_profile.ProfileActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.UUID;

import timber.log.Timber;

public class RoundnGameResults extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        WifiActivityContract,
        GameEndWifiContract,
        ResultsContract.View {
    public static final String INTENT_GAME_RESULTS = "intent_game_results_key";
    private WifiServiceConnection netConnService;
    private IntentFilter activityIntentFilter;
    boolean isOnline;
    private NavigationBurger navBurger = new NavigationBurger();
    private boolean backButtonPressedOnce = false;
    public Activity activity;
    private ResultsPresenter presenter;
    private ResultBroadcastReceiver resultReceiver;
    private Queue<PlayerResultPackage> wifiGameResults;
    private GameGridAdapter gameResultsAdapter;

    class ResultBroadcastReceiver extends BroadcastReceiver {
        private GameEndWifiContract gameEndWifiContract;

        ResultBroadcastReceiver(GameEndWifiContract gameEndWifiContact) {
            this.gameEndWifiContract = gameEndWifiContact;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Timber.d("L : Received action: " + intent.getAction());
            if (intent.getAction().equals(WifiService.ACTION_GAME_RESULTS)) {
                String resultMessage = intent.getExtras().getString(INTENT_GAME_RESULTS);
                try {
                    synchronized (RoundnGameResults.this) {
                        Timber.d("Received player data: " + resultMessage);
                        gameEndWifiContract.addReceivedWifiPlayerData(new JSONObject(resultMessage));
                    }
                    Timber.d("L : Received result intent. Result: " + new JSONObject(resultMessage).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            startActivity(new Intent(this, HomeScreen.class));
            return;
        }
        setContentView(R.layout.activity_end_screen_single_player1);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_end_screen);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                navigateToHome();
                finish();
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ArrayList<GameInstance> instancesToDisplay = HomeScreen.allGameInstances;
        isOnline = instancesToDisplay.get(0).isOnline();
        if (isOnline) {
            Timber.d("RE: Game is online!");
            activityIntentFilter = new IntentFilter();
            activityIntentFilter.addAction(WifiService.ACTION_GAME_RESULTS);
            netConnService = new WifiServiceConnection(this);
            resultReceiver = new ResultBroadcastReceiver(this);
            registerReceiver(resultReceiver, activityIntentFilter);
            activityIntentFilter = new IntentFilter();
        }

        presenter = new ResultsPresenter(this, HomeScreen.allGameInstances.size(), new FoxSQLData(this), instancesToDisplay, calculateScreenWidth());
        presenter.updateData();
        presenter.setupEndgameFox();
        presenter.setupBannerAd();
        presenter.setupBestwordHeader();
        presenter.setupResultSection();
    }

    private int calculateScreenWidth() {
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        return screenSize.x;
    }

    private void unBindService() {
        if (isOnline && netConnService.isBound) {
            Timber.d("Unbinding service in " + this.toString());
            unbindService(netConnService);
            netConnService.isBound = false;
        }
    }

    private void bindService() {
        if (isOnline) {
            Timber.d("Binding " + this.toString());
            bindService(new Intent(this, WifiService.class), netConnService,
                    Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isOnline) {
            bindService();
        }
    }

    @Override
    public void onServiceBound() {
        new Handler().post(() -> netConnService.getWifiService().declareGameOver());
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isOnline) {
            unBindService();
            // Try to unregister. It may already be unregistered previously
            try {
                unregisterReceiver(resultReceiver);
            } catch (IllegalArgumentException e) {
                Timber.d("Already unregistered");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void makeToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    public void navigateToHome() {
        if (isOnline) {
            netConnService.getWifiService().closeService();
            stopService(new Intent(RoundnGameResults.this, WifiService.class));
            unBindService();
        }
        Intent MainIntent = new Intent(this, HomeScreen.class);
        startActivity(MainIntent);
        finish();
    }

    @Override
    public void displayTitle(String title) {
        this.setTitle("Final Results");
    }

    @Override
    public GameData getPlayerData(UUID playerID) {
        return new GameData(this, playerID);
    }

    @Override
    public Bitmap getGameEndFoxBmp(int width) {
        return ImageHandler.getScaledBitmapByWidth(R.drawable.gameendsilcoloured, width, getResources());
    }

    @Override
    public Bitmap getGameEndFoxSpeechBmp(int width) {
        return ImageHandler.getScaledBitmapByWidth(R.drawable.speechbubbleleft, width, getResources());
    }

    // Display  the fox which tells the player who won the game
    @Override
    public void displayEndgameFox(Bitmap foxBitmap, Bitmap foxSpeechBitmap, String foxMessage) {
        ImageView instructionFoxIV = findViewById(R.id.content_game_end_screen_instructionFoxIV);
        instructionFoxIV.setImageBitmap(foxBitmap);
        ImageView instructionFoxSpeechBubbleIV = findViewById(R.id.content_game_end_screen_instructionFoxSpeechBubbleIV);
        instructionFoxSpeechBubbleIV.setImageBitmap(foxSpeechBitmap);
        // Set the textview width relative to the speech bubble imageview width
        TextView instructionFoxTV = findViewById(R.id.content_game_end_screen_instructionFoxTV);
        IVmethods.setWidthAsPercentOfLaidOutView(
                instructionFoxSpeechBubbleIV,
                instructionFoxTV,
                WordfoxConstants.TEXT_WIDTH_PERCENT_SPEECH_BUBBLE);
        instructionFoxTV.setText(foxMessage);
    }

    @Override
    public void displayBannerAd(String adUnit) {
        FrameLayout adviewContainer = findViewById(R.id.advert_container_end_game);
        AdView mAdView = new AdView(this);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.setAdSize(AdSize.BANNER);
        mAdView.setAdUnitId(adUnit);
        mAdView.loadAd(adRequest);
        adviewContainer.addView(mAdView);
    }

    @Override
    public void displayWordHeaders(String[] words, int width) {
        TextView word1TV = findViewById(R.id.bestword_heading_1);
        TextView word2TV = findViewById(R.id.bestword_heading_2);
        TextView word3TV = findViewById(R.id.bestword_heading_3);
        word1TV.setText(words[0]);
        word2TV.setText(words[1]);
        word3TV.setText(words[2]);
        word1TV.getLayoutParams().width = width;
        word2TV.getLayoutParams().width = width;
        word3TV.getLayoutParams().width = width;
    }

    @Override
    public Bitmap loadDefaultProfilePic(int size) {
        return ImageHandler.cropToSquare(
                ImageHandler.getScaledBitmapByShortestSide(GameData.PROFILE_DEFAULT_IMG,
                        size,
                        getResources()));
    }

    @Override
    public Bitmap getRankBmp(int imageResource, int width) {
        return ImageHandler.getScaledBitmapByLongestSide(imageResource, width, getResources());
    }

    @Override
    public synchronized void addReceivedWifiPlayerData(JSONObject newPlayerData) {
        GameDetails game = new WifiGameInstance(newPlayerData,
                HomeScreen.allGameInstances.get(0).getAllLongestPossible(),
                HomeScreen.allGameInstances.get(0).getLetters());
        if (gameResultsAdapter == null) {
            if (wifiGameResults == null) {
                wifiGameResults = new ArrayDeque<>();
            }
            wifiGameResults.add(presenter.prepareResultDetail(game));
        } else {
            gameResultsAdapter.newPlayer(presenter.prepareResultDetail(game));
        }
    }

    @Override
    public synchronized void prepareResultAdapter(List<PlayerResultPackage> players, List<String[]> gameLetters, int highestPossibleScore, int gridWidth, int spacerSize) {
        // Include any pending results that already arrived
        while (wifiGameResults != null && wifiGameResults.isEmpty()) {
            players.add(wifiGameResults.remove());
        }
        RecyclerView gridView = findViewById(R.id.gameend_results_rv);
        // spac of 1 for every fourth item (each player detail)
        int rowItemCount = WordfoxConstants.RESULT_GRIDS_PER_ROW;
        GridLayoutManager manager = new GridLayoutManager(this, rowItemCount);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int i) {
                // Every playerDetails item has a span of 1
                return (i % (rowItemCount + 1) == 0) ? manager.getSpanCount() : 1;
            }
        });
        gridView.setLayoutManager(manager);
        gameResultsAdapter = new GameGridAdapter(players, gameLetters, highestPossibleScore, gridWidth, getResources());
        gridView.setAdapter(gameResultsAdapter);
        gridView.addItemDecoration(new PlayerDetailsItemDecoration(spacerSize));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (this.backButtonPressedOnce) {
                navigateToHome();
                return;
            }
            Toast.makeText(this, "Double tap BACK to exit!", Toast.LENGTH_SHORT).show();
            this.backButtonPressedOnce = true;
            new Handler().postDelayed(() -> backButtonPressedOnce = false, 1500);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                // User chose the "Profile" item, jump to the profile page
                Intent profileScreenIntent = new Intent(RoundnGameResults.this, ProfileActivity.class);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        navBurger.navigateTo(item, RoundnGameResults.this);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
