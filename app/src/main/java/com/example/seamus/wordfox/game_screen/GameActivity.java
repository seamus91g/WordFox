package com.example.seamus.wordfox.game_screen;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.seamus.wordfox.FoxUtils;
import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.GameInstance;
import com.example.seamus.wordfox.GameTimer;
import com.example.seamus.wordfox.MainActivity;
import com.example.seamus.wordfox.NavigationBurger;
import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.database.FoxSQLData;
import com.example.seamus.wordfox.results_screen.RoundnGameResults;
import com.example.seamus.wordfox.injection.DictionaryApplication;
import com.example.seamus.wordfox.profile.ProfileActivity;

import java.util.ArrayList;

public class GameActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, GamescreenContract.View {
    public static int GAME_TIME_SECONDS = 30;
    public static final String MONITOR_TAG = "myTag";
    private NavigationBurger navBurger = new NavigationBurger();
    private GameTimer myGameTimerInstance;
    private GamescreenPresenter presenter;
    private LinearLayout timeBlock;
    private ArrayList<TextView> secondsBlocks;
    private LinearLayout gridLayout;
    private boolean backButtonPressedOnce = false;
    private boolean resetButtonPressedOnce = false;
    private boolean gameInFocus = true;
    private boolean timeUp = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Left side navigation drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Layout og the 3x3 grid of letters
        gridLayout = findViewById(R.id.guessGrid);

        DictionaryApplication dictionary = (DictionaryApplication) getApplication();
        GameInstance game = MainActivity.allGameInstances.get(getIntent().getExtras().getInt("game_index"));
        // Presenter handles all non-view related logic
        presenter = new GamescreenPresenter(
                this,
                game,
                dictionary.getDictionary(),
                new FoxSQLData(this),
                new GameData(this, game.getPlayerID())
        );
        presenter.setup();

