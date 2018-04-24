package com.example.seamus.wordfox;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.seamus.wordfox.dataWordsRecycler.WordData;
import com.example.seamus.wordfox.dataWordsRecycler.WordDataHeader;
import com.example.seamus.wordfox.database.DataPerGame;
import com.example.seamus.wordfox.databinding.ActivityDataPageBinding;
import com.example.seamus.wordfox.list.CardItem;
import com.example.seamus.wordfox.list.ExpandableHeaderItem;
import com.example.seamus.wordfox.profile.ProfileActivity;
import com.xwray.groupie.ExpandableGroup;
import com.xwray.groupie.GroupAdapter;
import com.xwray.groupie.example.core.decoration.DebugItemDecoration;
import com.xwray.groupie.example.core.decoration.HeaderItemDecoration;
import com.xwray.groupie.example.core.decoration.InsetItemDecoration;

import java.util.ArrayList;
import java.util.List;

public class DataPageActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationBurger navBurger = new NavigationBurger();

    private GridLayoutManager layoutManager;
    private GroupAdapter groupAdapter;
    //    private ContentDataPageBinding dataPageBinding;
    private ActivityDataPageBinding dataPageBinding;
    private int betweenPadding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_data_page);
        dataPageBinding = DataBindingUtil.setContentView(this, R.layout.activity_data_page);
        Log.d("Tag", "DP OnCreate 1");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        Log.d("Tag", "DP OnCreate 2");
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        betweenPadding = getResources().getDimensionPixelSize(R.dimen.padding_small);
        setup();
    }

    private void setup() {

        groupAdapter = new GroupAdapter();
        // Get all player names
        // Get words for each player
        // Get games for each player
        ArrayList<String> allPlayers = GameData.getNamedPlayerList(this);

        for (String player : allPlayers) {
            GameData playerGameData = new GameData(this.getApplicationContext(), player);
            if(playerGameData.getGameCount() == 0){
                continue;
            }
            //
            ExpandableHeaderItem playerName = new ExpandableHeaderItem(player);
            ExpandableGroup playerData = new ExpandableGroup(playerName);
            // Stats
            ExpandableHeaderItem statsSectionHeader = new ExpandableHeaderItem("STATS");
            ExpandableGroup statsSection = new ExpandableGroup(statsSectionHeader);

            statsSection.add(new CardItem(Color.BLUE, "Round count: " + playerGameData.getRoundCount()));
            statsSection.add(new CardItem(Color.BLUE, "Game count: " + playerGameData.getGameCount()));
            statsSection.add(new CardItem(Color.BLUE, "Highest total score: " + playerGameData.getHighestTotalScore()));

            playerData.add(statsSection);
            // Game data

            ExpandableHeaderItem gameSectionHeader = new ExpandableHeaderItem("GAMES");
            ExpandableGroup gameSection = new ExpandableGroup(gameSectionHeader);

            List<DataPerGame> allGames = WordLoader.getGames(this, player);
            for (DataPerGame game : allGames){
                String winners = game.winner;
                ExpandableHeaderItem gh = new ExpandableHeaderItem("Winner: " + winners);
                ExpandableGroup gameInfo = new ExpandableGroup(gh);
                gameInfo.add(new CardItem(Color.GRAY, "Round 1, Round 2, Round 3"));
                gameInfo.add(new CardItem(Color.GRAY, "Letters " + game.letters.get(0) + ", " + game.letters.get(1) + ", "+ game.letters.get(2)));
                gameInfo.add(new CardItem(Color.GRAY, "Longest " + game.bestPossible.get(0) + ", " + game.bestPossible.get(1) + ", "+ game.bestPossible.get(2)));
                for (String p : game.players){
                    List<String> pWords = game.wordsPerPlayer.get(p);
                    StringBuilder playerResult = new StringBuilder(p + " ");
                    for (String wor : pWords){
                        playerResult.append(wor);
                        playerResult.append(" ");
                    }
                    gameInfo.add(new CardItem(Color.GRAY, playerResult));
                }
                gameSection.add(gameInfo);
            }

            playerData.add(gameSection);
            // Word data
            ExpandableHeaderItem wordSectionHeader = new ExpandableHeaderItem("WORDS");
            ExpandableGroup wordSection = new ExpandableGroup(wordSectionHeader);

            ArrayList<WordDataHeader> pData = WordLoader.getValid(this, player);
            for (WordDataHeader wdh : pData) {
                ExpandableHeaderItem word = new ExpandableHeaderItem(wdh.getWordSubmitted());
                ExpandableGroup wordData = new ExpandableGroup(word);
                for (WordData wd : wdh.getChildList()) {
                    CardItem dataPoint = new CardItem(Color.BLUE, wd.getGivenLetters() + " " + wd.getLongestPossibleWord());
                    wordData.add(dataPoint);
                }
                wordSection.add(wordData);
            }
            playerData.add(wordSection);

//            playerData.add("Stats");
            groupAdapter.add(playerData);
        }


        ////////////////////////////////////////////////////
        ////////////////////////////////////////////////////
        layoutManager = new GridLayoutManager(this, groupAdapter.getSpanCount());
        layoutManager.setSpanSizeLookup(groupAdapter.getSpanSizeLookup());

        final RecyclerView recyclerView = dataPageBinding.recyclerView;
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new HeaderItemDecoration(Color.GRAY, betweenPadding, R.layout.item_header));
        recyclerView.addItemDecoration(new InsetItemDecoration(Color.GRAY, betweenPadding, CardItem.INSET_TYPE_KEY, CardItem.INSET));
        recyclerView.addItemDecoration(new DebugItemDecoration(this));
        recyclerView.setAdapter(groupAdapter);
    }


//////////////////////////////////////////////////////////////////////////////////////////////////

    //////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
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
                Intent profileScreenIntent = new Intent(DataPageActivity.this, ProfileActivity.class);
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
        navBurger.navigateTo(item, DataPageActivity.this);
        return true;
    }
}
//////////////////////////////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////////////////////////////////////////////////////////////
