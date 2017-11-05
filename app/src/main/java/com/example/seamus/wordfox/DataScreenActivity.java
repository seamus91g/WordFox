package com.example.seamus.wordfox;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.support.constraint.ConstraintSet.BOTTOM;
import static android.support.constraint.ConstraintSet.TOP;

public class DataScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationBurger navBurger = new NavigationBurger();
    private GameData myGameData;
    private int colorIndex = 0;         // Every second color is different
    public static final String MONITOR_TAG = "myTag";
    static int id = 1; //   Support for API 16
    private ArrayList<Integer> allUniqueIds = new ArrayList<Integer>(); // Unique ID for each generated xml view

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // First ID is the image at the top. All other views are constrained to this
        allUniqueIds.add(R.id.data_heading_image);
        // Get list of created player names
        ArrayList<String> players = GameData.getNamedPlayerList(this);
        // Iterate through each player which has data
        int count = 0;
        while (count < MainActivity.getMaxPlayerCount()){
            // Get all named players
            if (players.size() > 0 && count > 0){
                String playerName = players.remove(0);
                myGameData = new GameData(this.getApplicationContext(), playerName);
            // Get all numbered players
            }else{
                myGameData = new GameData(this.getApplicationContext(), count);
                ++count;
            }
            // If there's no data, skip to the next one
            if(myGameData.getGameCount() == 0){
                continue;
            }
            // Username section
            setTextView("Username", "HEADER");
            setTextView("" + myGameData.getUsername(), "DATA");
            // Games section
            setTextView("Games", "HEADER");
            setTextView("Played " + myGameData.getGameCount() + " games", "DATA");
            setTextView("Played " + myGameData.getRoundCount() + " rounds", "DATA");    //
            // Words section
            setTextView("Words", "HEADER");
            setTextView("Longest word: " + myGameData.findLongest(), "DATA");    //
            setTextView("Average word length: " + myGameData.getAverageWordLength(), "DATA");    //
            setTextView("Rounds where no word found: " + myGameData.getNoneFoundCount(), "DATA");    //
            updateWordOccurences();
            // Data section
            setTextView("DATA", "HEADER");
            setTextView("Highest score: " + myGameData.getHighestTotalScore(), "DATA");    //
            setTextView("Valid words submitted: " + myGameData.getSubmittedCorrectCount(), "DATA");    //
            setTextView("Invalid words submitted: " + myGameData.getSubmittedIncorrectCount(), "DATA");    //
            setTextView("Total shuffles: " + myGameData.getShuffleCount(), "DATA");    //
            setTextView("Average shuffles per round: " + myGameData.getShuffleAverage(), "DATA");    //
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
    // Create a text view, constrained and styled appropriately
    private void setTextView(String text, String type){
        ConstraintLayout consLayout = (ConstraintLayout) findViewById(R.id.wholePageData);
        ConstraintSet set = new ConstraintSet();
        TextView dataTextView;
        int newId = getUniqueId();
        allUniqueIds.add(newId);
        dataTextView = createDataPageTextView(text, type);
        dataTextView.setId(newId);
        consLayout.addView(dataTextView);
        set.clone(consLayout);
        set = attachUnderneath(newId, allUniqueIds.get(allUniqueIds.size() -2), set);
        set.applyTo(consLayout);
    }

    private ConstraintSet attachUnderneath(int idBottom, int idTop, ConstraintSet set){
        set.connect(idBottom, ConstraintSet.TOP, idTop, ConstraintSet.BOTTOM, 0);
        set.connect(idBottom, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT, 0);
        set.connect(idBottom, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT, 0);
        return set;
    }
    // Apply appropriate styles depending on if it's a header or data point
    private TextView createDataPageTextView(String dataText, String type) {
        TextView textView = new TextView(this);
        int dataStyle = 0;
        int colorId = 0;
        if (type.equals("HEADER")){
            dataStyle = R.style.dataTextHeadingStyle; // Load a style and a color for the text view
            colorId = R.color.colorTextHeadingBG;
            int dimenPx = dp2px(8);
            textView.setPadding(dimenPx, dimenPx, dimenPx, dimenPx);
        }else if(type.equals("DATA")){
            dataStyle = R.style.dataTextStyle;
            if (++colorIndex % 2 == 0) {
                colorId = R.color.colorTextDataBG;
            } else {
                colorId = R.color.colorTextDataBGAlt;
            }
            textView.setPadding(16, 2, 16, 2);
        }
        if (colorId != 0 && dataStyle != 0 ) {
            int color;
            if (Build.VERSION.SDK_INT < 23) {
                textView.setTextAppearance(this, dataStyle);
                color = getResources().getColor(colorId);
            } else {
                textView.setTextAppearance(dataStyle);
                color = ContextCompat.getColor(this, colorId);
            }
            // Find the width of the screen. Text view width should be equal to this
            DisplayMetrics dm = new DisplayMetrics();
            this.getWindow().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int parentWidth = dm.widthPixels;
            // Set all desired text view attributes
            textView.setText(dataText);
            textView.setBackgroundColor(color);
            textView.setWidth(parentWidth);
        }
        return textView;
    }
    // Convert from dp to pixels
    public int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, this.getResources().getDisplayMetrics());
    }
    // Print number of times each length was found
    public void updateWordOccurences() {
        for (int i = 3; i <= 9; i++) {
            int frequencyOccured = myGameData.findOccurence(i);
            String frequencyOccuredStr = Integer.toString(frequencyOccured);
            String textToDisplay = "Found " + frequencyOccuredStr + " words of length " + i;
            setTextView(textToDisplay, "DATA");
        }
    }
    // Returns a valid id that isn't in use
    public int getUniqueId() {
        int newId;
        if (Build.VERSION.SDK_INT < 17) {
            View v = findViewById(id);
            while (v != null){
                v = findViewById(++id);
            }
            newId = id++;
        } else {
            newId = View.generateViewId();
        }
        return newId;
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
                Log.d(MONITOR_TAG, "Chose des's profile icon, END");
                Intent profileScreenIntent = new Intent(DataScreenActivity.this, ProfileActivity.class);
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
        // Handle navigation view item clicks here.

        navBurger.navigateTo(item, DataScreenActivity.this);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
