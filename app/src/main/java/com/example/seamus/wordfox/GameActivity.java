package com.example.seamus.wordfox;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.shuffle;

public class GameActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String MONITOR_TAG = "myTag";
    private foxDictionary myDiction;
    public static gameInstance myGameInstance = new gameInstance();
    private NavigationBurger navBurger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Clear longest word. Clear score for round but keep Total Score.
        myGameInstance.clearRoundScores();

        // Read in text file of all valid words. Store words in class foxDictionary
        AssetManager assetManager = this.getAssets();
        myDiction = new foxDictionary();
        try {
            InputStream myIpStr = assetManager.open("validWords.txt");
            myDiction.readFile(myIpStr);
            myIpStr.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

        // 30 second counter until game ends.
        new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                int time = (int) millisUntilFinished / 1000;
                switch (time) {
                    case 24:
                        TextView box1 = (TextView) findViewById(R.id.timeblock1);
                        box1.setBackgroundColor(0);
                        break;
                    case 18:
                        TextView box2 = (TextView) findViewById(R.id.timeblock2);
                        box2.setBackgroundColor(0);
                        break;
                    case 12:
                        TextView box3 = (TextView) findViewById(R.id.timeblock3);
                        box3.setBackgroundColor(0);
                        break;
                    case 6:
                        TextView box4 = (TextView) findViewById(R.id.timeblock4);
                        box4.setBackgroundColor(0);
                        break;
                    case 1:
                        TextView box5 = (TextView) findViewById(R.id.timeblock5);
                        box5.setBackgroundColor(0);
                        Toast.makeText(GameActivity.this, "Time out!", Toast.LENGTH_SHORT).show();
                        startScoreScreen1Act();
                        break;
                }
            }
            public void onFinish() {
            }
        }.start();

        // Generate a random sequence of 9 letters to use for the game
        ArrayList<String> givenLetters = getGivenLetters();
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
        // Get ID of which cell was clicked and then retrieve the corresponding letter
        String cellID = getResources().getResourceName(v.getId());
        int resID = getResources().getIdentifier(cellID, "id", getPackageName());
        TextView cellGridTV = (TextView) findViewById(resID);
        String cellLetter = (String) cellGridTV.getText();
        // Update the current attempt by appending the chosen letter
        TextView currentGuessTV = (TextView) findViewById(R.id.currentAttempt);
        String currentGuess = (String) currentGuessTV.getText();
        currentGuess += cellLetter;             // Append the new letter
        currentGuessTV.setText(currentGuess);   // Write the appended string back to the Text View
        cellGridTV.setClickable(false);         // Can't choose the same letter twice!!
    }

    // Randomly generate a sequence of 9 letters for the user
    public ArrayList<String> getGivenLetters() {
        ArrayList<String> givenLetters = new ArrayList<String>();
        String consonants = randLetter("consonants");
        String vowels = randLetter("vowels");
        String letters = consonants + vowels;
        int lettersLen = letters.length();
        for (int i = 0; i < lettersLen; i++) {
            givenLetters.add((letters.substring(i, i + 1)));
        }
        shuffle(givenLetters);
        return givenLetters;
    }
    // Generate 6 random consonants or 3 random vowels
    public String randLetter(String choice) {
        String letters = "";
        String set = "";
        int times = 0;

        if (choice.equals("consonants")) {
            letters = "BCDFGHJKLMNPQRSTVWXYZ";
            times = 6;
        } else if (choice.equals("vowels")) {
            letters = "AEIOU";
            times = 3;
        }

        int letterLen = letters.length();
        for (int i = 0; i < times; i++) {
            int random = (int) (Math.random() * letterLen);
            char randomLetter = letters.charAt(random);
            set += randomLetter;
        }
        return set;
    }

    /////////////////////////////////
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
        getMenuInflater().inflate(R.menu.main, menu);
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
        Log.d(MONITOR_TAG, "Before_onNavigationItemSelected__");
        navBurger.navigateTo(item, GameActivity.this);

        Log.d(MONITOR_TAG, "After_onNavigationItemSelected__");
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
