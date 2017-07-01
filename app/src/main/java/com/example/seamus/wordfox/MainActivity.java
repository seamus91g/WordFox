package com.example.seamus.wordfox;

import android.os.Bundle;
import android.os.StrictMode;
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
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String MONITOR_TAG = "myTag";
    private TextView resetTV;
    private TextView submitTV;
    private TextView shuffleTV;
    private TextView cell1;
    private TextView cell2;
    private TextView cell3;
    private TextView cell4;
    private TextView cell5;
    private TextView cell6;
    private TextView cell7;
    private TextView cell8;
    private TextView cell9;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ArrayList givenLetters = new ArrayList();
        givenLetters = getGivenLetters();
        String givenLettersSTR = "";
        for (int i=0; i<givenLetters.size(); i++){
            givenLettersSTR += givenLetters.get(i);
        }

        TextView givenLettersTV = (TextView) findViewById(R.id.givenLettersGameScreen);
        givenLettersTV.setText(givenLettersSTR);

        writeToGuessGrid(givenLetters);

        resetTV = (TextView) findViewById(R.id.resetButton);
        submitTV = (TextView) findViewById(R.id.submitButton);
        shuffleTV = (TextView) findViewById(R.id.shuffleButton);

        resetTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TextView rst = (TextView) findViewById(R.id.currentAttempt);
                    rst.setText("");
                }
                return true;
            }

        });
        submitTV.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    TextView current = (TextView) findViewById(R.id.currentAttempt);
                    String currentStr = (String) current.getText();
                    TextView longest = (TextView) findViewById(R.id.longestAttempt);
                    longest.setText(currentStr);
                    int strLength = currentStr.length();
                    String sStrLength = Integer.toString(strLength);
                    TextView lengthLongest = (TextView) findViewById(R.id.lengthLongestAttempt);

                    lengthLongest.setText(sStrLength);
                    // lengthLongestAttempt
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

        cell1 = (TextView) findViewById(R.id.guessGridCell1);
        cell2 = (TextView) findViewById(R.id.guessGridCell2);
        cell3 = (TextView) findViewById(R.id.guessGridCell3);
        cell4 = (TextView) findViewById(R.id.guessGridCell4);
        cell5 = (TextView) findViewById(R.id.guessGridCell5);
        cell6 = (TextView) findViewById(R.id.guessGridCell6);
        cell7 = (TextView) findViewById(R.id.guessGridCell7);
        cell8 = (TextView) findViewById(R.id.guessGridCell8);
        cell9 = (TextView) findViewById(R.id.guessGridCell9);

        cell1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TextView cell = (TextView) findViewById(R.id.guessGridCell1);
                    String cellLetter = (String) cell.getText();

                    TextView currentGuessTV = (TextView) findViewById(R.id.currentAttempt);
                    String currentGuess = (String) currentGuessTV.getText();

                    currentGuess += cellLetter;
                    currentGuessTV.setText(currentGuess);
                    Log.d(MONITOR_TAG, "Guessing letter: " + cellLetter);
                }
                return false;
            }
        });
        cell2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TextView cell = (TextView) findViewById(R.id.guessGridCell2);
                    String cellLetter = (String) cell.getText(); TextView currentGuessTV = (TextView) findViewById(R.id.currentAttempt); String currentGuess = (String) currentGuessTV.getText(); currentGuess += cellLetter; currentGuessTV.setText(currentGuess); Log.d(MONITOR_TAG, "Guessing letter: " + cellLetter);
                }
                return false;
            }
        });
        cell3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TextView cell = (TextView) findViewById(R.id.guessGridCell3);
                    String cellLetter = (String) cell.getText(); TextView currentGuessTV = (TextView) findViewById(R.id.currentAttempt); String currentGuess = (String) currentGuessTV.getText(); currentGuess += cellLetter; currentGuessTV.setText(currentGuess); Log.d(MONITOR_TAG, "Guessing letter: " + cellLetter);
                }
                return false;
            }
        });
        cell4.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TextView cell = (TextView) findViewById(R.id.guessGridCell4);
                    String cellLetter = (String) cell.getText(); TextView currentGuessTV = (TextView) findViewById(R.id.currentAttempt); String currentGuess = (String) currentGuessTV.getText(); currentGuess += cellLetter; currentGuessTV.setText(currentGuess); Log.d(MONITOR_TAG, "Guessing letter: " + cellLetter);
                }
                return false;
            }
        });
        cell5.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TextView cell = (TextView) findViewById(R.id.guessGridCell5);
                    String cellLetter = (String) cell.getText(); TextView currentGuessTV = (TextView) findViewById(R.id.currentAttempt); String currentGuess = (String) currentGuessTV.getText(); currentGuess += cellLetter; currentGuessTV.setText(currentGuess); Log.d(MONITOR_TAG, "Guessing letter: " + cellLetter);
                }
                return false;
            }
        });
        cell6.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TextView cell = (TextView) findViewById(R.id.guessGridCell6);
                    String cellLetter = (String) cell.getText(); TextView currentGuessTV = (TextView) findViewById(R.id.currentAttempt); String currentGuess = (String) currentGuessTV.getText(); currentGuess += cellLetter; currentGuessTV.setText(currentGuess); Log.d(MONITOR_TAG, "Guessing letter: " + cellLetter);
                }
                return false;
            }
        });
        cell7.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TextView cell = (TextView) findViewById(R.id.guessGridCell7);
                    String cellLetter = (String) cell.getText(); TextView currentGuessTV = (TextView) findViewById(R.id.currentAttempt); String currentGuess = (String) currentGuessTV.getText(); currentGuess += cellLetter; currentGuessTV.setText(currentGuess); Log.d(MONITOR_TAG, "Guessing letter: " + cellLetter);
                }
                return false;
            }
        });
        cell8.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TextView cell = (TextView) findViewById(R.id.guessGridCell8);
                    String cellLetter = (String) cell.getText(); TextView currentGuessTV = (TextView) findViewById(R.id.currentAttempt); String currentGuess = (String) currentGuessTV.getText(); currentGuess += cellLetter; currentGuessTV.setText(currentGuess); Log.d(MONITOR_TAG, "Guessing letter: " + cellLetter);
                }
                return false;
            }
        });
        cell9.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    TextView cell = (TextView) findViewById(R.id.guessGridCell9);
                    String cellLetter = (String) cell.getText(); TextView currentGuessTV = (TextView) findViewById(R.id.currentAttempt); String currentGuess = (String) currentGuessTV.getText(); currentGuess += cellLetter; currentGuessTV.setText(currentGuess); Log.d(MONITOR_TAG, "Guessing letter: " + cellLetter);
                }
                return false;
            }
        });


        // cell3.setOnTouchListener(this);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

    public boolean writeToGuessGrid(ArrayList<String> givenLetters){

        TextView cell1 = (TextView) findViewById(R.id.guessGridCell1);
        TextView cell2 = (TextView) findViewById(R.id.guessGridCell2);
        TextView cell3 = (TextView) findViewById(R.id.guessGridCell3);
        TextView cell4 = (TextView) findViewById(R.id.guessGridCell4);
        TextView cell5 = (TextView) findViewById(R.id.guessGridCell5);
        TextView cell6 = (TextView) findViewById(R.id.guessGridCell6);
        TextView cell7 = (TextView) findViewById(R.id.guessGridCell7);
        TextView cell8 = (TextView) findViewById(R.id.guessGridCell8);
        TextView cell9 = (TextView) findViewById(R.id.guessGridCell9);
        cell1.setText(givenLetters.get(0));
        cell2.setText(givenLetters.get(1));
        cell3.setText(givenLetters.get(2));
        cell4.setText(givenLetters.get(3));
        cell5.setText(givenLetters.get(4));
        cell6.setText(givenLetters.get(5));
        cell7.setText(givenLetters.get(6));
        cell8.setText(givenLetters.get(7));
        cell9.setText(givenLetters.get(8));

        return true;
    }

    public ArrayList<String> getGivenLetters() {
        ArrayList<String> givenLetters = new ArrayList<String>();
        for (int i=0; i<9; i++) {
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
