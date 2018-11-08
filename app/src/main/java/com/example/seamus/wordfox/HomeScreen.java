package com.example.seamus.wordfox;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.seamus.wordfox.data.FoxDictionary;
import com.example.seamus.wordfox.game_screen.GameActivity;
import com.example.seamus.wordfox.profile.ProfileActivity;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

import static com.example.seamus.wordfox.IVmethods.getImageScaleToScreenWidthPercent;

public class HomeScreen extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static ArrayList<GameInstance> allGameInstances = new ArrayList<>();
    private NavigationBurger navBurger = new NavigationBurger();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        new Thread(() -> {
            if (isServiceRunning(WifiService.class)) {
                stopService(new Intent(HomeScreen.this, WifiService.class));
            }
        }).start();

        loadDictionary();
        setup();
        MobileAds.initialize(this, "ca-app-pub-5181377347442835~1259786879");


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        int width = size. x;
        int height = size. y;



        ImageView instructionFoxIV = findViewById(R.id.content_home_screen_instructionFoxIV);
        instructionFoxIV.setImageBitmap(ImageHandler.getScaledBitmap(R.drawable.woodfoxcoloured,
                getImageScaleToScreenWidthPercent(this, 0.4, R.drawable.woodfoxcoloured),getResources()));


        ImageView instructionFoxSpeechBubbleIV = findViewById(R.id.content_home_screen_instructionFoxSpeechBubbleIV);
        instructionFoxSpeechBubbleIV.setImageBitmap(ImageHandler.getScaledBitmap(R.drawable.speechbubbleright,
                getImageScaleToScreenWidthPercent(this, 0.59, R.drawable.speechbubbleright), getResources()));

        TextView instructionFoxTV = findViewById(R.id.content_home_screen_instructionFoxTV);
        IVmethods.setTVwidthPercentOfIV(instructionFoxSpeechBubbleIV,instructionFoxTV,0.8, R.string.content_home_screen_fox_instructions1);

        TextView justMeTV = findViewById(R.id.just_me_text);
        justMeTV.requestLayout();
        justMeTV.getLayoutParams().width = (int) width/2 ;
        TextView justMeExplanationTV = findViewById(R.id.just_me_explanation_text);
        justMeExplanationTV.requestLayout();
        justMeExplanationTV.getLayoutParams().width = (int) width/2 ;


        TextView withFriendsTV = findViewById(R.id.with_friends_text);
        withFriendsTV.requestLayout();
        withFriendsTV.getLayoutParams().width = (int) width/2 ;
        TextView withFriendsExplanationTV = findViewById(R.id.with_friends_explanation_text);
        withFriendsExplanationTV.requestLayout();
        withFriendsExplanationTV.getLayoutParams().width = (int) width/2 ;


        TextView passPlayTV = findViewById(R.id.pass_and_play_text);
        passPlayTV.requestLayout();
        passPlayTV.getLayoutParams().width = (int) width/2 ;
        TextView passPlayExplanationTV = findViewById(R.id.pass_and_play_explanation_text);
        passPlayExplanationTV.requestLayout();
        passPlayExplanationTV.getLayoutParams().width = (int) width/2 ;




    }

    public void loadDictionary() {
        if (FoxDictionary.isWordListLoaded) {
            return;
        }
        Thread thread = new Thread(() -> FoxDictionary.loadWords("word_list_with_alphagram.txt", "letterFrequency.txt", getAssets()));
        thread.start();
    }
    private void setup() {
        ImageButton justMeButton = findViewById(R.id.just_me_button);
        ImageButton withFriendsButton = findViewById(R.id.with_friends_button);
        ImageButton passPlayButton = findViewById(R.id.pass_and_play_button);

        justMeButton.setOnClickListener(justMeListener);
        withFriendsButton.setOnClickListener(withFriendsListener);
        passPlayButton.setOnClickListener(passPlayListener);

    }

    View.OnClickListener justMeListener = view -> startJustMe();

    View.OnClickListener withFriendsListener = view -> startWithFriends();

    View.OnClickListener passPlayListener = view -> startPassPlay();

    public void startWithFriends() {
        Intent wifiIntent = new Intent(this, LocalWifiActivity.class);
        startActivity(wifiIntent);
    }

    public void startPassPlay() {
        Intent wifiIntent = new Intent(this, PassAndPlay.class);
        startActivity(wifiIntent);
    }

    public void startJustMe(){

        allGameInstances.clear();

        PlayerIdentity playerOne = GameData.getPlayer1Identity(this);
        GameInstance playerOneGame = new GameInstance(playerOne.ID, playerOne.username, 0);
        allGameInstances.add(playerOneGame);

        Intent gameIntent = new Intent(this, GameActivity.class);
        gameIntent.putExtra(GameActivity.GAME_INDEX, 0);

        waitForDictionaryToLoad();
        this.startActivity(gameIntent);
    }
    
    private void waitForDictionaryToLoad(){
        // Wait for dictionary to finish loading
        while (!FoxDictionary.isWordListLoaded) {
            Log.d(WordfoxConstants.MONITOR_TAG, "Dictionary word list is not finished loading!");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                // User chose the "Profile" item, jump to the profile page
                Intent profileScreenIntent = new Intent(HomeScreen.this, ProfileActivity.class);
                startActivity(profileScreenIntent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        navBurger.navigateTo(item, HomeScreen.this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
