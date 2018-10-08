package com.example.seamus.wordfox.RoundResults;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.seamus.wordfox.ImageHandler;
import com.example.seamus.wordfox.MainActivity;
import com.example.seamus.wordfox.R;
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
        ResultsContract.View {

    public static final String MONITOR_TAG = "myTag";
    private ResultsPresenter presenter;
    private int gameIndexNumber;
    private WifiServiceConnection netConnService;
    private IntentFilter activityIntentFilter;
    boolean isOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_end_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        String roundOrGameEnd = getIntent().getExtras().getString("key");
        boolean gameOver = (roundOrGameEnd.equals("game"));     // TODO: No longer relevant .. ?
        gameIndexNumber = getIntent().getExtras().getInt(GameActivity.GAME_INDEX);

        ArrayList<GameInstance> instancesToDisplay = new ArrayList<>();
        if (gameOver) {
            instancesToDisplay.addAll(MainActivity.allGameInstances);
        } else {
            instancesToDisplay.add(MainActivity.allGameInstances.get(gameIndexNumber));
        }
        // TODO: Separate presenter for round and game end
        presenter = new ResultsPresenter(this, gameOver, MainActivity.allGameInstances.size(), new FoxSQLData(this), instancesToDisplay);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab_round_end);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                presenter.startGame(MainActivity.allGameInstances.get(gameIndexNumber));
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        populatePlayerDetails(MainActivity.allGameInstances.get(gameIndexNumber));
        populatePossibleWords(MainActivity.allGameInstances.get(gameIndexNumber));

        isOnline = instancesToDisplay.get(0).isOnline();
        if (isOnline) {
            Log.d(GameActivity.MONITOR_TAG, "RE: Game is online!");
            activityIntentFilter = new IntentFilter();
            activityIntentFilter.addAction(WifiService.ACTION_SEND_LETTERS);
            netConnService = new WifiServiceConnection();
            activityIntentFilter = new IntentFilter();
//            sendBroadcast(presenter.getLettersSTR());
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isOnline) {
            bindService(new Intent(this, WifiService.class), netConnService,
                    Context.BIND_AUTO_CREATE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isOnline && netConnService.isBound) {
            unbindService(netConnService);      // TODO: ... necessary?? Kills it anyway ..
            netConnService.isBound = false;
        }
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

//        int scaleFactor = ImageHandler.getScaleFactor(getResources(), R.drawable.letter_grid_blank, 200);
//        Bitmap testProf = ImageHandler.getScaledBitmap(R.drawable.letter_grid_blank, getResources());
//        int d = 7;  // ??
        Bitmap gridBmp = BitmapFactory.decodeResource(getResources(), R.drawable.letter_grid_blank);
        gridBmp = ImageHandler.getResizedBitmap(gridBmp, ImageHandler.dp2px(this, 100), ImageHandler.dp2px(this, 100));  // TODO: Adjust to screen size

        GridImage gridWithText = new GridImage(gridBmp, gameInstance.getLongestWord().toUpperCase(), gameInstance.getRoundLetters(), getResources().getColor(R.color.game_font_color), getResources().getColor(R.color.colorLightAccent));
        ImageView roundEndGridBest = cl.findViewById(R.id.round_end_result_grid);
        roundEndGridBest.setImageBitmap(gridWithText.getBmp());

        CircleImageView profilePicView = cl.findViewById(R.id.round_end_profile_pic);
        if (profPic == null) {
            profilePicView.setVisibility(View.GONE);
            resultPlayerNameView.setPadding(ImageHandler.dp2px(this, 20), ImageHandler.dp2px(this, 10), 10, 10);
            resultPlayerScoreView.setPadding(ImageHandler.dp2px(this, 20), 10, 10, ImageHandler.dp2px(this, 10));
        } else {
            profilePicView.setImageBitmap(profPic);
        }
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
    public void nextRound(int gameIndex) {
        gameProceed(GameActivity.class, gameIndexNumber);
    }

    @Override
    public void playerSwitch(int gameIndex) {
        gameProceed(PlayerSwitchActivity.class, gameIndexNumber + 1);
    }

    private void gameProceed(Class nextActivity, int index) {
        Intent gameIntent = new Intent(this, nextActivity);
        gameIntent.putExtra(GameActivity.GAME_INDEX, index);
        startActivity(gameIntent);
    }

    @Override
    public boolean playerSwitch() {
        for (int index = 0; index < MainActivity.allGameInstances.size(); index++) {
            if (MainActivity.allGameInstances.get(index).isGameOngoing()) {
                playerSwitch(index);
                return true;
            }
        }
        return false;
    }

    @Override
    public void proceedToFinalResults(int gameIndex) {
        Intent EndScreenIntent = new Intent(this, RoundnGameResults.class);
        Bundle endScreenBundle = new Bundle();
        endScreenBundle.putString("key", "game");
        endScreenBundle.putInt(GameActivity.GAME_INDEX, gameIndex);
        EndScreenIntent.putExtras(endScreenBundle);
        startActivity(EndScreenIntent);
    }

    @Override
    public GameData getPlayerData(UUID playerID) {
        return null;
    }
}
