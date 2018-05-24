package com.example.seamus.wordfox.statistics_screen;

import android.graphics.Color;
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

import com.example.seamus.wordfox.GameData;
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
import com.example.seamus.wordfox.database.DataPerGame;
import com.example.seamus.wordfox.database.FoxSQLData;
import com.example.seamus.wordfox.datamodels.WordItem;
import com.example.seamus.wordfox.list.CardItem;
import com.example.seamus.wordfox.list.ExpandableHeaderItem;
import com.xwray.groupie.ExpandableGroup;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Statistics extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "WFAdapter";
    protected RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;
    private WFAdapter mAdapter;
    private ArrayList<DataListItem> gameData = new ArrayList<>();
    private Set<String> uniqueWords = new HashSet<>();
//    private List<WordItem> wordItems;
//    private FoxSQLData foxData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        populateRecycler();
    }

    private void populateRecycler() {

        mRecyclerView = (RecyclerView) findViewById(R.id.data_list);

        ArrayList<String> allPlayers = GameData.getNamedPlayerList(this);
        for (String playerName : allPlayers) {

            ArrayList<DataListItem> allCategories = new ArrayList<>();
            DataListItem playerHeader = new TypePlayer(playerName, allCategories);
            gameData.add(playerHeader);

            ///////// Get stats
            ArrayList<DataListItem> allDataItems = new ArrayList<>();
            DataListItem statsCategory = new TypeCategory("Stats", allDataItems);
            gameData.add(statsCategory);

//            DataListItem dataPoint = new TypeCategory();
            GameData playerGameData = new GameData(this, playerName);
            // Player X games
            allDataItems.add(new TypeStats("Games played: ", playerGameData.getGameCount()));
            // Played X round
            allDataItems.add(new TypeStats("Rounds played: ", playerGameData.getRoundCount()));
            // Longest word
            allDataItems.add(new TypeStats("Longest word: ", playerGameData.findLongest()));
            // Average word length
//            allDataItems.add(new TypeStats());
//            // Rounds where no word found
//            allDataItems.add(new TypeStats());
//            // Times each word length found
//            allDataItems.add(new TypeStats());
//            // Highest score in a game
//            allDataItems.add(new TypeStats());
//            // Number valid words submitted
//            allDataItems.add(new TypeStats());
//            // Invalid words submitted
//            allDataItems.add(new TypeStats());
//            // Times shuffled
//            allDataItems.add(new TypeStats());
//            // Average shuffles per round
//            allDataItems.add(new TypeStats());
            for(DataListItem item : allDataItems){
                gameData.add(item);
            }

            ///////// Get games
            ArrayList<DataListItem> allGameHeaders = new ArrayList<>();
            DataListItem gamesCategory = new TypeCategory("Games", allGameHeaders);
            allCategories.add(gamesCategory);
            gameData.add(gamesCategory);

            List<DataPerGame> allGameData = WordLoader.getGames(this, playerName);
            for (DataPerGame game : allGameData) {
                DataListItem dliHeader = new TypeGamesHeader(game);
                DataListItem dliGame = new TypeGamesDetail(game);
                allGameHeaders.add(dliHeader);
                gameData.add(dliHeader);
                gameData.add(dliGame);
            }

            ///////// Get words
            ArrayList<DataListItem> allWordHeaders = new ArrayList<>();
            DataListItem wordsCategory = new TypeCategory("Words", allWordHeaders);
            allCategories.add(wordsCategory);
            gameData.add(wordsCategory);
            ArrayList<WordDataHeader> pData = WordLoader.getValid(this, playerName); //TODO: Deprecate WordDataHeader/WordData, use only Type classes
            for (WordDataHeader wdh : pData) {
                DataListItem dli = new TypeWordsHeader(wdh);
                gameData.add(dli);
                allWordHeaders.add(dli);
                for (WordData wd : wdh.getChildList()) {
                    DataListItem wordInfo = new TypeWordsDetail(wd);
                    gameData.add(wordInfo);
                }
            }
        }

        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new WFAdapter(gameData);
        mRecyclerView.setAdapter(mAdapter);

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
