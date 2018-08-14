package com.example.seamus.wordfox.results_screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seamus.wordfox.FoxUtils;
import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.GameInstance;
import com.example.seamus.wordfox.MainActivity;
import com.example.seamus.wordfox.NavigationBurger;
import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.database.FoxSQLData;
import com.example.seamus.wordfox.game_screen.GameActivity;
import com.example.seamus.wordfox.player_switch.PlayerSwitchActivity;
import com.example.seamus.wordfox.profile.ProfileActivity;

import java.util.ArrayList;

public class RoundnGameResults extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ResultsContract.View {

    private NavigationBurger navBurger = new NavigationBurger();

    public static final String MONITOR_TAG = "EndScreen: ";
    private static final String TAG = "RoundnGameResults";
    private boolean backButtonPressedOnce = false;
    public Activity activity;

    private int gameIndexNumber;
    private ResultsPresenter presenter;

    private LinearLayout resultsLL;
    private LinearLayout wordsLL;
    private LinearLayout resultSectionEndScreenLL;
    private LinearLayout.LayoutParams lp;
    private String p1Name = "";
    Button endOfRoundOrGameButton;

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

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String roundOrGameEnd = getIntent().getExtras().getString("key");
        boolean gameOver = (roundOrGameEnd.equals("game"));
        gameIndexNumber = getIntent().getExtras().getInt("gameIndexNumber");

        lp = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT); // width is first then height
        resultsLL = (LinearLayout) findViewById(R.id.resultEndScreenLL);
        wordsLL = (LinearLayout) findViewById(R.id.wordsEndScreenLL);
        resultSectionEndScreenLL = (LinearLayout) findViewById(R.id.resultSectionEndScreenLL);

        endOfRoundOrGameButton = (Button) findViewById(R.id.endOfRoundOrGameButton);

        ArrayList<GameInstance> instancesToDisplay = new ArrayList<>();
        if (gameOver) {
            instancesToDisplay.addAll(MainActivity.allGameInstances);
        } else {
            instancesToDisplay.add(MainActivity.allGameInstances.get(gameIndexNumber));
        }
        presenter = new ResultsPresenter(this, gameOver, MainActivity.allGameInstances.size(), new FoxSQLData(this), instancesToDisplay);
        presenter.populateHeaderMsg();
        presenter.populateResultLL();
        presenter.endOfRoundOrGameResults();
        presenter.createRoundSummary();
        presenter.updateData();
    }

    @Override
    public void setGameOverMessage(String gameOverMessage) {
        TextView gameOverTV = (TextView) findViewById(R.id.gameOverEndScreenTV);
        gameOverTV.setText(gameOverMessage);
    }

    @Override
    public void makeToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setVictoryMessage(String victoryMessage) {
        TextView winnerTV = new TextView(this);
        winnerTV.setText(victoryMessage);
        winnerTV.setGravity(Gravity.CENTER);
        //add the textview with the winner's name into the LinearLayout containing the Game Over message
        LinearLayout gameOverWinnerLL = findViewById(R.id.gameOverWinnerLLEndScreen);
        winnerTV.setLayoutParams(lp);
        gameOverWinnerLL.addView(winnerTV);
    }

    @Override
    public void prepareHomeButton() {
        endOfRoundOrGameButton.setText(R.string.home_button_text);
        endOfRoundOrGameButton.setOnClickListener(v -> {
            Intent MainIntent = new Intent(v.getContext(), MainActivity.class);
            startActivity(MainIntent);
        });
    }

    @Override
    public void displayTitle(String title) {
        this.setTitle("Final Results");
    }

    @Override
    public void prepareContinueButton() {
        endOfRoundOrGameButton.setText(R.string.next_button_text);
        endOfRoundOrGameButton.setOnClickListener(v -> {
            presenter.startGame(MainActivity.allGameInstances.get(gameIndexNumber));    // TODO: This is always item 0 in presenter gameinstances??
        });
    }

    @Override
    public void addTVtoResults(String result) {
        TextView resultsTV = new TextView(this);
        resultsTV.setText(result);
        resultsTV.setLayoutParams(lp);
        resultsLL.addView(resultsTV);
    }

    @Override
    public void addResultHeading(String resultTitle) {
        TextView resultTV = new TextView(this);
        resultTV.setLayoutParams(lp);
        resultTV.setText(resultTitle);
        resultSectionEndScreenLL.addView(resultTV);
    }

    @Override
    public void addResultValue(String resultContent) {
        addResultValue(resultContent, null);
    }

    @Override
    public void addResultValue(String resultContent, String description) {
        TextView resultTV = new TextView(this);
        resultTV.setLayoutParams(lp);
        resultTV.setText(resultContent);
        if (description != null) {
            resultTV.setContentDescription(description);
        }
        wordsLL.addView(resultTV);
    }

    //  Margin of 50DP between each section
    @Override
    public void addResultSpacer() {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, FoxUtils.dp2px(this, 50), 0, 0);
        TextView spacerTV = new TextView(this);
        spacerTV.setLayoutParams(params);
        resultSectionEndScreenLL.addView(spacerTV);
        // Cant add same view to two parents
        spacerTV = new TextView(this);
        spacerTV.setLayoutParams(params);
        wordsLL.addView(spacerTV);
    }

    @Override
    public void nextRound(int gameIndex) {
        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra("game_index", gameIndexNumber);
        startActivity(gameIntent);
    }

    @Override
    public boolean playerSwitch() {
        // TODO: next game will always be thisGameIndex + 1 ?? Why need to search? Check if 'current index' == 'player count' to end the game
        for (int index = 0; index < MainActivity.allGameInstances.size(); index++) {
            if (MainActivity.allGameInstances.get(index).isGameOngoing()) {
                playerSwitch(index);
                return true;
            }
        }
        return false;
    }

    @Override
    public void playerSwitch(int index) {
        Intent gameIntent = new Intent(this, PlayerSwitchActivity.class);
        gameIntent.putExtra("game_index", index);
        startActivity(gameIntent);
    }

    @Override
    public void proceedToFinalResults(int gameIndex) {
        Intent EndScreenIntent = new Intent(this, RoundnGameResults.class);
        Bundle endScreenBundle = new Bundle();
        endScreenBundle.putString("key", "game");
        endScreenBundle.putInt("gameIndexNumber", gameIndex);
        EndScreenIntent.putExtras(endScreenBundle);
        startActivity(EndScreenIntent);
    }

    @Override
    public String defaultP1Name() {
        if(p1Name.equals("")){
            GameData fox = new GameData(this, GameData.DEFAULT_P1_NAME);
            p1Name = fox.getUsername();
        }
        return p1Name;
    }

    @Override
    public GameData getPlayerData(String playerID) {
        return new GameData(this, playerID);
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
