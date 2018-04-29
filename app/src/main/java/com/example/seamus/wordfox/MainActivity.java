package com.example.seamus.wordfox;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static final String MONITOR_TAG = "myTag";
    private static final String TAG = "Time";
    private boolean backButtonPressedOnce = false;
    private NavigationBurger navBurger = new NavigationBurger();
    public static ArrayList<GameInstance> allGameInstances = new ArrayList<GameInstance>();
    private int numberOfPlayers;
    private final static int maxPlayerCount = 6;
    private Random rand = new Random(SystemClock.uptimeMillis());

    private int height;
    private int width;
//    private int startingX;

    private Handler myhandler;
    private Timer timer;
    private long leafFallDuration = 24000;
    private ArrayList<Integer> speedList = new ArrayList<Integer>(Arrays.asList(20000, 21000, 22000, 23000, 24000, 25000, 26000, 27000, 28000));

    private ArrayList<Integer> items = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6));

    //    private TextView letterA;
    private String fallingLetter = "";
    private TextView letterTV;

    private int textViewCount = 10;
    private TextView[] textViewArray;
    private ConstraintLayout constraintLayoutMain;


    private ArrayList xPositionsArray = new ArrayList<Integer>();

    private TextView numberTV;

    private Spinner np;

    private Button startGameButton;

    private LetterPool myLetterPool;

    public int ANIMATION_COUNT = 0;
    public int ANIMATION_COUNT_MAX = 50;
    private int chosenPlayerCount = 1;


    public static int getMaxPlayerCount() {
        return maxPlayerCount;
    }
//    public GameData myGameData = new GameData(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Log.d(MONITOR_TAG, "Main activity, END");


        numberTV = (TextView) findViewById(R.id.numberTV);
        np = (Spinner) findViewById(R.id.numberPicker);
        np.setOnItemSelectedListener(itemSelectedListener);
        startGameButton = (Button) findViewById(R.id.bStartGame);

//        letterA = (TextView) findViewById(R.id.letterA);
        myhandler = new Handler();

        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item, items);

        np.setAdapter(adapter);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Total score is accumulated across game rounds. Returning to the main menu will clear it
//        GameActivity.myGameInstance.clearAllScores();
        numberOfPlayers = 1;
        Log.d(MONITOR_TAG, "Number of game instances: " + allGameInstances.size() + ", END");



        //We are going to use the height so we have to get it first
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        height = displaymetrics.heightPixels;
        width = displaymetrics.widthPixels;

        AssetManager assetManager = this.getAssets();
        try {
            InputStream myIpStr = assetManager.open("letterFrequency.txt");
            FoxDictionary.populateLetterDistribution(myIpStr);
            myIpStr.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        myLetterPool = new LetterPool(FoxDictionary.letterDistributionMap);

        textViewArray = new TextView[textViewCount];
        constraintLayoutMain = (ConstraintLayout) findViewById(R.id.contentMainxml);

        for(int j = 1; j < 18; j++){
            int num = (width/20)*j;
            xPositionsArray.add(num);
        }

        fillRandomTVs();
        restart();
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP){
            bringToFront();
        } else{
            bumpElevation();
        }


