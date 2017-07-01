package com.example.seamus.wordfox;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String MONITOR_TAG = "myTag";
    private TextView resetTV;
    private TextView submitTV;
    private TextView shuffleTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
// consider changing to xml function onClick
        resetTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TextView rst = (TextView) findViewById(R.id.currentAttempt);
                    rst.setText("");

                    setGridClickable();
                }
                return true;
            }

        });


        submitTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    TextView currentTV = (TextView) findViewById(R.id.currentAttempt);
                    String currentStr = (String) currentTV.getText();
                    int currentStrLen = currentStr.length();

                    TextView longestTV = (TextView) findViewById(R.id.longestAttempt);
                    String longestStr = (String) longestTV.getText();
                    int longestStrLen = longestStr.length();

                    currentTV.setText("");

                    if (currentStrLen > longestStrLen) {
                        longestTV.setText(currentStr);

                        String currentStrLenSTR = Integer.toString(currentStrLen);
                        TextView lengthLongestTV = (TextView) findViewById(R.id.lengthLongestAttempt);
                        lengthLongestTV.setText(currentStrLenSTR);
                    } else {
                        //provide the same feedback as if the user has enterred an incorrect word (one that isn't in the dictionary)
                        // buzz vibrate and blink the screen or something
                    }

                    setGridClickable();
                }
                return true;
            }
        });
        shuffleTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                }
                return true;
            }
        });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        for (int i = 0; i < 9; i++) {
            givenLetters.add(randLetter());
        }
        return givenLetters;
    }

    public String randLetter() {
        final String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int N = alphabet.length();
        Random r = new Random();
        int charIndex = r.nextInt(N);
        char ranChar = (char) alphabet.charAt(charIndex);
        return Character.toString(ranChar);
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
