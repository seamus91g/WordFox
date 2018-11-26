package capsicum.game.wordfox.screen_rank_detail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import capsicum.game.wordfox.GameData;
import capsicum.game.wordfox.ImageHandler;
import capsicum.game.wordfox.NavigationBurger;
import capsicum.game.wordfox.R;
import capsicum.game.wordfox.screen_profile.FoxRank;
import capsicum.game.wordfox.screen_profile.ProfileActivity;

public class RankDetailActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private NavigationBurger navBurger = new NavigationBurger();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_detail);
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
    }

    private void setup() {
        RecyclerView rankView = findViewById(R.id.rank_detail_rv);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        rankView.setLayoutManager(mLayoutManager);
        RankAdapter ra = new RankAdapter(getRankDataset());
        rankView.setAdapter(ra);
    }

    private List<RankDetailItem> getRankDataset() {
        float FOX_WIDTH_PERCENT = 0.2f;
        int foxWidth = (int) (FOX_WIDTH_PERCENT * calculateScreenWidth());
        List<RankDetailItem> rankDataset = new ArrayList<>();
        GameData p1Game = new GameData(this, GameData.getPlayer1Identity(this).ID);
        int highScore = p1Game.getHighestTotalScore();
        for (FoxRank rank : GameData.getFoxRanks()) {
            Bitmap foxBitmap;
            boolean isLocked = false;
            if ((GameData.rankScoreMin(rank.foxRank) > highScore) || (p1Game.getGameCount() == 0)) {
                isLocked = true;
                foxBitmap = ImageHandler.getScaledBitmapByWidth(GameData.getOutlineResource(rank.foxRank), foxWidth, getResources());
            } else {
                foxBitmap = ImageHandler.getScaledBitmapByWidth(rank.imageResource, foxWidth, getResources());
            }
            RankDetailItem rdi = new RankDetailItem(rank.foxRank,
                    foxBitmap,
                    GameData.rankScoreMin(rank.foxRank),
                    p1Game.getRankCount(rank.foxRank),
                    isLocked);
            rankDataset.add(rdi);
        }
        return rankDataset;
    }

    private int calculateScreenWidth() {
        Point screenSize = new Point();
        getWindowManager().getDefaultDisplay().getSize(screenSize);
        return screenSize.x;
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
                Intent profileScreenIntent = new Intent(RankDetailActivity.this, ProfileActivity.class);
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
        navBurger.navigateTo(item, RankDetailActivity.this);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }
}
