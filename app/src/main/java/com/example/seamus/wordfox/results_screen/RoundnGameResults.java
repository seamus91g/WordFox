package com.example.seamus.wordfox.results_screen;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seamus.wordfox.FoxUtils;
import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.GameInstance;
import com.example.seamus.wordfox.GridImage;
import com.example.seamus.wordfox.ImageHandler;
import com.example.seamus.wordfox.MainActivity;
import com.example.seamus.wordfox.NavigationBurger;
import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.database.FoxSQLData;
import com.example.seamus.wordfox.game_screen.GameActivity;
import com.example.seamus.wordfox.player_switch.PlayerSwitchActivity;
import com.example.seamus.wordfox.profile.FoxRank;
import com.example.seamus.wordfox.profile.ProfileActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class RoundnGameResults extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        ResultsContract.View {
    private static final String DEFAULT_PROFILE_IMAGE_ASSET = "default_profile_smiley.png";
    private static final int MAX_RESOLUTION_IMAGE = 2048;   // Max allowed picture resolution

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

    LayoutInflater resultInflater;

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

        String roundOrGameEnd = getIntent().getExtras().getString("key");
        boolean gameOver = (roundOrGameEnd.equals("game"));
        gameIndexNumber = getIntent().getExtras().getInt("gameIndexNumber");

        lp = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT); // width is first then height
//        resultsLL = (LinearLayout) findViewById(R.id.resultEndScreenLL);
//        wordsLL = (LinearLayout) findViewById(R.id.wordsEndScreenLL);
//        resultSectionEndScreenLL = (LinearLayout) findViewById(R.id.resultSectionEndScreenLL);
//
//        endOfRoundOrGameButton = (Button) findViewById(R.id.endOfRoundOrGameButton);

        ArrayList<GameInstance> instancesToDisplay = new ArrayList<>();
        if (gameOver) {
            instancesToDisplay.addAll(MainActivity.allGameInstances);
        } else {
            instancesToDisplay.add(MainActivity.allGameInstances.get(gameIndexNumber));
        }
        presenter = new ResultsPresenter(this, gameOver, MainActivity.allGameInstances.size(), new FoxSQLData(this), instancesToDisplay);
