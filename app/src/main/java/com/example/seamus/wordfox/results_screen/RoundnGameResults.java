package com.example.seamus.wordfox.results_screen;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.GameDetails;
import com.example.seamus.wordfox.GameInstance;
import com.example.seamus.wordfox.GridImage;
import com.example.seamus.wordfox.HomeScreen;
import com.example.seamus.wordfox.ImageHandler;
import com.example.seamus.wordfox.NavigationBurger;
import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.SwapActivity;
import com.example.seamus.wordfox.WifiGameInstance;
import com.example.seamus.wordfox.WifiService;
import com.example.seamus.wordfox.WifiServiceConnection;
import com.example.seamus.wordfox.database.FoxSQLData;
import com.example.seamus.wordfox.game_screen.GameActivity;
import com.example.seamus.wordfox.profile.FoxRank;
import com.example.seamus.wordfox.profile.ProfileActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Queue;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class RoundnGameResults extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ResultsContract.View {
    private static final int MAX_RESOLUTION_IMAGE = 2048;   // Max allowed picture resolution
    public static final String INTENT_GAME_RESULTS = "intent_game_results_key";

    private WifiServiceConnection netConnService;
    private IntentFilter activityIntentFilter;
    boolean isOnline;

    private NavigationBurger navBurger = new NavigationBurger();

    public static final String MONITOR_TAG = "myTag";
    private static final String TAG = "RoundnGameResults";
    private boolean backButtonPressedOnce = false;
    public Activity activity;

    private ResultsPresenter presenter;
    private LinearLayout resultContainer;

    private AdView mAdView;
    private LayoutInflater resultInflater;
    private ResultBroadcastReceiver resultReceiver;
    private Queue<JSONObject> wifiGameResults;
    private Bitmap defaultProfilePic;

    class ResultBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(MONITOR_TAG, "L : Received any ol thing : " + intent.getAction());
            if (intent.getAction().equals(WifiService.ACTION_GAME_RESULTS)) {
                String resultMessage = intent.getExtras().getString(INTENT_GAME_RESULTS);
                try {
                    synchronized (RoundnGameResults.this) {
                        wifiGameResults.add(new JSONObject(resultMessage));
                    }
                    Log.d(MONITOR_TAG, "L : Received result intent. Result: " + new JSONObject(resultMessage).toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            }
        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        AdRequest adRequestTest = new AdRequest.Builder()
                .addTestDevice("16930B084D136C6BEFB468B4D1F2919C")
                .build();
//        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView = findViewById(R.id.adViewEndGame);
        mAdView.loadAd(adRequestTest);

        ArrayList<GameInstance> instancesToDisplay = HomeScreen.allGameInstances;

        isOnline = instancesToDisplay.get(0).isOnline();
        if (isOnline) {
            Log.d(GameActivity.MONITOR_TAG, "RE: Game is online!");
            activityIntentFilter = new IntentFilter();
            activityIntentFilter.addAction(WifiService.ACTION_GAME_RESULTS);
            wifiGameResults = new ArrayDeque<>();
            netConnService = new WifiServiceConnection();
            resultReceiver = new ResultBroadcastReceiver();
            registerReceiver(resultReceiver, activityIntentFilter);
            activityIntentFilter = new IntentFilter();
            new Thread(wifiResultFeed).start();
        }

        presenter = new ResultsPresenter(this, HomeScreen.allGameInstances.size(), new FoxSQLData(this), instancesToDisplay);
        presenter.updateData();

        /////////
        resultContainer = findViewById(R.id.player_results_container);
        resultInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < HomeScreen.allGameInstances.size(); ++i) {
            addResultDetail(HomeScreen.allGameInstances.get(i));
        }
        GameInstance gameInstance = HomeScreen.allGameInstances.get(0);
        TextView best1 = findViewById(R.id.bestword_heading_1);
        TextView best2 = findViewById(R.id.bestword_heading_2);
        TextView best3 = findViewById(R.id.bestword_heading_3);
        best1.setText(gameInstance.getRoundLongestPossible(0).toUpperCase() + "\n(" + gameInstance.getRoundLongestPossible(0).length() + ")" );
        best2.setText(gameInstance.getRoundLongestPossible(1).toUpperCase()+ "\n(" + gameInstance.getRoundLongestPossible(1).length() + ")" );
        best3.setText(gameInstance.getRoundLongestPossible(2).toUpperCase()+ "\n(" + gameInstance.getRoundLongestPossible(2).length() + ")" );
        String winner = "";

        if(HomeScreen.allGameInstances.size()>1) {
            int highScore = -1;
            for (int i = 0; i < HomeScreen.allGameInstances.size(); ++i) {
                if (HomeScreen.allGameInstances.get(i).getTotalScore() > highScore) {
                    highScore = HomeScreen.allGameInstances.get(i).getTotalScore();
                    winner = HomeScreen.allGameInstances.get(i).getName();
                }
            }
            winner = "Winner is " + winner + "!" + "\n" + "You scored\n" + highScore + " out of " + HomeScreen.allGameInstances.get(0).getHighestPossibleScore();
        } else{
            winner = "GAME OVER!\n" + "You scored\n" + HomeScreen.allGameInstances.get(0).getTotalScore() + " out of " + HomeScreen.allGameInstances.get(0).getHighestPossibleScore();
        }

        TextView winnerText = findViewById(R.id.winner_banner_text);
        winnerText.setText(winner);









//        int maxScore = gameInstance.getLongestPossible().length();
//        int playerScore = gameInstance.getScore();
//        int percentScore = (100 * playerScore) / (maxScore);
//
//        String playerPercent = "  (" + percentScore + "%)";
//
//        TextView resultPlayerNameView = cl.findViewById(R.id.round_end_result_player_name);
//        String playerName = gameInstance.getName();
//        resultPlayerNameView.setText(playerName + playerPercent);
//
//        TextView resultPlayerScoreView = cl.findViewById(R.id.round_end_result_best_word);
//        resultPlayerScoreView.setText(gameInstance.getLongestWord());
//
//
//        String playerResult = playerScore + " out of " + maxScore;
//        TextView longestWordView = cl.findViewById(R.id.round_end_longest_word);
//        String longestWordHeader = getResources().getString(R.string.you_scored) + "\n" + playerResult  ;
//        longestWordView.setText(longestWordHeader);

















        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        ImageView myIV = findViewById(R.id.winner_banner);
        myIV.setImageBitmap(ImageHandler.getScaledBitmap(R.drawable.gameendwithspeech, (int) (0.35*width),getResources()));



        TextView word1TV = findViewById(R.id.bestword_heading_1);
        TextView word2TV = findViewById(R.id.bestword_heading_2);
        TextView word3TV = findViewById(R.id.bestword_heading_3);
        word1TV.requestLayout();
        word1TV.getLayoutParams().width = (int) width/3;
        word2TV.requestLayout();
        word2TV.getLayoutParams().width = (int) width/3;
        word3TV.requestLayout();
        word3TV.getLayoutParams().width = (int) width/3;


    }

    public static int dpToPx(float dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }
    public static int dpToSp(float dp, Context context) {
        return (int) (dpToPx(dp, context) / context.getResources().getDisplayMetrics().scaledDensity);
    }

    private void unBindService() {
        if (isOnline && netConnService.isBound) {
            Log.d(MONITOR_TAG, "Unbinding service in " + this.toString());
            unbindService(netConnService);
            netConnService.isBound = false;
        }
    }

    private void bindService() {
        if (isOnline) {
            Log.d(MONITOR_TAG, "Binding " + this.toString());
            bindService(new Intent(this, WifiService.class), netConnService,
                    Context.BIND_AUTO_CREATE);
        }
    }

    private void addResultDetail(GameDetails game) {
        resultContainer.addView(inflatePlayerResult(game));
    }

    private Runnable wifiResultFeed = new Runnable() {
        @Override
        public void run() {
            // TODO: End loop when results received equals player count
            while (true) {
                if (!wifiGameResults.isEmpty()) {
                    runOnUiThread(wifiResultsToUI);
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };
    private Runnable wifiResultsToUI = new Runnable() {
        @Override
        public void run() {
            JSONObject gameResult;
            synchronized (RoundnGameResults.this) {
                // If UI thread is slow runnable may have been triggered more times than there
                // are elements, so check if empty
                if (wifiGameResults.isEmpty()) {
                    return;
                }
                gameResult = wifiGameResults.remove();
            }
            GameDetails game = new WifiGameInstance(gameResult,
                    HomeScreen.allGameInstances.get(0).getAllLongestPossible(),
                    HomeScreen.allGameInstances.get(0).getLetters());
            addResultDetail(game);
        }
    };

    private LinearLayout inflatePlayerResult(GameDetails gameInstance) {
        LinearLayout cl = (LinearLayout) resultInflater.inflate(R.layout.result_player_layout, null);

        GameData plyrGd = new GameData(this, gameInstance.getID());

        String profPicStr = plyrGd.getProfilePicture();
        Bitmap profPic = null;
        ImageHandler imageHandler = new ImageHandler(this);     // Handle this better
        if (!profPicStr.equals("")) {
            Uri myFileUri = Uri.parse(profPicStr);
            profPic = imageHandler.getBitmapFromUri(myFileUri, ImageHandler.dp2px(this, 60));
        }
        int maxScore = gameInstance.getHighestPossibleScore();
        int playerScore = gameInstance.getTotalScore();
        int percentScore = (100 * playerScore) / (maxScore);

        TextView resultPlayerNameView = cl.findViewById(R.id.result_player_name);
        String playerName = gameInstance.getName() + "  (" + percentScore + "%)";
        resultPlayerNameView.setText(playerName);
        TextView resultPlayerScoreView = cl.findViewById(R.id.result_player_score);

        if(HomeScreen.allGameInstances.size()>1) {
//
            String playerResult = playerScore + " out of " + maxScore;
            resultPlayerScoreView.setText(playerResult);
        }else{

            resultPlayerScoreView.setVisibility(View.GONE);
        }




        ImageView resultPlayerFoxPicView = cl.findViewById(R.id.result_player_fox_pic);
        TextView resultPlayerRankNameView = cl.findViewById(R.id.result_player_rank_name);
        FoxRank foxRank = GameData.determineRankValue(playerScore);
        resultPlayerRankNameView.setText(foxRank.foxRank);

        CircleImageView profilePicView = cl.findViewById(R.id.results_screen_profile_pic);
        if (profPic == null) {
            if(defaultProfilePic == null){
                defaultProfilePic = ImageHandler.getScaledBitmap(GameData.PROFILE_DEFAULT_IMG, 120, getResources());
            }
            profPic = defaultProfilePic;
        }
        profilePicView.setImageBitmap(profPic);
        profPic = null;
        Bitmap foxRankImage = ImageHandler.getScaledBitmap(foxRank.imageResource, 120, getResources());    // TODO: Adjust to screen size
//        Bitmap foxRankImage = BitmapFactory.decodeResource(getResources(), foxRank.imageResource);      // TODO: Re-use fox pics if players get the same rank
//        foxRankImage = ImageHandler.getResizedBitmap(foxRankImage, ImageHandler.dp2px(this, 60), ImageHandler.dp2px(this, 60));
        resultPlayerFoxPicView.setImageBitmap(foxRankImage);
        for (int i = 0; i < GameInstance.NUMBER_ROUNDS; ++i) {
            String gridTag = "player_grid" + (i + 1);
            String wordTag = "player_word" + (i + 1);
            ImageView gridImage = cl.getChildAt(0).findViewWithTag(gridTag);
            String bestWord = gameInstance.getRoundWord(i) + " (" + gameInstance.getRoundWord(i).length() + ")";
            String letters = gameInstance.getLetters(i);
            TextView wordTV = cl.getChildAt(0).findViewWithTag(wordTag);
            wordTV.setText(bestWord);
            gridImage.setImageBitmap(pressedKey(letters, bestWord));
        }
        return cl;
    }

    private Bitmap pressedKey(String letters, String word) {
        if (letters.equals(GameData.NONE_FOUND)) {    // TODO: Throw exception
            return null;
        }
        if (word.equals(GameData.NONE_FOUND)) {
            return null;
        }
        Bitmap buttongGridImage = ImageHandler.getScaledBitmap(R.drawable.letter_grid_blank, ImageHandler.dp2px(this, 100), getResources());
        GridImage grid = new GridImage(buttongGridImage, word, letters, getResources().getColor(R.color.game_font_color), getResources().getColor(R.color.colorLightAccent));
        return grid.getBmp();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isOnline) {
            bindService();
            new Handler().post(() -> netConnService.getWifiService().declareGameOver());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isOnline) {
            unBindService();
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
        }
        Intent MainIntent = new Intent(this, HomeScreen.class);
        startActivity(MainIntent);
    }

    @Override
    public void displayTitle(String title) {
        this.setTitle("Final Results");
    }

    @Override
    public void playerSwitch(int index) {
        Intent gameIntent = new Intent(this, SwapActivity.class);
        gameIntent.putExtra(GameActivity.GAME_INDEX, index);
        startActivity(gameIntent);
    }

    @Override
    public GameData getPlayerData(UUID playerID) {
        return new GameData(this, playerID);
    }

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
                Log.d(MONITOR_TAG, "Chose des's profile icon, END");
                Intent profileScreenIntent = new Intent(RoundnGameResults.this, ProfileActivity.class);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.

        Log.d(MONITOR_TAG, "Before_onNavigationItemSelected__MainActivity");
        navBurger.navigateTo(item, RoundnGameResults.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