        displayTitle();
        timeBlock = findViewById(R.id.timeBlock);
        populateTimeBlock();
    }

    @Override
    public TextView createTimeCounterSegment(int height) {
        TextView tv = new TextView(this);
        tv.setId(getUniqueId());
        tv.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height));
        tv.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        return tv;
    }

    @Override
    public void makeToast(String toastMessage) {
        Toast.makeText(this, toastMessage, Toast.LENGTH_SHORT).show();
    }

    private void displayTitle() {
        int currentRound = presenter.getRound();
        this.setTitle("Round " + (currentRound + 1));
    }

    @Override
    public void updateHeaderLetters() {
        TextView givenLettersTV = findViewById(R.id.givenLettersGameScreen);
        givenLettersTV.setText(presenter.getLettersSTR());
    }

    // Display on screen the longest found word
    @Override
    public void setLongest(String word) {
        // Print longest word
        TextView longestWord = findViewById(R.id.longestAttempt);
        longestWord.setText(word);
        // Print length of longest word
        String lengthSTR = Integer.toString(word.length());
        TextView lengthLongestTV = findViewById(R.id.lengthLongestAttempt);
        lengthLongestTV.setText(lengthSTR);  // Print Length of word
    }

    // Cell is most recent clicked
    @Override
    public void setCellNewlyClicked(String tag) {
        if (tag.equals("")) {
            return;
        }
        TextView cellTV = gridLayout.findViewWithTag(tag);
        cellTV.setClickable(true);         // Can un-click the most recently clicked letter
        cellTV.setBackground(ContextCompat.getDrawable(this, R.drawable.purple_rounded_border_textview));
    }

    // Cell has been clicked, but not the most recent clicked.
    @Override
    public void setCellOldClicked(String tag) {
        TextView previousCellGridTV = gridLayout.findViewWithTag(tag);
        previousCellGridTV.setClickable(false);         // Can't choose the same letter twice!
        previousCellGridTV.setBackground(ContextCompat.getDrawable(this, R.drawable.lightpurple_rounded_border_textview));
    }

    // Cell has not been clicked
    @Override
    public void setCellNotClicked(String tag) {
        TextView cellTV = gridLayout.findViewWithTag(tag);
        cellTV.setBackground(ContextCompat.getDrawable(this, R.drawable.turquoise_background_rounded_border_textview));
        cellTV.setClickable(true);
    }

    // Display the ongoing attempt
    @Override
    public void setCurrentAttempt(String currentGuess) {
        TextView currentGuessTV = findViewById(R.id.currentAttempt);
        currentGuessTV.setText(currentGuess);
    }

    // When the user clicks one of the letters in the grid. Append to ongoing attempt.
    public void gridCellClicked(View v) {       // TODO: should be on click listener
        TextView cellGridTV = (TextView) v;
        String cellLetter = (String) cellGridTV.getText();
        presenter.gridCellClicked(v.getTag().toString(), cellLetter);
    }

    // Wait for the layout to populate and then add the time segments
    private void populateTimeBlock() {
        // We must wait for the layout to be finished before we can measure the height.
        timeBlock.post(() -> presenter.addTimeBlocks(timeBlock.getHeight()));
    }

    // When timer ends, change to the Score Screen to show results.
    @Override
    public void startScoreScreen1Act(int index) {
        Intent EndScreenIntent = new Intent(this, RoundnGameResults.class);
        Bundle endScreenBundle = new Bundle();
        endScreenBundle.putString("key", "round");
        endScreenBundle.putInt("gameIndexNumber", index);
        EndScreenIntent.putExtras(endScreenBundle);
        startActivity(EndScreenIntent);
    }

    // Randomly shuffle locations of the letters in the grid
    public void shuffleGivenLetters(View v) {
        presenter.shuffleGivenLetters();
    }

    // Display a particular letter to it's respective location on the 3x3 grid
    @Override
    public void printGridCell(SingleCell cell) {
        TextView currentCell = gridLayout.findViewWithTag(cell.tag);
        currentCell.setText(cell.letter);
    }

    // Set time up flag. Will be checked 'onResume()' to decide to jump straight to results
    @Override
    public void setTimeUp(boolean timeState) {
        this.timeUp = timeState;
    }

    @Override
    public int getUniqueId() {
        return FoxUtils.getUniqueId(this);
    }

    @Override
    public void createTimer(ArrayList<TextView> textViews) {
        secondsBlocks = textViews;
        for (TextView tv : textViews) {
            timeBlock.addView(tv);
        }
        myGameTimerInstance = new GameTimer(this);
    }

    @Override
    public void updateSecondsCounter(int time) {
        TextView secondsLeft = findViewById(R.id.secondsRemaining);
        secondsLeft.setText(String.valueOf(time));
    }

    @Override
    public void hideTimeSection(int sectionIndex) {
        TextView timeBlock = secondsBlocks.get(sectionIndex);
        timeBlock.setBackgroundColor(0);
    }

    @Override
    public boolean isGameInFocus() {
        return gameInFocus;
    }

    public void clearCurrentAttempt(View v) {
        TextView currentAttemptTV = findViewById(R.id.currentAttempt);
        currentAttemptTV.setText("");
        presenter.setGridClickable();
        presenter.clearOnGoingAttempt();
        // Quick exit the game by tapping reset button twice
        if (this.resetButtonPressedOnce) {
            completeGame();
        }
        this.resetButtonPressedOnce = true;
        new Handler().postDelayed(() -> resetButtonPressedOnce = false, 500);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                resetButtonPressedOnce = false;
//            }
//        }, 500);
    }

    // Check if word is valid & longer than current best. If so, set as longest attempt.
    public void submitCurrentAttempt(View v) {
        TextView currentTV = findViewById(R.id.currentAttempt);
        String currentStr = (String) currentTV.getText();
        currentTV.setText("");
        presenter.submitCurrentAttempt(currentStr);
    }

    @Override
    public void completeGame() {
        presenter.updateData();
        presenter.completeGame();
    }

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
                Intent profileScreenIntent = new Intent(GameActivity.this, ProfileActivity.class);
                startActivity(profileScreenIntent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameInFocus = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        myGameTimerInstance.killTimer();
        super.onDestroy();
    }

    // If time expired since resuming, end the game
    @Override
    protected void onResume() {
        super.onResume();
        if (timeUp) {
            completeGame();
        }
        gameInFocus = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    // Must press back button twice in quick succession to exit the game
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            // If pressed recently, proceed to home screen
            if (this.backButtonPressedOnce) {
                Intent homeScreenIntent = new Intent(this, MainActivity.class);
                startActivity(homeScreenIntent);
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        navBurger.navigateTo(item, GameActivity.this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}

