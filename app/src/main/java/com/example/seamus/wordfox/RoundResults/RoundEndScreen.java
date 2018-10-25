package com.example.seamus.wordfox.RoundResults;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.GameInstance;
import com.example.seamus.wordfox.GridImage;
import com.example.seamus.wordfox.HomeScreen;
import com.example.seamus.wordfox.ImageHandler;
import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.SwapActivity;
import com.example.seamus.wordfox.WifiService;
import com.example.seamus.wordfox.WifiServiceConnection;
import com.example.seamus.wordfox.database.FoxSQLData;
import com.example.seamus.wordfox.game_screen.GameActivity;
import com.example.seamus.wordfox.player_switch.PlayerSwitchActivity;
import com.example.seamus.wordfox.results_screen.ResultsContract;
import com.example.seamus.wordfox.results_screen.ResultsPresenter;
import com.example.seamus.wordfox.results_screen.RoundnGameResults;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


public class RoundEndScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        RoundEndContract.View {

    public static final String MONITOR_TAG = "myTag";
    private RoundEndPresenter presenter;
    private int gameIndexNumber;
    private WifiServiceConnection netConnService;
    boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_end_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gameIndexNumber = getIntent().getExtras().getInt(GameActivity.GAME_INDEX);

        // TODO: Separate presenter for round and game end
        presenter = new RoundEndPresenter(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_round_end);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.startGame(HomeScreen.allGameInstances.get(gameIndexNumber));
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

        TextView resultPlayerNameView = cl.findViewById(R.id.round_end_result_player_name);
        String playerName = gameInstance.getName();
        resultPlayerNameView.setText(playerName);
        TextView roundEndPercent = cl.findViewById(R.id.round_end_result_percent);
        String playerPercent = "  (" + percentScore + "%)";
        roundEndPercent.setText(playerPercent);
        TextView resultPlayerScoreView = cl.findViewById(R.id.round_end_result_player_score);
        String playerResult = playerScore + " out of " + maxScore;
        resultPlayerScoreView.setText(playerResult);
        TextView longestWordView = cl.findViewById(R.id.round_end_longest_word);
        String longestWordHeader = getResources().getString(R.string.your_longest_word_was) + " " + gameInstance.getLongestWord();
        longestWordView.setText(longestWordHeader);

        Bitmap gridBmp = BitmapFactory.decodeResource(getResources(), R.drawable.letter_grid_blank);
        gridBmp = ImageHandler.getResizedBitmap(gridBmp, ImageHandler.dp2px(this, 100), ImageHandler.dp2px(this, 100));  // TODO: Adjust to screen size

        GridImage gridWithText = new GridImage(gridBmp, gameInstance.getLongestWord().toUpperCase(), gameInstance.getRoundLetters(), getResources().getColor(R.color.game_font_color), getResources().getColor(R.color.colorLightAccent));
        ImageView roundEndGridBest = cl.findViewById(R.id.round_end_result_grid);
        roundEndGridBest.setImageBitmap(gridWithText.getBmp());

        CircleImageView profilePicView = cl.findViewById(R.id.round_end_profile_pic);
        if (profPic == null) {
            profPic = ImageHandler.getScaledBitmap(R.drawable.ppfox2_outline, 120, getResources());
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
                String word = possibleWords.get(count).toUpperCase();
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.round_end_screen, menu);
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
