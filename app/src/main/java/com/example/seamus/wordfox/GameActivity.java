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
import java.util.Collections;

import static java.util.Collections.shuffle;

public class GameActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String MONITOR_TAG = "myTag";
    private TextView resetTV;
    private TextView submitTV;
    private TextView shuffleTV;
//    public static int totalScore;
    private foxDictionary myDiction;

    public static gameInstance myGameInstance = new gameInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        totalScore = 0;
//        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//        SharedPreferences.Editor editor = preferences.edit();
//        editor.putInt("totalScore",0);
//        editor.apply();

        myGameInstance.clearRoundScores();

        AssetManager assetManager = this.getAssets();
        myDiction = new foxDictionary();
        try {
            InputStream myIpStr = assetManager.open("validWords.txt");
            myDiction.readFile(myIpStr);
            myIpStr.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
//        myDiction.checkWordExists("potato");
//        myDiction.checkWordExists("ptato");

        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
//                TextView mTextField = (TextView) findViewById(R.id.timeTextField);
                int time = (int) millisUntilFinished / 1000;
                switch (time){
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


//                mTextField.setText("t: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
//                TextView mTextField = (TextView) findViewById(R.id.timeTextField);
//                mTextField.setText("0");
            }
        }.start();



        ArrayList givenLetters = new ArrayList();
        givenLetters = getGivenLetters();
        String givenLettersSTR = "";
        for (int i = 0; i < givenLetters.size(); i++) {
            givenLettersSTR += givenLetters.get(i);
        }
        TextView givenLettersTV = (TextView) findViewById(R.id.givenLettersGameScreen);
        givenLettersTV.setText(givenLettersSTR);

        writeToGuessGrid(givenLetters);


        resetTV = (TextView) findViewById(R.id.resetButton);
        submitTV = (TextView) findViewById(R.id.submitButton);
        shuffleTV = (TextView) findViewById(R.id.shuffleButton);




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void startScoreScreen1Act(){
        Log.d(MONITOR_TAG, "starting Score Screen 1");
        Intent ScoreScreen1Intent = new Intent(this, ScoreScreen1Activity.class);
        startActivity(ScoreScreen1Intent);
    }

    public boolean writeToGuessGrid(ArrayList<String> givenLetters) {

        for (int i = 0; i < givenLetters.size(); i++) {
            String allCellId = "guessGridCell" + (i + 1);
            int resID = getResources().getIdentifier(allCellId, "id", getPackageName());
            TextView currentCell = (TextView) findViewById(resID);

            currentCell.setText(givenLetters.get(i));
        }

        return true;
    }

    public void setGridClickable() {
        int cellCount = 9;
        for (int i = 0; i < cellCount; i++) {

            String allCellId = "guessGridCell" + (i + 1);

            int resID = getResources().getIdentifier(allCellId, "id", getPackageName());
            TextView currentCell = (TextView) findViewById(resID);
            currentCell.setClickable(true);
        }

    }

    public void clearCurrentAttempt(View v){
        TextView currentAttemptTV = (TextView) findViewById(R.id.currentAttempt);
        currentAttemptTV.setText("");
        setGridClickable();
    }

    public void submitCurrentAttempt(View v){
        TextView currentTV = (TextView) findViewById(R.id.currentAttempt);
        String currentStr = (String) currentTV.getText();
        int currentStrLen = currentStr.length();
        String lcCurrentStr = currentStr.toLowerCase();
        currentTV.setText("");
        setGridClickable();
        if(!myDiction.checkWordExists(lcCurrentStr)){
            Toast.makeText(this, "Word doesn't exist", Toast.LENGTH_SHORT).show();
            return;
        }

        TextView longestTV = (TextView) findViewById(R.id.longestAttempt);
        String longestStr = (String) longestTV.getText();
        int longestStrLen = longestStr.length();


        if (currentStrLen > longestStrLen) {
            myGameInstance.setLongestWord(currentStr);
            longestTV.setText(currentStr);

            myGameInstance.setScore(currentStrLen);


            String currentStrLenSTR = Integer.toString(currentStrLen);
            TextView lengthLongestTV = (TextView) findViewById(R.id.lengthLongestAttempt);
            lengthLongestTV.setText(currentStrLenSTR);

//            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
//            SharedPreferences.Editor editor = preferences.edit();
//            editor.putString("score",currentStrLenSTR);
//            editor.apply();

        } else {
            //provide the same feedback as if the user has enterred an incorrect word (one that isn't in the dictionary)
            // buzz vibrate and blink the screen or something
        }
    }

    public void shuffleGivenLetters(View v){
        ArrayList<String> givenLetters = new ArrayList<String>();
        TextView givenLettersTV = (TextView) findViewById(R.id.givenLettersGameScreen);
        String givenLettersSTR = (String) givenLettersTV.getText();

        for (int i = 0; i < givenLettersSTR.length(); i++) {
//                        givenLettersSTR += givenLetters.get(i);
            Log.d(MONITOR_TAG, "subStr: " + givenLettersSTR.substring(i, i+1));
            givenLetters.add(givenLettersSTR.substring(i, i+1));
        }
        Collections.shuffle(givenLetters);

        givenLettersSTR = "";
        for (int i = 0; i < givenLetters.size(); i++) {
            givenLettersSTR += givenLetters.get(i);
        }
//                    Log.d(MONITOR_TAG, "Shuf letters: " + givenLettersSTR);

//
        givenLettersTV.setText(givenLettersSTR);
        writeToGuessGrid(givenLetters);
    }

    public void gridCellClicked(View v) {

        // ArrayList<Integer> CellIDsUsed = new ArrayList<Integer>();

        String cellID = getResources().getResourceName(v.getId());
        int resID = getResources().getIdentifier(cellID, "id", getPackageName());

        TextView cellGridTV = (TextView) findViewById(resID);
        String cellLetter = (String) cellGridTV.getText();

        TextView currentGuessTV = (TextView) findViewById(R.id.currentAttempt);
        String currentGuess = (String) currentGuessTV.getText();

        currentGuess += cellLetter;
        currentGuessTV.setText(currentGuess);
        cellGridTV.setClickable(false);
        Log.d(MONITOR_TAG, "v ID: " + v.getId());
        Log.d(MONITOR_TAG, "ID: " + cellID);
        Log.d(MONITOR_TAG, "res ID: " + resID);
        Log.d(MONITOR_TAG, "Text: " + cellGridTV.getText());
    }

    public ArrayList<String> getGivenLetters() {
        ArrayList<String> givenLetters = new ArrayList<String>();
        String consonants = randLetter("consonants");
        String vowels = randLetter("vowels");
        String letters = consonants + vowels;
        int lettersLen = letters.length();

        for (int i=0; i<lettersLen; i++)
        {
            givenLetters.add((letters.substring(i, i+1)));
        }

        shuffle(givenLetters);
        return givenLetters;
    }

    public String randLetter(String choice) {
        String letters = "";
        String set = "";
        int times = 0;

        if (choice.equals("consonants")) {
            letters = "BCDFGHJKLMNPQRSTVWXYZ";
            times = 6;
        }else if(choice.equals("vowels")) {
            letters = "AEIOU";
            times = 3;
        }

        int letterLen = letters.length();

        for (int i=0; i<times; i++)
        {
            int random = (int)(Math.random() * letterLen);
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


}
