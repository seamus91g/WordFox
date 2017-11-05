package com.example.seamus.wordfox;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

public class GameActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String MONITOR_TAG = "myTag";
    private FoxDictionary myDiction;
    private GameInstance myGameInstance; // = new GameInstance();
    private LinkedList<SingleCell> alreadyClicked = new LinkedList<SingleCell>();
    private NavigationBurger navBurger = new NavigationBurger();
    private boolean backButtonPressedOnce = false;
    private boolean resetButtonPressedOnce = false;
    private GameTimer myGameTimerInstance;
    private GameData myGameData;
    private ArrayList<SingleCell> listOfGridCells; // = new ArrayList<SingleCell>();
    private boolean gameInFocus;
    private boolean timeUp;
    private int gameIndexNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameIndexNumber = getIntent().getExtras().getInt("game_index");
        Log.d(MONITOR_TAG, "Game index is: " + gameIndexNumber);
        myGameInstance = MainActivity.allGameInstances.get(gameIndexNumber);
        int currentRound = MainActivity.allGameInstances.get(gameIndexNumber).getRound();
        this.setTitle("Round " + (currentRound + 1));

        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        alreadyClicked.add(new SingleCell(0, ""));      // TODO Fix this!

        if (myGameInstance.getPlayerID().equals("")){
            myGameData = new GameData(this.getApplicationContext(), gameIndexNumber);
        }else{
            myGameData = new GameData(this.getApplicationContext(), myGameInstance.getPlayerID());
        }

        if (currentRound == 0) {
            myGameData.gameCountUp();
        }
        myGameData.roundCountUp();
        // Clear longest word. Clear score for round but keep Total Score.
        myGameInstance.clearRoundScores();

        setGameInFocus(true);

        // Read in text file of all valid words. Store words in class FoxDictionary
        myDiction = new FoxDictionary("validWords_alph.txt", "letterFrequency.txt", this);

        myGameTimerInstance = new GameTimer(this);

        // Generate a random sequence of 9 letters to use for the game
        ArrayList<String> givenLetters = new ArrayList<>();
        String givenLettersSTR = "";
        if (gameIndexNumber == 0) {
            givenLetters = myDiction.getGivenLetters();
            for (int i = 0; i < givenLetters.size(); i++) {
                givenLettersSTR += givenLetters.get(i);
            }
        } else {      // If multi player game, re-use the same letters
            Log.d(MONITOR_TAG, "Re-using game letters ... ");
            givenLettersSTR = MainActivity.allGameInstances.get(0).getLetters(myGameInstance.getRound());
            String[] letters = givenLettersSTR.split("");
            for (int i = 1; i < letters.length; i++) {       // First is a blank, skip
                givenLetters.add(letters[i]);
            }
        }

        myGameInstance.setLetters(givenLettersSTR);
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
        myGameInstance.setLongestPossible(myDiction.longestWordFromLetters(givenLettersSTR));
        Log.d(MONITOR_TAG, "Longest word is: " + myGameInstance.getLongestPossible());
    }

    // When timer ends, change to the Score Screen to show results.
    public void startScoreScreen1Act() {
        Log.d(MONITOR_TAG, "starting Score Screen 1");
        Intent ScoreScreen1Intent = new Intent(this, ScoreScreen1Activity.class);

        ScoreScreen1Intent.putExtra("game_index", gameIndexNumber);
        startActivity(ScoreScreen1Intent);
    }

    // Print the 9 generated letters to the 3x3 grid.
    public boolean writeToGuessGrid(ArrayList<String> givenLetters) {
        // Loop to retrieve each letter and write to its appropriate text view in the grid
        listOfGridCells = new ArrayList<SingleCell>();

        Log.d(MONITOR_TAG, "Resetting grid cell list. Size is: " + listOfGridCells.size());
        for (int i = 0; i < givenLetters.size(); i++) {
            String allCellId = "guessGridCell" + (i + 1);
            int resID = getResources().getIdentifier(allCellId, "id", getPackageName());
//            SingleCell mySC = new SingleCell(resID, givenLetters.get(i));
            listOfGridCells.add(new SingleCell(resID, givenLetters.get(i)));

        }
        printGridCells(listOfGridCells);
        return true;
    }

    public void printGridCells(ArrayList<SingleCell> gridCells) {
        for (int i = 0; i < gridCells.size(); i++) {
            TextView currentCell = (TextView) findViewById(gridCells.get(i).resID);
            currentCell.setText(gridCells.get(i).letter);
        }
    }

    // Set the clickable attribute to true on each letter in the 3x3 grid. This is used to reset the grid after a game.
    public void setGridClickable() {
        int cellCount = 9;
        for (int i = 0; i < cellCount; i++) {
            String allCellId = "guessGridCell" + (i + 1);
            int resID = getResources().getIdentifier(allCellId, "id", getPackageName());
            TextView currentCell = (TextView) findViewById(resID);
            currentCell.setClickable(true);
            currentCell.setBackground(ContextCompat.getDrawable(this, R.drawable.turquoise_background_rounded_border_textview));
            alreadyClicked.clear();
            alreadyClicked.add(new SingleCell(0, ""));      // TODO Fix this!
        }
    }

    // When user clicks the Reset button, clear the currently typed letters.
    public void clearCurrentAttempt(View v) {
        TextView currentAttemptTV = (TextView) findViewById(R.id.currentAttempt);
        currentAttemptTV.setText("");
        setGridClickable();

        if (this.resetButtonPressedOnce) {
            completeGame();
        }
        this.resetButtonPressedOnce = true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                resetButtonPressedOnce = false;
            }
        }, 500);
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
            myGameData.incorrectCountUp();
            return;
        }
        myGameData.correctCountUp();
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
        myGameData.shuffleCountUp();
        // Shuffle the list containing the grid cells
        Collections.shuffle(listOfGridCells);
        String shuffled = "";
        for (SingleCell singleCell : listOfGridCells) {     // Create String to print at top of screen
            shuffled += singleCell.letter;
        }

        // Map the old resource IDs to the new ones
        HashMap<Integer, Integer> oldToNew = new HashMap<Integer, Integer>();
        for (int i = 0; i < listOfGridCells.size(); i++) {
            SingleCell singleCell = listOfGridCells.get(i);
            int resIdOld = singleCell.resID;        // Old resource ID of this grid cell
            TextView currentCell = (TextView) findViewById(resIdOld);
            currentCell.setClickable(true);
            currentCell.setBackground(ContextCompat.getDrawable(this, R.drawable.turquoise_background_rounded_border_textview));


            // Android xml ID string is based on it's order in the List of grid cells
            String newCellId = "guessGridCell" + (i + 1);
            // Get new resource ID from the android xml ID string
            singleCell.resID = getResources().getIdentifier(newCellId, "id", getPackageName());
            listOfGridCells.set(i, singleCell);
            oldToNew.put(resIdOld, singleCell.resID);   // Map changes to the resource IDs
        }

        TextView currentCell = null;
        // Assign new resource IDs to the already clicked grid cells
        // Highlight all the letters which have already been clicked
        for (SingleCell singleCellClicked : alreadyClicked) {
            if (singleCellClicked.resID == 0) {
                Log.d(MONITOR_TAG, "Skipping first");
                continue;
            }
            singleCellClicked.resID = oldToNew.get(singleCellClicked.resID);
            currentCell = (TextView) findViewById(singleCellClicked.resID);
            currentCell.setBackground(ContextCompat.getDrawable(this, R.drawable.lightpurple_rounded_border_textview));
            currentCell.setClickable(false);         // Can't choose the same letter twice!!
        }
        // Most recently clicked cell is a different color
        if (currentCell != null) {
            currentCell.setBackground(ContextCompat.getDrawable(this, R.drawable.purple_rounded_border_textview));
            currentCell.setClickable(true);         // Can't choose the same letter twice!!
        }

        // Print newly shuffled letters to top of the screen and to the 3x3 grid
        TextView givenLettersTV = (TextView) findViewById(R.id.givenLettersGameScreen);
        givenLettersTV.setText(shuffled);

        printGridCells(listOfGridCells);
    }
