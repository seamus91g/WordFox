package com.example.seamus.wordfox;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import com.example.seamus.wordfox.data.FoxDictionary;
import com.example.seamus.wordfox.game_screen.GameActivity;


public class PassAndPlay extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static boolean firstTime = true;
    public static final String MONITOR_TAG = "myTag";
    private int numberOfPlayers = -1;
    Bitmap myBitmap1;
    Bitmap myBitmap2;
    Bitmap myBitmap3;
    Bitmap myBitmap33;
    Bitmap myBitmap4;
    Bitmap myBitmap44;
    Bitmap myBitmap5;
    Bitmap myBitmap55;
    Bitmap myBitmap6;
    Bitmap myBitmap66;

//    public static ArrayList<GameInstance> allGameInstances = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_and_play);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        setup();




        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int width = size. x;
        int height = size. y;

        SeekBar mySB = findViewById(R.id.seekBar);
        mySB.getProgressDrawable().setColorFilter(getResources().getColor(R.color.colorPrimary), PorterDuff.Mode.SRC_IN);

        numberOfPlayers = 2;
        Log.d(MONITOR_TAG, "Number of players: " + numberOfPlayers + ", END");







        mySB.requestLayout();
        mySB.getLayoutParams().width = (int) width/2 ;


        ImageView myIV = findViewById(R.id.fox_instructions_passandplay);
        myIV.requestLayout();
        myIV.setImageBitmap(ImageHandler.getScaledBitmap(R.drawable.woodfoxcolouredwithspeech, (int) (0.5*width),getResources()));



        myBitmap1 = ImageHandler.getScaledBitmap(R.drawable.silverfoxsilcoloured, (int) (0.1*width),getResources());
        myBitmap2 = ImageHandler.getScaledBitmap(R.drawable.arcticfoxsilcoloured, (int) (0.1*width),getResources());
        myBitmap3 = ImageHandler.getScaledBitmap(R.drawable.gameend_outline, (int) (0.1*width),getResources());
        myBitmap33 = ImageHandler.getScaledBitmap(R.drawable.gameendsilcoloured, (int) (0.1*width),getResources());
        myBitmap4 = ImageHandler.getScaledBitmap(R.drawable.grayfox_outline, (int) (0.1*width),getResources());
        myBitmap44 = ImageHandler.getScaledBitmap(R.drawable.grayfoxsilcoloured, (int) (0.1*width),getResources());
        myBitmap5 = ImageHandler.getScaledBitmap(R.drawable.kitfox_outline, (int) (0.1*width),getResources());
        myBitmap55 = ImageHandler.getScaledBitmap(R.drawable.kitfoxsilcoloured, (int) (0.1*width),getResources());
        myBitmap6 = ImageHandler.getScaledBitmap(R.drawable.redfox_outline, (int) (0.1*width),getResources());
        myBitmap66 = ImageHandler.getScaledBitmap(R.drawable.redfoxsilcoloured, (int) (0.1*width),getResources());


        ImageView fox1IV = findViewById(R.id.foxes1);
        ImageView fox2IV = findViewById(R.id.foxes2);
        ImageView fox3IV = findViewById(R.id.foxes3);
        ImageView fox4IV = findViewById(R.id.foxes4);
        ImageView fox5IV = findViewById(R.id.foxes5);
        ImageView fox6IV = findViewById(R.id.foxes6);

        fox1IV.setImageBitmap(myBitmap1);
        fox2IV.setImageBitmap(myBitmap2);
        fox3IV.setImageBitmap(myBitmap3);
        fox4IV.setImageBitmap(myBitmap4);
        fox5IV.setImageBitmap(myBitmap5);
        fox6IV.setImageBitmap(myBitmap6);


        mySB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {


            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                switch (progress){

                    case 0:
                        numberOfPlayers = 2;
                        fox3IV.setImageBitmap(myBitmap3);
                        fox4IV.setImageBitmap(myBitmap4);
                        fox5IV.setImageBitmap(myBitmap5);
                        fox6IV.setImageBitmap(myBitmap6);
                        break;
                    case 1:
                        numberOfPlayers = 3;
                        fox3IV.setImageBitmap(myBitmap33);
                        fox4IV.setImageBitmap(myBitmap4);
                        fox5IV.setImageBitmap(myBitmap5);
                        fox6IV.setImageBitmap(myBitmap6);
                        break;
                    case 2:
                        numberOfPlayers = 4;
                        fox3IV.setImageBitmap(myBitmap33);
                        fox4IV.setImageBitmap(myBitmap44);
                        fox5IV.setImageBitmap(myBitmap5);
                        fox6IV.setImageBitmap(myBitmap6);
                        break;
                    case 3:
                        numberOfPlayers = 5;
                        fox3IV.setImageBitmap(myBitmap33);
                        fox4IV.setImageBitmap(myBitmap44);
                        fox5IV.setImageBitmap(myBitmap55);
                        fox6IV.setImageBitmap(myBitmap6);
                        break;
                    case 4:
                        numberOfPlayers = 6;
                        fox3IV.setImageBitmap(myBitmap33);
                        fox4IV.setImageBitmap(myBitmap44);
                        fox5IV.setImageBitmap(myBitmap55);
                        fox6IV.setImageBitmap(myBitmap66);
                        break;
                }

                Log.d(MONITOR_TAG, "Number of players: " + numberOfPlayers + ", END");
                HomeScreen.allGameInstances.clear();


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }


        });

    }

    private void setup() {
        Button startButton = findViewById(R.id.bStartPAP);
        startButton.setOnClickListener(startListener);
    }
    View.OnClickListener startListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            startGame();
        }
    };

    public void startGame() {
        if (numberOfPlayers < 2 || numberOfPlayers > 6){
            Log.d(MONITOR_TAG, "Returning. Number of players is: " + numberOfPlayers);
            return;
        }
        HomeScreen.allGameInstances.clear();

        PlayerIdentity playerOne = GameData.getPlayer1Identity(this);
        GameInstance playerOneGame = new GameInstance(playerOne.ID, playerOne.username, 0, numberOfPlayers);
        HomeScreen.allGameInstances.add(playerOneGame);

//        ArrayList<PlayerIdentity> players = GameData.fetchSomeIdentities(numberOfPlayers - 1, this);    // TODO: Include p1 in this, seems pointless loading p1 separately
//        for (int i = 0; i < players.size(); i++) {
//            GameInstance thisGame = new GameInstance(players.get(i).ID, players.get(i).username, i + 1);
//            HomeScreen.allGameInstances.add(thisGame);
//        }

        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra(GameActivity.GAME_INDEX, 0);

        // Wait for dictionary to finish loading
        while (!FoxDictionary.isWordListLoaded) {
            Log.d(MONITOR_TAG, "Dictionary word list is not finished loading!");
            try {
                Thread.sleep(100);      // Wait for dictionary to finish loading
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Log.d(MONITOR_TAG, "Number of players is: " + numberOfPlayers + ". Game instances: " + HomeScreen.allGameInstances.size());
        this.startActivity(gameIntent);
    }

    public void setNumPlayers(View myView) {
        String Players = (String) myView.getTag();

        switch (Players) {
            case "2players":
                numberOfPlayers = 2;
                changeTextTV();
                break;
            case "3players":
                numberOfPlayers = 3;
                changeTextTV();
                break;
            case "4players":
                numberOfPlayers = 4;
                changeTextTV();
                break;
            case "5players":
                numberOfPlayers = 5;
                changeTextTV();
                break;
            case "6players":
                numberOfPlayers = 6;
                changeTextTV();
                break;
            default:
                numberOfPlayers = -1;
                changeTextTV();
                break;
        }

        Log.d(MONITOR_TAG, "Number of players: " + numberOfPlayers + ", END");
    }

    private void changeTextTV() {   // TODO: Implement speech
//        TextView myTV = (TextView) findViewById(R.id.speechTV);
//        String numPlayersChosen;
//        if (numberOfPlayers == -1) {
//            numPlayersChosen = "Choose number of players!";
//        } else {
//            numPlayersChosen = "Press START to begin your " + numberOfPlayers + " player game!!";
//        }
//        myTV.setText(numPlayersChosen);
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
        getMenuInflater().inflate(R.menu.pass_and_play, menu);
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