//        presenter.populateHeaderMsg();
//        presenter.populateResultLL();
//        presenter.endOfRoundOrGameResults();
//        presenter.createRoundSummary();
        presenter.updateData();


        /////////
        LinearLayout resultContainer = findViewById(R.id.player_results_container);
        resultInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        for (int i = 0; i < MainActivity.allGameInstances.size(); ++i) {
            resultContainer.addView(inflatePlayerResult(MainActivity.allGameInstances.get(i)));
        }
        GameInstance gameInstance = MainActivity.allGameInstances.get(0);
        TextView best1 = findViewById(R.id.bestword_heading_1);
        TextView best2 = findViewById(R.id.bestword_heading_2);
        TextView best3 = findViewById(R.id.bestword_heading_3);
        best1.setText(gameInstance.getRoundLongestPossible(0).toUpperCase());
        best2.setText(gameInstance.getRoundLongestPossible(1).toUpperCase());
        best3.setText(gameInstance.getRoundLongestPossible(2).toUpperCase());
        String winner = "";
        int highScore = -1;
        for (int i = 0; i < MainActivity.allGameInstances.size(); ++i) {
            if (MainActivity.allGameInstances.get(i).getTotalScore() > highScore) {
                highScore = MainActivity.allGameInstances.get(i).getTotalScore();
                winner = MainActivity.allGameInstances.get(i).getPlayerID();
            }
        }
        TextView winnerText = findViewById(R.id.winner_banner_text);
        winner = "Winner is " + winner + "!";
        winnerText.setText(winner);
    }

    private LinearLayout inflatePlayerResult(GameInstance gameInstance) {
        LinearLayout cl = (LinearLayout) resultInflater.inflate(R.layout.result_player_layout, null);

        GameData plyrGd = new GameData(this, gameInstance.getPlayerName());

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
        String playerName = gameInstance.getPlayerID() + "  (" + percentScore + "%)";
        resultPlayerNameView.setText(playerName);
        TextView resultPlayerScoreView = cl.findViewById(R.id.result_player_score);
        String playerResult = playerScore + " out of " + maxScore;
        resultPlayerScoreView.setText(playerResult);

        ImageView resultPlayerFoxPicView = cl.findViewById(R.id.result_player_fox_pic);
        TextView resultPlayerRankNameView = cl.findViewById(R.id.result_player_rank_name);
        int rank = GameData.determineRankValue(playerScore);
        plyrGd.setRank(rank);
        FoxRank foxRank = GameData.determineRankClass(rank);
        resultPlayerRankNameView.setText(foxRank.foxRank);


        CircleImageView profilePicView = cl.findViewById(R.id.results_screen_profile_pic);
        if (profPic == null) {
            profilePicView.setVisibility(View.GONE);
            resultPlayerNameView.setPadding(ImageHandler.dp2px(this, 20), ImageHandler.dp2px(this, 10), 10, 10);
            resultPlayerScoreView.setPadding(ImageHandler.dp2px(this, 20), 10, 10, ImageHandler.dp2px(this, 10));
        } else {
            profilePicView.setImageBitmap(profPic);
        }
        profPic = null;
        Bitmap foxRankImage = ImageHandler.getScaledBitmap(foxRank.imageResource, 120, getResources());
//        Bitmap foxRankImage = BitmapFactory.decodeResource(getResources(), foxRank.imageResource);      // TODO: Re-use fox pics if players get the same rank
        foxRankImage = ImageHandler.getResizedBitmap(foxRankImage, ImageHandler.dp2px(this, 60), ImageHandler.dp2px(this, 60));  // TODO: Adjust to screen size
        resultPlayerFoxPicView.setImageBitmap(foxRankImage);
        for (int i = 0; i < GameInstance.NUMBER_ROUNDS; ++i) {
            String gridTag = "player_grid" + (i + 1);
            String wordTag = "player_word" + (i + 1);
            ImageView gridImage = cl.getChildAt(0).findViewWithTag(gridTag);
            String bestWord = gameInstance.getRoundWord(i);
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
        Bitmap buttongGridImage = BitmapFactory.decodeResource(getResources(), R.drawable.letter_grid_blank);
        buttongGridImage = ImageHandler.getResizedBitmap(buttongGridImage, ImageHandler.dp2px(this, 100), ImageHandler.dp2px(this, 100));  // TODO: Adjust to screen size

        GridImage grid = new GridImage(buttongGridImage, word, letters, getResources().getColor(R.color.game_font_color), getResources().getColor(R.color.colorLightAccent));
        return grid.getBmp();
    }


    @Override
    public void setGameOverMessage(String gameOverMessage) {
//        TextView gameOverTV = (TextView) findViewById(R.id.gameOverEndScreenTV);
//        gameOverTV.setText(gameOverMessage);
    }

    @Override
    public void makeToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setVictoryMessage(String victoryMessage) {
//        TextView winnerTV = new TextView(this);
//        winnerTV.setText(victoryMessage);
//        winnerTV.setGravity(Gravity.CENTER);
//        //add the textview with the winner's name into the LinearLayout containing the Game Over message
//        LinearLayout gameOverWinnerLL = findViewById(R.id.gameOverWinnerLLEndScreen);
//        winnerTV.setLayoutParams(lp);
//        gameOverWinnerLL.addView(winnerTV);
    }

    @Override
    public void prepareHomeButton() {
        endOfRoundOrGameButton.setText(R.string.home_button_text);
        endOfRoundOrGameButton.setOnClickListener(v -> {
            navigateToHome();
        });
    }

    public void navigateToHome(){
        Intent MainIntent = new Intent(this, MainActivity.class);
        startActivity(MainIntent);
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
        if (p1Name.equals("")) {
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
