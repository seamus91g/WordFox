package com.example.seamus.wordfox;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;

public class GameActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String MONITOR_TAG = "myTag";
    private foxDictionary myDiction;
    public static gameInstance myGameInstance = new gameInstance();
    static LinkedList alreadyClicked = new LinkedList();
    private NavigationBurger navBurger = new NavigationBurger();
    private boolean backButtonPressedOnce = false;
    private gameTimer myGameTimerInstance;
    private GameData myGameData;
    private boolean gameInFocus;
    private boolean timeUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(MONITOR_TAG, "onCreate ---------- ");
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        alreadyClicked.add(0);
        myGameData = new GameData(this.getApplicationContext());
        myGameData.gameCountUp();
        // Clear longest word. Clear score for round but keep Total Score.
        myGameInstance.clearRoundScores();

        setGameInFocus(true);

        // Read in text file of all valid words. Store words in class foxDictionary
        myDiction = new foxDictionary("validWords.txt", "letterFrequency.txt", this);

        myGameTimerInstance = new gameTimer(this);

        // Generate a random sequence of 9 letters to use for the game
        ArrayList<String> givenLetters = myDiction.getGivenLetters();
        String givenLettersSTR = "";
        for (int i = 0; i < givenLetters.size(); i++) {
            givenLettersSTR += givenLetters.get(i);
        }
        // Write the letters to the heading and to the 3x3 textView grid
        TextView givenLettersTV = (TextView) findViewById(R.id.givenLettersGameScreen);
        givenLettersTV.setText(givenLettersSTR);
        writeToGuessGrid(givenLetters);

        // Left side navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    // When timer ends, change to the Score Screen to show results.
    public void startScoreScreen1Act() {
        Log.d(MONITOR_TAG, "starting Score Screen 1");
        Intent ScoreScreen1Intent = new Intent(this, ScoreScreen1Activity.class);
        startActivity(ScoreScreen1Intent);
    }

    // Print the 9 generated letters to the 3x3 grid.
    public boolean writeToGuessGrid(ArrayList<String> givenLetters) {
        // Loop to retrieve each letter and write to its appropriate text view in the grid
        for (int i = 0; i < givenLetters.size(); i++) {
            String allCellId = "guessGridCell" + (i + 1);
            int resID = getResources().getIdentifier(allCellId, "id", getPackageName());
            TextView currentCell = (TextView) findViewById(resID);
            currentCell.setText(givenLetters.get(i));
        }
        return true;
    }

    // Set the clickable attribute to true on each letter in the 3x3 grid. This is used to reset the grid after a game.
    public void setGridClickable() {
        int cellCount = 9;
        for (int i = 0; i < cellCount; i++) {
            String allCellId = "guessGridCell" + (i + 1);
            int resID = getResources().getIdentifier(allCellId, "id", getPackageName());
            TextView currentCell = (TextView) findViewById(resID);
            currentCell.setClickable(true);
            currentCell.setBackgroundColor(0x00000000);
            alreadyClicked.clear();
            alreadyClicked.add(0);
        }
    }

    // When user clicks the Reset button, clear the currently typed letters.
    public void clearCurrentAttempt(View v) {
        TextView currentAttemptTV = (TextView) findViewById(R.id.currentAttempt);
        currentAttemptTV.setText("");
        setGridClickable();
    }

    // Check if word is valid & longer than current best. If so, set as longest attempt.
    public void submitCurrentAttempt(View v) {
        setGridClickable();     // Set all letters to be chooseable again.
        // Retrieve what the currently chosen letter sequence is
        TextView currentTV = (TextView) findViewById(R.id.currentAttempt);
        String currentStr = (String) currentTV.getText();
        currentTV.setText("");  // Once retrieved, clear text from the current guess box

        // Check dictionary to see if the word exists
        String lcCurrentStr = currentStr.toLowerCase(); // All words in dictionary are lower case
        if (!myDiction.checkWordExists(lcCurrentStr)) {
            Toast.makeText(this, "Word doesn't exist", Toast.LENGTH_SHORT).show();
            return;
        }

        // Retrieve the existing longest guess to compare with the submitted one
        TextView longestTV = (TextView) findViewById(R.id.longestAttempt);
        String longestStr = (String) longestTV.getText();

        int longestStrLen = longestStr.length();
        int currentStrLen = currentStr.length();
        // If current attempt is longer than previous best attempt, accept word as new longest
        if (currentStrLen >= longestStrLen) {
            // Print longest word and its length to the screen
            String currentStrLenSTR = Integer.toString(currentStrLen);
            TextView lengthLongestTV = (TextView) findViewById(R.id.lengthLongestAttempt);
            lengthLongestTV.setText(currentStrLenSTR);  // Print Length of word
            longestTV.setText(currentStr);              // Print word

            myGameInstance.setLongestWord(currentStr);  // Save to display on the score screen
            myGameInstance.setScore(currentStrLen);     // Save to display on the score screen
        }
    }

    // Randomly shuffle the locations of the letters
    public void shuffleGivenLetters(View v) {
        // Retrieve the letters from the screen Text View
        TextView givenLettersTV = (TextView) findViewById(R.id.givenLettersGameScreen);
        String givenLettersSTR = (String) givenLettersTV.getText();

        // Convert String to List and then shuffle the list. Convert back to String.
        ArrayList<String> letters = new ArrayList<String>(Arrays.asList(givenLettersSTR.split("")));
        letters.remove(0);  // Blank element at start. Remove it
        Collections.shuffle(letters);
        String shuffled = "";
        for (String letter : letters) {     // Convert back to a String
            shuffled += letter;
        }

        // Print newly shuffled letters to top of the screen and to the 3x3 grid
        givenLettersTV.setText(shuffled);
        writeToGuessGrid(letters);
    }

    // Detect if user clicks a cell in the 3x3 letter grid. Prevent choosing the same cell twice!
    public void gridCellClicked(View v) {
        String cellID = getResources().getResourceName(v.getId());
        int resID = getResources().getIdentifier(cellID, "id", getPackageName());
        // Get ID of which cell was clicked and then retrieve the corresponding letter
        TextView cellGridTV = (TextView) findViewById(resID);
        String cellLetter = (String) cellGridTV.getText();
        // Update the current attempt by appending the chosen letter
        TextView currentGuessTV = (TextView) findViewById(R.id.currentAttempt);
        String currentGuess = (String) currentGuessTV.getText();

        int previousID = -1;
        if (alreadyClicked.getLast() != null){
            previousID = (int) alreadyClicked.getLast();
        }
        Log.d(MONITOR_TAG, "Click: 1");
        if ( previousID != resID){      // If new color
            currentGuess += cellLetter;             // Append the new letter
            if (previousID != 0) {
                TextView previousCellGridTV = (TextView) findViewById(previousID);
                previousCellGridTV.setClickable(false);         // Can't choose the same letter twice!!
                previousCellGridTV.setBackgroundColor(Color.parseColor("#BBDEFB"));
            }
            alreadyClicked.add(resID);
            cellGridTV.setBackgroundColor(Color.parseColor("#90CAF9"));
        }else {
            alreadyClicked.removeLast();
            int prePreviousID = (int) alreadyClicked.getLast();
            cellGridTV.setBackgroundColor(0x00000000);
            if (prePreviousID != 0) {
                TextView previousCellGridTV = (TextView) findViewById(prePreviousID);
                previousCellGridTV.setClickable(true);         // Can't choose the same letter twice!!
                previousCellGridTV.setBackgroundColor(Color.parseColor("#90CAF9"));
            }
            currentGuess = currentGuess.substring(0, currentGuess.length() - 1);
        }
        currentGuessTV.setText(currentGuess);   // Write the appended string back to the Text View
    }

    public boolean isTimeUp(){
        return timeUp;
    }
    public void setTimeUp(boolean timeState){
        Log.d(MONITOR_TAG, "Setting time state: " + timeState);
        this.timeUp = timeState;
    }
    public boolean isGameInFocus(){
        return gameInFocus;
    }
    public void setGameInFocus(boolean gameState){
        Log.d(MONITOR_TAG, "Setting focus state: " + gameState);
        this.gameInFocus = gameState;
    }
    public void completeGame(){
        Log.d(MONITOR_TAG, "Adding word to prefs: " + myGameInstance.getLongestWord() + ", END");
        myGameData.addWord(myGameInstance.getLongestWord());
        Log.d(MONITOR_TAG, "Now longest: " + myGameData.findLongest() + ", END");
        Log.d(MONITOR_TAG, "Changing activity from gameTimer");
        Intent ScoreScreen1Intent = new Intent(this, ScoreScreen1Activity.class);
        startActivity(ScoreScreen1Intent);
    }





    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        return true;
    }@Override

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                // User chose the "Profile" item, jump to the profile page
                Log.d(MONITOR_TAG, "Chose des's profile icon, END");
                Intent profileScreenIntent = new Intent(GameActivity.this, ProfileActivity.class);
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

    /////////////////////////////////

    @Override
    protected void onPause() {
        super.onPause();
        setGameInFocus(false);
        Log.d(MONITOR_TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(MONITOR_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        Log.d(MONITOR_TAG, "Killing timer from onDestroy");
        myGameTimerInstance.killTimer();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(MONITOR_TAG, "onResume");
        if (isTimeUp()){
            completeGame();
            Log.d(MONITOR_TAG, "Changing activity from onResume");
            Intent ScoreScreen1Intent = new Intent(GameActivity.this, ScoreScreen1Activity.class);
            startActivity(ScoreScreen1Intent);
        }
        setGameInFocus(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(MONITOR_TAG, "onStart");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            if (this.backButtonPressedOnce){
                Intent homeScreenIntent = new Intent(this, MainActivity.class);
                startActivity(homeScreenIntent);
            }
            this.backButtonPressedOnce = true;
            Toast.makeText(this, "Press BACK again to exit the game", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    backButtonPressedOnce = false;
                }
            }, 2500);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Log.d(MONITOR_TAG, "Before_onNavigationItemSelected__");
        navBurger.navigateTo(item, GameActivity.this);

        Log.d(MONITOR_TAG, "After_onNavigationItemSelected__");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
