package com.example.seamus.wordfox.statistics_screen;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.ImageHandler;
import com.example.seamus.wordfox.NavigationBurger;
import com.example.seamus.wordfox.PlayerIdentity;
import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.RV.DataListItem;
import com.example.seamus.wordfox.RV.RVTypes.TypeAdvert;
import com.example.seamus.wordfox.RV.RVTypes.TypeCategory;
import com.example.seamus.wordfox.RV.RVTypes.TypeGamesDetail;
import com.example.seamus.wordfox.RV.RVTypes.TypeGamesHeader;
import com.example.seamus.wordfox.RV.RVTypes.TypeHeadingImage;
import com.example.seamus.wordfox.RV.RVTypes.TypePlayer;
import com.example.seamus.wordfox.RV.RVTypes.TypeStats;
import com.example.seamus.wordfox.RV.RVTypes.TypeWordsHeader;
import com.example.seamus.wordfox.RV.WFAdapter;
import com.example.seamus.wordfox.WordLoader;
import com.example.seamus.wordfox.WordfoxConstants;
import com.example.seamus.wordfox.dataWordsRecycler.WordDataHeader;
import com.example.seamus.wordfox.database.DataPerGame;
import com.example.seamus.wordfox.profile.FoxRank;
import com.example.seamus.wordfox.profile.ProfileActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Statistics extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static int PROFILE_PIC_SCREEN_WIDTH_PERCENT = 20;
    private static int DATA_FOX_SCREEN_WIDTH_PERCENT = 30;
    protected RecyclerView mRecyclerView;
    private ArrayList<DataListItem> gameData = new ArrayList<>();
    private NavigationBurger navBurger = new NavigationBurger();
    private Bitmap defaultPicture = null;
    int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        headingAndBanner();
        populateRecycler();
    }

    private void headingAndBanner() {
        int foxWidth = (screenWidth * DATA_FOX_SCREEN_WIDTH_PERCENT) / 100;
        int speechWidth = foxWidth * 2;
        Bitmap heading = ImageHandler.getScaledBitmapByWidth(R.drawable.datafoxsilcoloured, foxWidth, getResources());
        Bitmap speechBubble = ImageHandler.getScaledBitmapByWidth(R.drawable.speechbubbleleft, speechWidth, getResources());
        DataListItem headingImage = new TypeHeadingImage(heading, speechBubble);
        gameData.add(headingImage);

        DataListItem bannerAdvert = new TypeAdvert(loadAdBanner(this));
        gameData.add(bannerAdvert);
    }

    private AdView loadAdBanner(Context context) {
        AdView mAdView = new AdView(context);
        mAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
        mAdView.setAdUnitId(context.getResources().getString(R.string.test_banner_ad_unit_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER;
        mAdView.setLayoutParams(lp);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
                findViewById(R.id.adViewLL).setVisibility(View.GONE);
            }
        });
        return mAdView;
    }

    private void populateRecycler() {

        mRecyclerView = (RecyclerView) findViewById(R.id.data_list);

        ArrayList<PlayerIdentity> allPlayers = GameData.getPlayerList(this);
        final List<DataPerGame> allGameData = WordLoader.getGames(this);
        for (PlayerIdentity identity : allPlayers) {
            GameData playerGameData = new GameData(this, identity.ID);
            // Don't display players with no data, except player one
            if (playerGameData.getGameCount() == 0 && !allPlayers.get(0).equals(identity)) {
                continue;
            }
            ArrayList<DataListItem> allCategories = new ArrayList<>();
            Bitmap profPic = loadPlayerBitmap(playerGameData.getProfilePicture());
            FoxRank foxRank = playerGameData.getHighRank();
            DataListItem playerHeader = new TypePlayer(playerGameData.getUsername(), allCategories, profPic, foxRank.foxRank);
            gameData.add(playerHeader);

            ///////// Get stats
            DataListItem statsCategory = new TypeCategory("STATS", createStats(playerGameData));
            allCategories.add(statsCategory);

            ///////// Get games
            DataListItem gamesCategory = new TypeCategory("GAMES", createGameData(identity.ID, allGameData));
            allCategories.add(gamesCategory);

            ///////// Get words
            DataListItem wordsCategory = new TypeCategory("WORDS", createWordData(identity.ID));
            allCategories.add(wordsCategory);

        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        WFAdapter mAdapter = new WFAdapter(gameData);
        mRecyclerView.setAdapter(mAdapter);
    }

    private ArrayList<DataListItem> createWordData(UUID playerID) {

        ArrayList<DataListItem> allWordHeaders = new ArrayList<>();
        ArrayList<WordDataHeader> pData = WordLoader.getValid(this, playerID); //TODO: Deprecate WordDataHeader/WordData, use only Type classes
        if (pData.size() == 0) {
            allWordHeaders.add(new TypeStats<>("No words found yet!", ""));
        }
        for (WordDataHeader wdh : pData) {
            DataListItem dli = new TypeWordsHeader(wdh);
            allWordHeaders.add(dli);
        }
        return allWordHeaders;
    }

    private ArrayList<DataListItem> createGameData(UUID ID, List<DataPerGame> allGameData) {
        ArrayList<DataListItem> allGameHeaders = new ArrayList<>();
        for (DataPerGame game : allGameData) {
            if (!containsPlayer(game.players, ID)) {
                continue;
            }
            boolean isJustMe = (game.players.size() == 1);
            DataListItem dliGame = new TypeGamesDetail(game);  // get an ID for each view we will require
            DataListItem dliHeader = new TypeGamesHeader(game, dliGame, isJustMe);
            allGameHeaders.add(dliHeader);
        }
        if (allGameHeaders.size() == 0) {
            allGameHeaders.add(new TypeStats<>("No games played yet!", ""));
        }
        return allGameHeaders;
    }

    private boolean containsPlayer(ArrayList<PlayerIdentity> players, UUID ID) {
        for (PlayerIdentity p : players) {        // TODO: Seems like this check shouldn't be necessary
            if (p.ID.equals(ID)) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<DataListItem> createStats(GameData playerGameData) {
        ArrayList<DataListItem> allDataItems = new ArrayList<>();
        if (playerGameData.getGameCount() == 0) {
            allDataItems.add(new TypeStats<>("No games played yet!", ""));
            return allDataItems;
        }
        // Player X games
        allDataItems.add(new TypeStats<>("Games played: ", playerGameData.getGameCount()));
        // Played X round
        allDataItems.add(new TypeStats<>("Rounds played: ", playerGameData.getRoundCount()));
        // Longest word
        String longestWord = playerGameData.findLongest() + " (" + playerGameData.findLongest().length() + ")";
        allDataItems.add(new TypeStats<>("Longest word: ", longestWord));
        // Average word length
        allDataItems.add(new TypeStats<>("Average word length: ", playerGameData.getAverageWordLength()));
        // Rounds where no word found
        allDataItems.add(new TypeStats<>("Round where no word found: ", playerGameData.getNoneFoundCount()));
        // Times each word length found
        for (int i = 3; i <= 9; ++i) {
            allDataItems.add(new TypeStats<>("Length " + i + " words found: ", playerGameData.findOccurence(i)));
        }
        // Highest score in a game
        allDataItems.add(new TypeStats<>("Highest score: ", playerGameData.getHighestTotalScore()));
        // Number valid words submitted
        allDataItems.add(new TypeStats<>("Valid words submitted: ", playerGameData.getSubmittedCorrectCount()));
        // Invalid words submitted
        allDataItems.add(new TypeStats<>("Invalid words submitted: ", playerGameData.getSubmittedIncorrectCount()));
        // Times shuffled
        allDataItems.add(new TypeStats<>("Times shuffled: ", playerGameData.getShuffleCount()));
        // Average shuffles per round
        allDataItems.add(new TypeStats<>("Average shuffles per round: ", playerGameData.getShuffleAverage()));
        return allDataItems;
    }

    private Bitmap loadPlayerBitmap(String profPicStr) {
        int profPicWidth = (screenWidth * PROFILE_PIC_SCREEN_WIDTH_PERCENT) / 100;
        if (!profPicStr.equals("")) {
            Uri myFileUri = Uri.parse(profPicStr);
            return ImageHandler.getBitmapFromUriScaleWidth(this, myFileUri, profPicWidth);    // TODO: Set as percent
        } else {
            if (defaultPicture == null) {     // Only load if needed
                defaultPicture = ImageHandler.getScaledBitmapByWidth(GameData.PROFILE_DEFAULT_IMG, profPicWidth, getResources());
            }
            return defaultPicture;
        }
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
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                // User chose the "Profile" item, jump to the profile page
                Intent profileScreenIntent = new Intent(Statistics.this, ProfileActivity.class);
                startActivity(profileScreenIntent);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        navBurger.navigateTo(item, Statistics.this);
        return true;
    }
}