//        //this method is in charge to make the leaf fall from the top over and over again
//        leafFalling();
//        //we call the first rotation, and the first will call the second and the secon will call the first, recursively, then leaf is always moving
//        firstLeafRotation();

    }
    public void bringToFront(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                numberTV.bringToFront();
                np.bringToFront();
                startGameButton.bringToFront();
                bringToFront();
            }
        }, 1000);

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void bumpElevation(){

            numberTV.setElevation(1000);
            np.setElevation(1000);
            startGameButton.setElevation(1000);


    }


    public void restart(){

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                restart();
                fillRandomTVs();
            }
        }, 500);

    }

    public void fillRandomTVs(){

        final int drawableResourceId = this.getResources().getIdentifier("turquoise_background_rounded_border_textview", "drawable", this.getPackageName());

        final Context myContext = this;
        Collections.shuffle(speedList);
        if (ANIMATION_COUNT < ANIMATION_COUNT_MAX) {

                    TextView myTextView = new TextView(myContext);
                    fallingLetter = myLetterPool.getLetter();
                    myTextView.setText(fallingLetter);
                    myTextView.setTextSize(18);
                    myTextView.setWidth(100);
                    myTextView.setHeight(100);
                    myTextView.setGravity(1);
                    myTextView.setTextColor(Color.WHITE);
                    myTextView.setPadding(10,10,10,10);
                    myTextView.setBackgroundResource(drawableResourceId);

                    constraintLayoutMain.addView(myTextView);

                    //this method is in charge to make the leaf fall from the top over and over again
                    Integer myRand = randInt(0,xPositionsArray.size()-1);
                    int inn = (int) xPositionsArray.get(myRand);
                    leafFalling(inn, myTextView, speedList.get(ANIMATION_COUNT % speedList.size())/3);

            ANIMATION_COUNT++;
        }




    }
    public int randInt(int min, int max) {

        // NOTE: This will (intentionally) not run as written so that folks
        // copy-pasting have to think about how to initialize their
        // Random instance.  Initialization of the Random instance is outside
        // the main scope of the question, but some decent options are to have
        // a field that is initialized once and then re-used as needed or to
        // use ThreadLocalRandom (if using at least Java 1.7).
        //
        // In particular, do NOT do 'Random rand = new Random()' here or you
        // will get not very good / not very random results.
        // TODO fix this so it's random


        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;
        Log.d(TAG, "randInt: number is " + randomNum);
        Log.d(TAG, "randInt: max is " + max);
        Log.d(TAG, "randInt: min is " + min);
        return randomNum;
    }

    private void leafFalling(int startingX, final TextView letterBox, Integer duration) {


        //The trick here is, the animation is triggered, so the leaf start to fall
        //then, the timer is also set, to the exact same duration than the animation
        //So, when the animation is over, the timer will run out, calling the animation again
        //and setting the timer, recursively
        TranslateAnimation animation = new TranslateAnimation(startingX, startingX, -height/10, (float) (height*1.1));
        animation.setDuration(duration);
        letterBox.startAnimation(animation);



        myhandler.postDelayed(new Runnable(){
            @Override
            public void run(){

////                startingX = randInt(-width/2,width/2);
//                fallingLetter = myLetterPool.getLetter();
//                letterTV.setText(fallingLetter);
                constraintLayoutMain.removeView(letterBox);
                ANIMATION_COUNT -= 1;
//                fillRandomTVs();
            }
        }, duration);

    }

    void firstLeafRotation() {
        //The trick here is the same than above, but insted of calling it self when the timer run out
        //the second animation will be triggered, and the second will cal the first
//        letterA.animate().rotationBy(75).setDuration(leafRotationDuration).setInterpolator(new LinearInterpolator()).start();

        TimerTask reloadLeaf = new TimerTask() {
            @Override
            public void run() {
                secondLeafRotation();
            }
        };

//        timer.schedule(reloadLeaf, leafRotationDuration);
    }


    private void secondLeafRotation() {
//        letterA.animate().rotationBy(-75).setDuration(leafRotationDuration).setInterpolator(new LinearInterpolator()).start();

        //So when this timer is executed, the first rotation will be called, and will restart the movement
//        TimerTask reloadLeaf = new TimerTask() {
//            @Override
//            public void run() {
//                firstLeafRotation();
//            }
//        };
//
//        timer.schedule(reloadLeaf, leafRotationDuration);
    }

    // When user has choosen a name from the menu, set up the game instance
    private AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view,
                                   int position, long id) {
            // The first option is not a name, just a player number
            Integer choice = (Integer) parent.getItemAtPosition(position);
            chosenPlayerCount = choice;
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
            // TODO Auto-generated method stub
        }
    };

    public void startGameAct(View v) {
//        GameInstance myInstance = new GameInstance();
//        myInstance.startGame(this);



        numberOfPlayers = chosenPlayerCount;
        Log.d(MONITOR_TAG, "Number of players: " + numberOfPlayers + ", END");
        allGameInstances.clear();
//        for (int i=0; i<maxNumberOfRounds; i++){
//            this.roundIDs.add(UUID.randomUUID().toString());
//        }

        GameInstance thisGame = new GameInstance(0);
        allGameInstances.add(thisGame);
        for (int i = 1; i < numberOfPlayers; i++) {
            thisGame = new GameInstance(i, thisGame.getRoundIDs());
            allGameInstances.add(thisGame);
        }

        int indexOfGameInstance = 0;
        allGameInstances.get(indexOfGameInstance).clearAllScores(); // Is this necessary??  :S
        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra("game_index", indexOfGameInstance);
//            Log.d(MONITOR_TAG, "In startGame 2");
        this.startActivity(gameIntent);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (this.backButtonPressedOnce) {
                this.finishAffinity();
                return;
            }
            Toast.makeText(this, "Double tap BACK to exit!", Toast.LENGTH_SHORT).show();
            this.backButtonPressedOnce = true;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    backButtonPressedOnce = false;
                }
            }, 1500);

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
                Intent profileScreenIntent = new Intent(MainActivity.this, ProfileActivity.class);
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
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        Log.d(MONITOR_TAG, "Before_onNavigationItemSelected__MainActivity");
        navBurger.navigateTo(item, MainActivity.this);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
