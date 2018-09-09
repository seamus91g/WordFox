package com.example.seamus.wordfox.statistics_screen;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.example.seamus.wordfox.FoxUtils;
import com.example.seamus.wordfox.GameData;
import com.example.seamus.wordfox.GameInstance;
import com.example.seamus.wordfox.ImageHandler;
import com.example.seamus.wordfox.MainActivity;
import com.example.seamus.wordfox.NavigationBurger;
import com.example.seamus.wordfox.R;
import com.example.seamus.wordfox.RV.DataListItem;
import com.example.seamus.wordfox.RV.RVTypes.TypeCategory;
import com.example.seamus.wordfox.RV.RVTypes.TypeGamesDetail;
import com.example.seamus.wordfox.RV.RVTypes.TypeGamesHeader;
import com.example.seamus.wordfox.RV.RVTypes.TypePlayer;
import com.example.seamus.wordfox.RV.RVTypes.TypeStats;
import com.example.seamus.wordfox.RV.RVTypes.TypeWordsDetail;
import com.example.seamus.wordfox.RV.RVTypes.TypeWordsHeader;
import com.example.seamus.wordfox.RV.WFAdapter;
import com.example.seamus.wordfox.WordLoader;
import com.example.seamus.wordfox.dataWordsRecycler.WordData;
import com.example.seamus.wordfox.dataWordsRecycler.WordDataHeader;
//import com.example.seamus.wordfox._junk.DataPageActivity;
import com.example.seamus.wordfox.database.DataPerGame;
import com.example.seamus.wordfox.database.FoxSQLData;
import com.example.seamus.wordfox.datamodels.WordItem;
import com.example.seamus.wordfox.profile.FoxRank;
import com.example.seamus.wordfox.profile.ProfileActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Statistics extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    protected RecyclerView mRecyclerView;
    private ArrayList<DataListItem> gameData = new ArrayList<>();
    private NavigationBurger navBurger = new NavigationBurger();

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

        populateRecycler();
    }

    private void populateRecycler() {

        mRecyclerView = (RecyclerView) findViewById(R.id.data_list);

        ArrayList<String> allPlayers = GameData.getNamedPlayerList(this);
        final List<DataPerGame> allGameData = WordLoader.getGames(this);
        for (String playerName : allPlayers) {
            GameData playerGameData = new GameData(this, playerName);
            ArrayList<DataListItem> allCategories = new ArrayList<>();
            Bitmap profPic = loadPlayerBitmap(playerGameData.getProfilePicture());
            FoxRank foxRank = GameData.determineRankClass(playerGameData.getRank());
            DataListItem playerHeader = new TypePlayer(playerGameData.getUsername(), allCategories, profPic, foxRank.foxRank);
            gameData.add(playerHeader);

            ///////// Get stats
            DataListItem statsCategory = new TypeCategory("Stats", createStats(playerGameData));
            allCategories.add(statsCategory);

            ///////// Get games
            DataListItem gamesCategory = new TypeCategory("Games", createGameData(playerGameData.getUsername(), playerName, allGameData));
            allCategories.add(gamesCategory);

            ///////// Get words
            DataListItem wordsCategory = new TypeCategory("Words", createWordData(playerName));
            allCategories.add(wordsCategory);

        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        WFAdapter mAdapter = new WFAdapter(gameData);
        mRecyclerView.setAdapter(mAdapter);

    }

    private ArrayList<DataListItem> createWordData(String playerName){

        ArrayList<DataListItem> allWordHeaders = new ArrayList<>();
        ArrayList<WordDataHeader> pData = WordLoader.getValid(this, playerName); //TODO: Deprecate WordDataHeader/WordData, use only Type classes
        if(pData.size() == 0){
            allWordHeaders.add(new TypeStats<>("No words found yet!", ""));
        }
        for (WordDataHeader wdh : pData) {
            DataListItem dli = new TypeWordsHeader(wdh);
            allWordHeaders.add(dli);
        }
        return allWordHeaders;
    }


    private ArrayList<DataListItem> createGameData(String username, String ID, List<DataPerGame> allGameData){

        ArrayList<DataListItem> allGameHeaders = new ArrayList<>();
        if(allGameData.size() == 0){
            allGameHeaders.add(new TypeStats<>("No games played yet!", ""));
        }
        for (DataPerGame game : allGameData) {
            if (!game.players.contains(ID)) {
                continue;
            }
            if(game.players.contains(GameData.DEFAULT_P1_NAME)){    // TODO: Surely will always be true?
                game.swapName(GameData.DEFAULT_P1_NAME, username);
            }
            DataListItem dliGame = new TypeGamesDetail(game);  // get an ID for each view we will require
            DataListItem dliHeader = new TypeGamesHeader(game, dliGame);
            allGameHeaders.add(dliHeader);
        }
        return allGameHeaders;
    }

    private ArrayList<DataListItem> createStats(GameData playerGameData) {
        ArrayList<DataListItem> allDataItems = new ArrayList<>();
        if(playerGameData.getGameCount() == 0){
            allDataItems.add(new TypeStats<>("No games played yet!", ""));
            return allDataItems;
        }
        // Player X games
        allDataItems.add(new TypeStats<>("Games played: ", playerGameData.getGameCount()));
        // Played X round
        allDataItems.add(new TypeStats<>("Rounds played: ", playerGameData.getRoundCount()));
        // Longest word
        allDataItems.add(new TypeStats<>("Longest word: ", playerGameData.findLongest()));
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
        Bitmap profPic = null;
        ImageHandler imageHandler = new ImageHandler(this);     // Handle this better
        if (!profPicStr.equals("")) {
            Uri myFileUri = Uri.parse(profPicStr);
            profPic = imageHandler.getBitmapFromUri(myFileUri, 120);
//            int scale = ImageHandler.getScaleFactor(getResources(), )
        }
        return profPic;
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
        getMenuInflater().inflate(R.menu.statistics, menu);
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