//    private void updateResIDs

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

        int previousID = -1;        // TODO do this better.
        if (alreadyClicked.getLast().letter != null) {
            previousID = (int) alreadyClicked.getLast().resID;
        }
        if (previousID != resID) {      // If new color
            currentGuess += cellLetter;             // Append the new letter
            if (previousID != 0) {
                TextView previousCellGridTV = (TextView) findViewById(previousID);
                previousCellGridTV.setClickable(false);         // Can't choose the same letter twice!!
                previousCellGridTV.setBackground(ContextCompat.getDrawable(this, R.drawable.lightpurple_rounded_border_textview));
            }
//            SingleCell singleCell = new SingleCell(resID, cellLetter);
            alreadyClicked.add(new SingleCell(resID, cellLetter));
            cellGridTV.setBackground(ContextCompat.getDrawable(this, R.drawable.purple_rounded_border_textview));
        } else {
            alreadyClicked.removeLast();
            int prePreviousID = (int) alreadyClicked.getLast().resID;
            cellGridTV.setBackground(ContextCompat.getDrawable(this, R.drawable.turquoise_background_rounded_border_textview));
            if (prePreviousID != 0) {
                TextView previousCellGridTV = (TextView) findViewById(prePreviousID);
                previousCellGridTV.setClickable(true);         // Can't choose the same letter twice!!
                previousCellGridTV.setBackground(ContextCompat.getDrawable(this, R.drawable.purple_rounded_border_textview));
            }
            currentGuess = currentGuess.substring(0, currentGuess.length() - 1);
        }
        currentGuessTV.setText(currentGuess);   // Write the appended string back to the Text View
    }

    public boolean isTimeUp() {
        return timeUp;
    }

    public void setTimeUp(boolean timeState) {
        this.timeUp = timeState;
    }

    public boolean isGameInFocus() {
        return gameInFocus;
    }

    public void setGameInFocus(boolean gameState) {
        this.gameInFocus = gameState;
    }

    public void completeGame() {
        Log.d(MONITOR_TAG, "Adding word to prefs: " + myGameInstance.getLongestWord() + ", END");
        myGameData.addWord(myGameInstance.getLongestWord());
        startScoreScreen1Act();

        int currentRound = myGameInstance.getRound();
        switch (currentRound) {
            case 0:
                myGameInstance.setRound1Word(myGameInstance.getLongestWord());
                myGameInstance.setRound1Length(myGameInstance.getLongestWord().length());
                Log.d(MONITOR_TAG, "CURRENT ROUND, WORD FOR THIS ROUND, " + currentRound + myGameInstance.getLongestWord());
                break;
            case 1:
                myGameInstance.setRound2Word(myGameInstance.getLongestWord());
                myGameInstance.setRound2Length(myGameInstance.getLongestWord().length());
                Log.d(MONITOR_TAG, "CURRENT ROUND, WORD FOR THIS ROUND, " + currentRound + myGameInstance.getLongestWord());
                break;
            case 2:
                myGameInstance.setRound3Word(myGameInstance.getLongestWord());
                myGameInstance.setRound3Length(myGameInstance.getLongestWord().length());
                Log.d(MONITOR_TAG, "CURRENT ROUND, WORD FOR THIS ROUND, " + currentRound + myGameInstance.getLongestWord());
                break;
            default:
                break;
        }
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
//        Log.d(MONITOR_TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
//        Log.d(MONITOR_TAG, "onStop");
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
//        Log.d(MONITOR_TAG, "onResume");
        if (isTimeUp()) {
            Log.d(MONITOR_TAG, "Changing activity from onResume");
            completeGame();
//            Intent ScoreScreen1Intent = new Intent(GameActivity.this, ScoreScreen1Activity.class);
//            startActivity(ScoreScreen1Intent);
        }
        setGameInFocus(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
//        Log.d(MONITOR_TAG, "onStart");
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            super.onBackPressed();
            if (this.backButtonPressedOnce) {
                Intent homeScreenIntent = new Intent(this, MainActivity.class);
                startActivity(homeScreenIntent);
            }
            this.backButtonPressedOnce = true;
            Toast.makeText(this, "Press BACK again to exit the game", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
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

class SingleCell {
    int resID;
    String letter;

    public SingleCell(int resID, String letter) {
        this.resID = resID;
        this.letter = letter;
    }

}
